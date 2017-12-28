package de.haw.eventalert.source.email.client.imap;

/**
 * Created by Tim on 18.08.2017.
 */

import com.sun.mail.imap.IMAPFolder;
import de.haw.eventalert.source.email.client.EMailClient;
import de.haw.eventalert.source.email.client.MessageConverter;
import de.haw.eventalert.source.email.client.exception.ConnectionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Tim on 19.04.2017.
 * <p>
 * vorlage: http://www.programcreek.com/java-api-examples/index.php?source_dir=tradeframework-master/event-trader/src/main/java/com/jgoetsch/eventtrader/source/IMAPMsgSource.java
 */
public class EMailImapClient implements EMailClient {

    private static final Logger LOG = LoggerFactory.getLogger(EMailImapClient.class);
    private static final int CONNECT_RETRY_COUNT_MAX = 5;
    private static final long CONNECT_RETRY_WAIT_MS = 1500;
    private final Object waitStartupMonitor = new Object[0]; //wait object has to be serializable
    private volatile boolean isRunning;
    private String host;
    private int port;
    private String user;
    private String password;
    private String protocol;
    private String folderName;

    private transient Store store; //TODO get rid of tansient (?)
    private transient IMAPFolder folder;

    private transient MessageCountListener listener;

    public EMailImapClient() {
    }

