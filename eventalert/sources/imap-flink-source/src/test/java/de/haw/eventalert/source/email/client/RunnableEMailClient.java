package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.exception.ExecutionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;
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
        } catch (ExecutionFailedException | UserAuthFailedException e) {
            this.cancel();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConsumer(Consumer<MailMessage> consumer) {
        client.setConsumer(consumer);
    }

    @Override
    public void init(String host, int port, boolean isSSL, String userName, String userPassword, String folderName) {
        client.init(host, port, isSSL, userName, userPassword, folderName);
    }

    @Override
    public void runClient() throws ExecutionFailedException, UserAuthFailedException {
        client.runClient();
    }

    @Override
    public void cancel() {
        client.cancel();
    }
}
