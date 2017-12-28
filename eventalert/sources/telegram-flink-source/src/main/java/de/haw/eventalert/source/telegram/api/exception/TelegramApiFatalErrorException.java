package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

public class TelegramApiFatalErrorException extends RuntimeException {
    TelegramApiFatalErrorException(String message, RpcErrorException rpcError) {
        super(message, rpcError);
    }
}
