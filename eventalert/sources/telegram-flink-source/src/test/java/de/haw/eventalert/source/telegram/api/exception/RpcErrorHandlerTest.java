package de.haw.eventalert.source.telegram.api.exception;

import com.github.badoualy.telegram.tl.exception.RpcErrorException;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RpcErrorHandlerTest {

    //telegram api error codes
    private static final int BAD_REQUEST_ERROR_CODE = 400;
    private static final int NOT_FOUND_ERROR_CODE = 404;

    private static final int FLOOD_ERROR_CODE = 420;
    private static final String TEST_FLOOD_ERROR_TAG = "FLOOD_ERROR_458135";

    private static final int TEST_ERROR_CODE = 123;
    private static final String TEST_ERROR_TAG = "TEST_ERROR_TAG";

    @Test
    public void testBadRequestErrorException() {
        assertTrue(RpcErrorHandler.handle(new RpcErrorException(BAD_REQUEST_ERROR_CODE, TEST_ERROR_TAG)) instanceof BadRequestErrorException);
        assertTrue(RpcErrorHandler.handle(new RpcErrorException(BAD_REQUEST_ERROR_CODE, "PHONE_NUMBER_INVALID")) instanceof PhoneNumberInvalidErrorException);
    }

    @Test
    public void testFloodErrorException() {
        assertThrows(RuntimeException.class, () -> RpcErrorHandler.handle(new RpcErrorException(FLOOD_ERROR_CODE, TEST_FLOOD_ERROR_TAG)));
        assertThrows(TelegramApiFatalErrorException.class, () -> RpcErrorHandler.handle(new RpcErrorException(FLOOD_ERROR_CODE, TEST_FLOOD_ERROR_TAG)));
        assertThrows(FloodErrorException.class, () -> RpcErrorHandler.handle(new RpcErrorException(FLOOD_ERROR_CODE, TEST_FLOOD_ERROR_TAG)));
    }

    @Test
    public void testNotFoundException() {
        assertThrows(RuntimeException.class, () -> RpcErrorHandler.handle(new RpcErrorException(NOT_FOUND_ERROR_CODE, TEST_ERROR_TAG)));
        assertThrows(TelegramApiFatalErrorException.class, () -> RpcErrorHandler.handle(new RpcErrorException(NOT_FOUND_ERROR_CODE, TEST_ERROR_TAG)));
        assertThrows(NotFoundErrorException.class, () -> RpcErrorHandler.handle(new RpcErrorException(NOT_FOUND_ERROR_CODE, TEST_ERROR_TAG)));
    }

    @Test
    public void testUnspecificApiException() {
        assertThrows(TelegramApiErrorException.class, () -> RpcErrorHandler.handle(new RpcErrorException(TEST_ERROR_CODE, TEST_ERROR_TAG)));
    }
}
