package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

public class NotFoundErrorException extends TelegramApiFatalErrorException {
    NotFoundErrorException(RpcErrorException rpcError) {
        super("requested telegram api method not found! ", rpcError);
    }
}
