package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;

public class PhoneNumberInvalidErrorException extends BadRequestErrorException {
    PhoneNumberInvalidErrorException(RpcErrorException rpcError) {
        super("The phone number is invalid", rpcError);
    }
}
