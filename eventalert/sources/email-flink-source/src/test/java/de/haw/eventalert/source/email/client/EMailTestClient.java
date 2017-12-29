package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.exception.ConnectionFailedException;
import de.haw.eventalert.source.email.client.exception.ExecutionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;

import java.util.function.Consumer;

/**
 * Created by Tim on 10.12.2017.
 * {@link EMailClient} container to implement {@link Runnable} interface for test cases.
 */
class EMailTestClient implements EMailClient, Runnable {

    private final EMailClient client;

    public EMailTestClient(EMailClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            client.runClient();
        } catch (ExecutionFailedException | UserAuthFailedException | ConnectionFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConsumer(Consumer<MailMessage> consumer) {
        client.setConsumer(consumer);
    }

    @Override
    public void setConfiguration(EMailSourceConfiguration configuration) {
        client.setConfiguration(configuration);
    }

    @Override
    public void runClient() throws ExecutionFailedException, UserAuthFailedException, ConnectionFailedException {
        client.runClient();
    }

    @Override
    public boolean waitStartup(long timeoutMillis) throws InterruptedException {
        return client.waitStartup(timeoutMillis);
    }


    @Override
    public void cancel() {
        client.cancel();
    }
}
