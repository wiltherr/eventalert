package de.haw.eventalert.source.email.client.imap;

/**
 * Created by Tim on 18.08.2017.
 */

import com.sun.mail.imap.IMAPFolder;
import de.haw.eventalert.source.email.client.EMailClient;
import de.haw.eventalert.source.email.client.MessageConverter;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Tim on 19.04.2017.
 * <p>
 * vorlage: http://www.programcreek.com/java-api-examples/index.php?source_dir=tradeframework-master/event-trader/src/main/java/com/jgoetsch/eventtrader/source/IMAPMsgSource.java
 */
public class EMailImapClient implements EMailClient {

    private static final Logger LOG = LoggerFactory.getLogger(EMailImapClient.class);
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
    public void init(String protocol, String host, int port, String userName, String userPassword, String folderName) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.user = userName;
        this.password = userPassword;
        this.folderName = folderName;
    }

    @Override
    public void login() throws EMailSourceClientLoginFailedException {
        if (consumer == null) throw new IllegalStateException("consumer have to be set before login!");
        try {
            connect();
        } catch (AuthenticationFailedException e) {
            LOG.error("authentication failed! maybe user login is invalid. host={}:{}, user={}, folderName={}", host, port, user, folderName, e);
            throw new EMailSourceClientLoginFailedException("user authentication failed", e);
        } catch (MessagingException e) {
            LOG.error("login failed! maybe connection to host failed or user data is invalid. host={}:{}, user{}, folderName={}", host, port, user, folderName, e);
            throw new EMailSourceClientLoginFailedException("login failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void runClient() throws EMailSourceClientExecutionException {
        LOG.info("EMailImapClient runs");
        if (store == null) throw new IllegalStateException("client is not logged in!");
        if (consumer == null) throw new IllegalStateException("consumer have to be set before run!");
        isRunning = true;

        while (isRunning) { //TODO stable connection that dont close automaticcly when no new messages are received
            try {
                if (!store.isConnected())
                    connect();
                if (!folder.isOpen())
                    openFolder();
                //folder.idle(); TODO this is not supported by greenMail. but maybe its not needed either?
            } catch (MessagingException e) {
                LOG.error("running failed! maybe lost connection to host. {}", e.getMessage(), e);
                throw new EMailSourceClientExecutionException("client failed  while running, maybe lost connection to host", e);
            }
        }
        isRunning = false;
    }

    @Override
    public void cancel() {
        this.isRunning = false;
        LOG.info("client was cancelled!");
        try {
            disconnect();
            LOG.info("client disconnected successfully.");
        } catch (MessagingException e) {
            LOG.error("Error disconnecting!", e);
        }
    }

    private void connect() throws MessagingException {
        LOG.info("EMailImapClient (re)connects");

        //create store if not exists
        if (store == null) {
            //create a new session
            final Properties props = new Properties();
            props.setProperty("mail.store.protocol", protocol); //TODO imap protokoll oder imaps (ssl) und auch pop3 müssen gehen
            Session session = Session.getDefaultInstance(props);
            //create store
            store = session.getStore();
        }

        //connect if not connected
        if (!store.isConnected()) {
            //connect with user data
            store.connect(host, port, user, password);
        }

        openFolder();
    }

    /**
     * opens the folder on storage and connects the consumer with a {@link MessageCountAdapter}
     * <p>
     * will converts also convert the javax {@link Message} to {@link MailMessage}
     *
     * @throws MessagingException    can be thrown if folder name or store is invalid
     * @throws IllegalStateException can be thrown if store was not initialised or no consumer was set.
     */
    private void openFolder() throws MessagingException, IllegalStateException {
        if (store == null) throw new IllegalStateException("store was not initialised!");
        if (consumer == null) throw new IllegalStateException("no consumer was set!");

        if (store.isConnected()) {
            //open folder if not opened or initialised
            if (folder == null || !folder.isOpen()) {
                //get user folder and open with read only access
                folder = (IMAPFolder) store.getFolder(folderName);
                folder.open(Folder.READ_ONLY);

                //connect listener on folder with consumer
                folder.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent e) {
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

    private void disconnect() throws MessagingException {
        if (folder != null && folder.isOpen()) {
            //folder.removeMessageCountListener(); TODO maybe this is needed?
            folder.close(false); //TODO was ist expunge?
        }
        if (store != null)
            store.close();
    }
}