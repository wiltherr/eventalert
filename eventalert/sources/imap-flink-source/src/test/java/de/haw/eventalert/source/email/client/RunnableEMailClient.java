package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;
import de.haw.eventalert.source.email.client.imap.EMailImapClient;
import de.haw.eventalert.source.email.entity.MailMessage;

import java.util.function.Consumer;

/**
 * Created by Tim on 10.12.2017.
 * wrapper for an {@link EMailClient} to implement {@link Runnable} interface
 */
class RunnableEMailClient implements EMailClient, Runnable  {

    private final EMailClient client;

    public RunnableEMailClient(EMailClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            client.runClient();
        } catch (EMailSourceClientExecutionException e) {
            this.cancel();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConsumer(Consumer<MailMessage> consumer) {
        client.setConsumer(consumer);
    }

    @Override
    public void init(String protocol, String host, int port, String userName, String userPassword, String folderName) {
        client.init(protocol, host, port, userName, userPassword, folderName);
    }

    @Override
    public void login() throws EMailSourceClientLoginFailedException {
        client.login();
    }

    @Override
    public void runClient() throws EMailSourceClientExecutionException {
        client.runClient();
    }

    @Override
    public void cancel() {
        client.cancel();
    }
}
