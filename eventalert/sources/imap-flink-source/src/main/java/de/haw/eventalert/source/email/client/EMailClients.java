package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.imap.EMailImapClient;

/**
 * Created by Tim on 11.12.2017.
 * factory class for {@link EMailClient}
 */
public class EMailClients {
    public static EMailClient createImap() {
        return new EMailImapClient();
    }
}
