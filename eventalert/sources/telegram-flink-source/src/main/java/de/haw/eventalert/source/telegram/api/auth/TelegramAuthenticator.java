package de.haw.eventalert.source.telegram.api.auth;

import com.github.badoualy.telegram.api.Kotlogram;
import com.github.badoualy.telegram.api.TelegramApp;
import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.tl.api.TLUser;
import com.github.badoualy.telegram.tl.api.auth.TLAuthorization;
import com.github.badoualy.telegram.tl.api.auth.TLSentCode;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;
import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.exception.AuthProcessAlreadyStartedException;
import de.haw.eventalert.source.telegram.api.auth.exception.AuthenticationCodeNullOrEmptyException;
import de.haw.eventalert.source.telegram.api.auth.exception.PhoneNumberInvalidException;
import de.haw.eventalert.source.telegram.api.auth.exception.PhoneNumberNullOrEmptyException;
import de.haw.eventalert.source.telegram.api.exception.PhoneNumberInvalidErrorException;
import de.haw.eventalert.source.telegram.api.exception.RpcErrorHandler;
import de.haw.eventalert.source.telegram.api.exception.TelegramApiErrorException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * used to create a new authentication
 * <p>
 * can be used only for one single telegram authentication
 */
public class TelegramAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramAuthenticator.class);

    final private TelegramClient tgClient;
    private TelegramAuthStorage tgAuthStorage;
    private boolean isInAuthGenerationProcess = false;

    public TelegramAuthenticator(ApiConfiguration apiConf) {
        TelegramApp tgApp = new TelegramApp(apiConf.getApiId(), apiConf.getApiHash(), apiConf.getAppModel(),
                apiConf.getAppSystemVersion(), apiConf.getAppVersion(), apiConf.getAppLangCode());
        //create new tgAuthStorage
        tgAuthStorage = new TelegramAuthStorage();
        //create new client
        tgClient = Kotlogram.getDefaultClient(tgApp, tgAuthStorage, apiConf.getDefaultDataCenter());
    }

    /**
     * starts the {@link AuthGenerator}, which is used to generate a new {@link TelegramAuthentication} by calling its {@link AuthGenerator#generateAuthentication(String)} generate authentication method}.
     * <p>
     * will send a code via sms or telegram to phoneNumber. {@link AuthGenerator}!
     * <p>
     * while authentication process @{@link Kotlogram} will fill the empty tgAuthStorage automatically
     *
     * @param phoneNumber valid phone number of telegram account including country specific prefix
     * @return AuthGenerator process to create a new telegram authentication with the code sent to phoneNumber
     * @throws PhoneNumberNullOrEmptyException    will be thrown if {@code phoneNumber} is null or empty
     * @throws PhoneNumberInvalidException        will be thrown if telegram api does not except {@code phoneNumber}
     * @throws TelegramApiErrorException          will be thrown if any other api error happens
     * @throws AuthProcessAlreadyStartedException will be thrown if a {@link AuthGenerator} was created already
     */
    public AuthGenerator startAuthGenerator(String phoneNumber) throws PhoneNumberNullOrEmptyException, PhoneNumberInvalidException, TelegramApiErrorException {
        if (isInAuthGenerationProcess) {
            throw new AuthProcessAlreadyStartedException();
        }
        if (StringUtils.isBlank(phoneNumber)) {
            throw new PhoneNumberNullOrEmptyException();
        }

        try {
            TLSentCode tlSentCode = tgClient.authSendCode(false, phoneNumber, true);
            return new AuthGenerator(tlSentCode, phoneNumber);
        } catch (RpcErrorException e) {
            TelegramApiErrorException apiError = RpcErrorHandler.handle(e);
            if (apiError instanceof PhoneNumberInvalidErrorException) {
                throw new PhoneNumberInvalidException();
            } else {
                throw apiError;
            }
        } catch (IOException e) {
            return null; //TODO catch exception
        }
    }

    /**
     * close {@link TelegramAuthenticator}
     * call this method if authentication was successful
     * will delete volatile {@link TelegramAuthStorage} and shutdown {@link TelegramClient}
     */
    public void close() {
        if (tgClient != null && !tgClient.isClosed()) {
            tgClient.close(true);
        }
        tgAuthStorage.deleteAuthKey();
        tgAuthStorage.deleteDc();
    }

    /**
     * wraps {@link TLSentCode} and a phone number
     * used to create new {@link TelegramAuthentication}
     *
     * @see TelegramAuthenticator#startAuthGenerator(String)
     */
    public class AuthGenerator {
        private TLSentCode tlSendCode;
        private String phoneNumber;

        AuthGenerator(TLSentCode tlSendCode, String phoneNumber) {
            this.tlSendCode = tlSendCode;
            this.phoneNumber = phoneNumber;
            TelegramAuthenticator.this.isInAuthGenerationProcess = true;
        }

        /**
         * generates a new {@link TelegramAuthentication} with authenticationCode received via telegram or sms
         * <p>
         * start a {@link AuthGenerator} with {@link AuthGenerator#startAuthGenerator(String)}
         *
         * @param authenticationCode was received via telegram or sms by {@link #startAuthGenerator(String) starting auth generator}
         * @return a new telegramAuthentication if success or null
         * @throws TelegramApiErrorException              if {@code authenticationCode} if authenticationCode is refused by api or other api errors happens
         * @throws AuthenticationCodeNullOrEmptyException if {@code authenticationCode} is blank or null
         */
        public TelegramAuthentication generateAuthentication(String authenticationCode) throws AuthenticationCodeNullOrEmptyException, TelegramApiErrorException {
            if (StringUtils.isBlank(authenticationCode)) {
                throw new AuthenticationCodeNullOrEmptyException();
            }

            try {
                TLAuthorization tlAuthorization = TelegramAuthenticator.this.tgClient.authSignIn(phoneNumber, tlSendCode.getPhoneCodeHash(), authenticationCode);
                //check if user name is in authorization (if not it is invalid)
                TLUser self = tlAuthorization.getUser().getAsUser();
                if (self != null && self.getId() != 0) {
                    //leave authGenerationProcess
                    TelegramAuthenticator.this.isInAuthGenerationProcess = false;
                    //return authentication as TelegramAuthentication (tgAuthStorage was set by Kotlogram automatically)
                    return TelegramAuthenticator.this.tgAuthStorage.export();
                }
            } catch (RpcErrorException e) {
                throw RpcErrorHandler.handle(e);
            } catch (IOException e) {
                return null;
            }
            return null;
        }
    }

}
