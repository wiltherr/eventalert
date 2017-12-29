package de.haw.eventalert.source.telegram.client;

import com.github.badoualy.telegram.api.Kotlogram;
import com.github.badoualy.telegram.api.TelegramApiStorage;
import com.github.badoualy.telegram.api.TelegramApp;
import com.github.badoualy.telegram.api.TelegramClient;
import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthStorage;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 15.09.2017.
 */
public class TelegramUpdateClient implements Serializable {
    public static final Logger LOG = LoggerFactory.getLogger(TelegramUpdateClient.class);
    private final ApiConfiguration apiConf;
    private final TelegramAuthentication telegramAuth;

    private boolean isRunning = false;

    private TelegramClient client;
    private TelegramEventCallback.TelegramEventListener listener;

    public TelegramUpdateClient(ApiConfiguration apiConf, TelegramAuthentication telegramAuthentication) {
        this.apiConf = apiConf;
        this.telegramAuth = telegramAuthentication;
    }

    public void setListener(TelegramEventCallback.TelegramEventListener listener) {
        this.listener = listener;
    }

    public void start() {
        isRunning = true;
        run();
    }

    public void stop() {
        isRunning = false;
        LOG.info("TelegramUpdateClient will stop");
        client.close();
    }

    private void run() {
        if (listener == null) {
            throw new IllegalStateException("listener has to be set before run!");
        }

        TelegramApp application = new TelegramApp(apiConf.getApiId(), apiConf.getApiHash(), apiConf.getAppModel(),
                apiConf.getAppSystemVersion(), apiConf.getAppVersion(), apiConf.getAppLangCode());
        TelegramApiStorage apiStorage = new TelegramAuthStorage(telegramAuth);
        client = Kotlogram.getDefaultClient(application, apiStorage, apiStorage.loadDc(), new TelegramEventCallback(this.listener));
        LOG.info("TelegramUpdateClient was started");
        while (!client.isClosed()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                client.close();
            }
        }
        client.close();
        LOG.info("TelegramUpdateClient was is stopped");
    }


}
