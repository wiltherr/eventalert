package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

public class FloodErrorException extends TelegramApiFatalErrorException {
    FloodErrorException(RpcErrorException rpcError) {
        super("fatal telegram api error! maximum allowed number of requests to called api method exceeded! " +
                "wait " + rpcError.getTagInteger() + " seconds for next request to this method.", rpcError);
    }
}