    @Override
    public void setConsumer(Consumer<MailMessage> consumer) {
        this.listener = new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                LOG.trace("MessageCountEvent {} messages added", e.getMessages().length);
                Stream.of(e.getMessages())
                        //Convert messages of messageCountEvent to MailMessages
                        .map(MessageConverter.toMailMessage)
                        //apply MailMessages on consumer
                        .forEach(consumer);
            }
        };
    }

    @Override
    public void setConfiguration(EMailSourceConfiguration configuration) {
        this.host = configuration.getHost();
        this.port = configuration.getPort();
        //ssl need imaps protocol
        protocol = configuration.isSecure() ? "imaps" : "imap";
        this.user = configuration.getUser();
        this.password = configuration.getPassword();
        this.folderName = configuration.getFolder();
        LOG.debug("Client was initialised.");
    }

    @Override
    public void runClient() throws UserAuthFailedException, ConnectionFailedException {
        if (listener == null) throw new IllegalStateException("A consumer has to be set before running client!");
        long startTime = System.currentTimeMillis();
        isRunning = true;
        connect();
        //notfiy start
        synchronized (waitStartupMonitor) {
            waitStartupMonitor.notifyAll();
        }

        LOG.info("Client started. Startup time: {}ms", System.currentTimeMillis() - startTime);
        while (isRunning) { //TODO stable connection that don't close automatically when no new messages are received
            if (!store.isConnected() || !folder.isOpen())
                reconnect();
            //folder.idle(); TODO this is not supported by greenMail. but maybe its not needed either?
        }
        LOG.info("Client stopped.");
    }

    @Override
    public boolean waitStartup(long timeoutMillis) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        synchronized (waitStartupMonitor) {
            while (!isRunning && System.currentTimeMillis() - startTime < timeoutMillis) {
                LOG.debug("Wait for startup, timeout: {}ms", timeoutMillis);
                waitStartupMonitor.wait(timeoutMillis);
            }
            if (isRunning)
                LOG.debug("Wait for startup completed. Waited {}ms.", System.currentTimeMillis() - startTime);
            else
                LOG.error("Timeout for startup exceeded. Server was not started.");
        }
        return isRunning;
    }

    @Override
    public void cancel() {
        LOG.info("Client was cancelled!");
        this.isRunning = false;
        disconnect();
    }

    private void connect() throws UserAuthFailedException, ConnectionFailedException {
        LOG.info("Client connects to IMAP Server.");

        if (store == null)
            createStore(); //create store if not exists

        if (!store.isConnected()) {
            try {
                //connect with user data
                store.connect(host, port, user, password);
            } catch (AuthenticationFailedException e) {
                LOG.error("Authentication failed! maybe user login is invalid. user={}", user);
                throw new UserAuthFailedException("Server was reachable, but authentication failed: " + e.getMessage(), e);
            } catch (MessagingException e) {
                LOG.error("Connection to IMAP server (host={}:{}) failed: {}", host, port, e.getMessage());
                throw new ConnectionFailedException("connecting to server failed: " + e.getMessage(), e);
            }
        }

        if (store.isConnected()) {
            LOG.debug("Connection to IMAP server was successfully established!");
        } else {
            LOG.error("Connection to IMAP server (host={}:{}) failed.", host, port);
            throw new ConnectionFailedException("could not connect to IMAP server.");
        }

        try {
            openFolder();
            LOG.debug("Folder was successfully opened!");
        } catch (MessagingException e) {
            LOG.error("Opening folder {} failed: {}", folderName, e.getMessage());
            throw new ConnectionFailedException("Open folder failed: " + e.getMessage());
        }
    }

    private void disconnect() {
        LOG.info("Client disconnects.");
        try {
            closeFolder();
        } catch (MessagingException e) {
            LOG.error("Error while closing folder!", e);
        }

        try {
            closeStore();
        } catch (MessagingException e) {
            LOG.error("Error while closing client!", e);
        }
    }

    private void reconnect() throws ConnectionFailedException {
        //try to connect several times
        int retryCount = 0;
        while (isRunning && retryCount < CONNECT_RETRY_COUNT_MAX && (!store.isConnected() || !folder.isOpen())) {
            retryCount++;
            try {
                LOG.info("Client reconnects. Attempt {} of {}", retryCount, CONNECT_RETRY_COUNT_MAX);
                connect();
                return;
            } catch (ConnectionFailedException | UserAuthFailedException e) {
                LOG.info("Waiting {}ms for next reconnect try.", CONNECT_RETRY_WAIT_MS);
                try {
                    //wait for the next retry
                    TimeUnit.MILLISECONDS.sleep(CONNECT_RETRY_WAIT_MS);
                } catch (InterruptedException e1) {
                    LOG.error("Client was interrupted while waiting for next connection retry.", e);
                    throw new RuntimeException(e1); //can be thrown if client is cancelled while reconnecting.
                }
            }
        }
        throw new ConnectionFailedException("reconnecting failed!");
    }

    private void createStore() {
        try {
            //create a new session
            final Properties props = new Properties();
            props.setProperty("mail.store.protocol", protocol);
            Session session = Session.getDefaultInstance(props);
            //create store
            store = session.getStore(protocol);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("mail store protocol '" + protocol + "' is invalid!");
        }
    }

    /**
     * opens the folder on storage and connects the consumer with a {@link MessageCountAdapter}
     * <p>
     * will converts also convert the javax {@link Message} to {@link MailMessage}
     *
     * @throws MessagingException    can be thrown if folder name or store is invalid
     * @throws IllegalStateException can be thrown if no consumer was set
     */
    private void openFolder() throws MessagingException, IllegalStateException {
        //if (store == null) throw new IllegalStateException("store was not initialised!");
        if (listener == null) throw new IllegalStateException("no consumer was set!");

        if (store != null && store.isConnected()) {
            //open folder if not opened or initialised
            if (folder == null || !folder.isOpen()) {
                //get user folder and open with read only access
                folder = (IMAPFolder) store.getFolder(folderName);
                folder.open(Folder.READ_ONLY);

                //connect listener on folder with consumer
                folder.addMessageCountListener(listener);
            }
        }
    }

    private void closeFolder() throws MessagingException {
        if (folder != null && folder.isOpen()) {
            folder.removeMessageCountListener(listener);
            //folder.removeMessageCountListener(); TODO maybe this is needed?
            folder.close(false); //TODO was ist expunge?
            LOG.debug("Folder was closed successfully.");
        } else {
            LOG.warn("Called closeFolder on a already closed folder.");
        }
    }

    private void closeStore() throws MessagingException {
        if (store != null && store.isConnected()) {
            store.close();
            LOG.debug("Store was closed successfully.");
        } else {
            LOG.warn("Called closeStore on a already closed store.");
        }
    }
}