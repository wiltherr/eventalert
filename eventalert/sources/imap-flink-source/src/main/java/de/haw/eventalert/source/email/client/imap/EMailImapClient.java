package de.haw.eventalert.source.email.client.imap;

/**
 * Created by Tim on 18.08.2017.
 */

import com.sun.mail.imap.IMAPFolder;
import de.haw.eventalert.source.email.client.EMailClient;
import de.haw.eventalert.source.email.client.MessageConverter;
import de.haw.eventalert.source.email.client.exception.ExecutionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
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
    private Consumer<MailMessage> consumer;
    private volatile boolean isRunning;
    private String host;
    private int port;
    private String user;
    private String password;
    private String protocol;
    private String folderName;

    private transient Store store; //TODO get rid of tansient (?)
    private transient IMAPFolder folder;

    public EMailImapClient() {
    }

    @Override
    public void setConsumer(Consumer<MailMessage> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void init(String host, int port, boolean isSSL, String userName, String userPassword, String folderName) {
        this.host = host;
        this.port = port;

        if (isSSL)
            protocol = "imaps";
        else
            protocol = "imap";

        this.user = userName;
        this.password = userPassword;
        this.folderName = folderName;
    }

    @Override
    public void runClient() throws ExecutionFailedException, UserAuthFailedException {
        if (consumer == null) throw new IllegalStateException("consumer have to be set before run!");
        isRunning = true;
        try {
            connectStore();
            LOG.info("Client runs.");
            while (isRunning) { //TODO stable connection that dont close automaticcly when no new messages are received
                if (!store.isConnected())
                    connectStore();
                if (!folder.isOpen())
                    openFolder();
                //folder.idle(); TODO this is not supported by greenMail. but maybe its not needed either?
            }
        } catch (MessagingException e) {
            LOG.error("running failed! maybe lost connection to host. {}", e.getMessage(), e);
            throw new ExecutionFailedException("client failed while running, maybe lost connection to host", e);
        }
        isRunning = false;
        disconnect();
    }

    @Override
    public void cancel() {
        LOG.info("Client was cancelled!");
        this.isRunning = false;
        //this.disconnect();
    }

    private void connectStore() throws ExecutionFailedException, UserAuthFailedException, MessagingException {
        LOG.info("Client connects to IMAP Server.");

        if (store == null)
            createStore(); //create store if not exists

        //try to connectStore several times
        int retryCount = 0;
        while (!store.isConnected() && isRunning && retryCount < CONNECT_RETRY_COUNT_MAX) {
            retryCount++;
            try {
                LOG.debug("Try to connectStore. Attempt {} of {}", retryCount, CONNECT_RETRY_COUNT_MAX);
                //connectStore with user data
                store.connect(host, port, user, password);
            } catch (AuthenticationFailedException e) {
                LOG.error("authentication failed! maybe user login is invalid. host={}:{}, user={}, folderName={}", host, port, user, folderName, e);
                throw new UserAuthFailedException("Server was reachable, but authentication failed: " + e.getMessage(), e);
            } catch (MessagingException e) {
                LOG.debug("Connection failed: {}", e.getMessage(), e);
                LOG.debug("Waiting {}ms for next retry.", CONNECT_RETRY_WAIT_MS);

                try {
                    //wait for the next retry
                    TimeUnit.MILLISECONDS.sleep(CONNECT_RETRY_WAIT_MS);
                } catch (InterruptedException e1) {
                    LOG.error("Client was interrupted while waiting for next connection retry.", e);
                    throw new RuntimeException(e1);
                }
            }
        }

        if (store.isConnected()) {
            LOG.info("Connection was successfully established!");
        } else {
            LOG.error("Connection to IMAP server failed.");
            throw new ExecutionFailedException("could not connectStore to IMAP server.");
        }
        openFolder();
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
        if (consumer == null) throw new IllegalStateException("no consumer was set!");

        if (store != null && store.isConnected()) {
            //open folder if not opened or initialised
            if (folder == null || !folder.isOpen()) {
                //get user folder and open with read only access
                folder = (IMAPFolder) store.getFolder(folderName);
                folder.open(Folder.READ_ONLY);

                //connectStore listener on folder with consumer
                folder.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent e) {
                        LOG.debug("MessageCountEvent {} messages added", e.getMessages().length);
                        Stream.of(e.getMessages())
                                //Convert messages of messageCountEvent to MailMessages
                                .map(MessageConverter.toMailMessage)
                                //apply MailMessages on consumer
                                .forEach(consumer);
                    }
                });
            }
        }
    }

    private void disconnect() {
        LOG.info("Client disconnects.");
        try {
            closeFolder();
            LOG.info("Folder was closed successfully.");
        } catch (MessagingException e) {
            LOG.error("Error while closing folder!", e);
        }

        try {
            closeStore();
            LOG.info("Store was closed successfully.");
        } catch (MessagingException e) {
            LOG.error("Error while closing client!", e);
        }
    }

    private void closeFolder() throws MessagingException {
        if (folder != null && folder.isOpen()) {
            //folder.removeMessageCountListener(); TODO maybe this is needed?
            folder.close(false); //TODO was ist expunge?
        }
    }

    private void closeStore() throws MessagingException {
        if (store != null && store.isConnected())
            store.close();
    }
}