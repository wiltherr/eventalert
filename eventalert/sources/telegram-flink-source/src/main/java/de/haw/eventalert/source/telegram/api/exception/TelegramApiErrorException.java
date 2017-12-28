package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

public class TelegramApiErrorException extends Exception {

    TelegramApiErrorException(String message, RpcErrorException rpcError) {
        super(message, rpcError);
    }

    TelegramApiErrorException(RpcErrorException rpcError) {
        super("unknown telegram api error. Error code: " + rpcError.getCode() + "; Error type: " + rpcError.getTag() + ". " +
                "see https://core.telegram.org/api/errors for further information's", rpcError);
    }
}
