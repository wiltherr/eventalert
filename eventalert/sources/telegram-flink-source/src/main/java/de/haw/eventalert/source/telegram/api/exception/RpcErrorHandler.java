package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

/**
 * handles errors from Telegram API (thrown as {@link RpcErrorException}
 * by {@link com.github.badoualy.telegram.api.Kotlogram})
 * <p>
 * a full list of all error codes and type-tags are available on telegram api page
 *
 * @see <a href="https://core.telegram.org/api/errors">https://core.telegram.org/api/errors</a>
 */
public class RpcErrorHandler {

    //telegram api error codes
    private static final int BAD_REQUEST_ERROR_CODE = 400;
    private static final int NOT_FOUND_ERROR_CODE = 404;
    private static final int FLOOD_ERROR_CODE = 420;
    //bad request (400) telegram api error tags
    private static final String PHONE_NUMBER_INVALID = "PHONE_NUMBER_INVALID";

    /**
     * handles {@link RpcErrorException} by returning a more specific {@link TelegramApiErrorException}
     * or throwing {@link TelegramApiFatalErrorException} in case of fatal api errors
     *
     * @param rpcError from TelegramAPI thrown by {@link com.github.badoualy.telegram.api.Kotlogram}
     * @return a more specific {@link TelegramApiErrorException} with detailed message
     * @throws TelegramApiFatalErrorException in case of fatal api errors like 404 (NOT_FOUND) and 420 (FLOOD)
     */
    public static TelegramApiErrorException handle(RpcErrorException rpcError) throws TelegramApiFatalErrorException {
        switch (rpcError.getCode()) {
            case FLOOD_ERROR_CODE:
                throw handleFloodError(rpcError);
            case NOT_FOUND_ERROR_CODE:
                throw handleNotFoundError(rpcError);
            default:
                return handleApiError(rpcError);
        }
    }

    private static TelegramApiFatalErrorException handleNotFoundError(RpcErrorException rpcError) {
        return new NotFoundErrorException(rpcError);
    }

    private static FloodErrorException handleFloodError(RpcErrorException rpcError) {
        return new FloodErrorException(rpcError);
    }

    private static TelegramApiErrorException handleApiError(RpcErrorException rpcError) {
        switch (rpcError.getCode()) {
            case BAD_REQUEST_ERROR_CODE:
                return handleBadRequestError(rpcError);

            default:
                return new TelegramApiErrorException(rpcError);
        }
    }

    private static BadRequestErrorException handleBadRequestError(RpcErrorException rpcError) {
        switch (rpcError.getTag()) {
            case PHONE_NUMBER_INVALID:
                return new PhoneNumberInvalidErrorException(rpcError);
            default:
                return new BadRequestErrorException(rpcError);
        }
    }
}
