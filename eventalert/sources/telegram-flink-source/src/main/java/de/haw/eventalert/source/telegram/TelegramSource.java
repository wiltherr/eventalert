package de.haw.eventalert.source.telegram;

import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import de.haw.eventalert.source.telegram.api.auth.util.TelegramAuthenticationFileUtil;
import de.haw.eventalert.source.telegram.client.TelegramAbstractEvent;
import de.haw.eventalert.source.telegram.client.TelegramEventCallback;
import de.haw.eventalert.source.telegram.client.TelegramMessageEvent;
import de.haw.eventalert.source.telegram.client.TelegramUpdateClient;
import de.haw.eventalert.source.telegram.util.PropertyUtil;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static de.haw.eventalert.source.telegram.CreateAuthenticationTool.DEFAULT_API_AUTH_KEY_FILE_NAME;

public class TelegramSource implements SourceFunction<TelegramMessageEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramSource.class);

    private TelegramUpdateClient telegramUpdateClient;

    public TelegramSource() {
        this(null);
    }

    public TelegramSource(TelegramAuthentication telegramAuthentication) {
        if (telegramAuthentication == null) {
            try {
                telegramAuthentication = TelegramAuthenticationFileUtil.readFromFile(
                        Paths.get(CreateAuthenticationTool.class.getClassLoader().getResource(DEFAULT_API_AUTH_KEY_FILE_NAME).toURI())
                );
            } catch (URISyntaxException | IOException e) {
                throw new IllegalArgumentException(DEFAULT_API_AUTH_KEY_FILE_NAME + " file can not be found. please check telegram-flink-source readme!", e);
            }
        }

        try {
            ApiConfiguration apiConf = ApiConfiguration.fromProperties(PropertyUtil.getApiProperties());
            this.telegramUpdateClient = new TelegramUpdateClient(apiConf, telegramAuthentication);
        } catch (IOException e) {
            throw new IllegalArgumentException("telegram-api properties can not be found. please check telegram-flink-source readme!", e);
        }
    }


    @Override
    public void run(SourceContext<TelegramMessageEvent> ctx) throws Exception {
        telegramUpdateClient.setListener(new TelegramEventCallback.TelegramEventListener() {
            @Override
            public void onMessage(TelegramAbstractEvent telegramAbstractEvent) {
                LOG.debug("received new abstract event: {}", telegramAbstractEvent);
            }

            @Override
            public void onMessage(TelegramMessageEvent telegramMessageEvent) {
                LOG.debug("received new event: {}", telegramMessageEvent);
                ctx.collect(telegramMessageEvent);
            }
        });
        telegramUpdateClient.start();
    }

    @Override
    public void cancel() {
        telegramUpdateClient.stop();
    }
}
