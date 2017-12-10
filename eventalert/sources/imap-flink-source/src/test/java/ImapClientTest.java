import de.haw.eventalert.source.email.client.ImapClient;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImapClientTest {

    private ImapClient imapClient;

    @BeforeEach
    public void setUp() {
        imapClient = new ImapClient();
        imapClient.init("", 993, "", "", "");
    }

    @Test
    public void testConsumer() throws EMailSourceClientExecutionException, EMailSourceClientLoginFailedException {
        imapClient.setConsumer(e -> System.out.println(e.getContent()));
        imapClient.login();
        imapClient.run();
    }
}
