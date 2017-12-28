package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

public class BadRequestErrorException extends TelegramApiErrorException {
    BadRequestErrorException(RpcErrorException parentRpcError) {
        super("unknown bad request exception", parentRpcError);
    }

    BadRequestErrorException(String message, RpcErrorException parentRpcError) {
        super(message, parentRpcError);
    }
}
