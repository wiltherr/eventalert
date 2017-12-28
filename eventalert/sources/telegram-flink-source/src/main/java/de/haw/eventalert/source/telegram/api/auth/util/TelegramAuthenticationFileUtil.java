package de.haw.eventalert.source.telegram.api.auth.util;

import com.github.badoualy.telegram.mtproto.model.DataCenter;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthStorage;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import org.apache.commons.io.FileUtils;
import org.apache.flink.shaded.com.google.common.base.Splitter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 * util class to export {@link TelegramAuthStorage}
 */
public class TelegramAuthenticationFileUtil {

    private static final Charset AUTH_FILE_CHARSET = StandardCharsets.UTF_8;
    private static final char SPLIT_CHAR = ':';

    /**
     * writes {@link TelegramAuthentication} to given filePath.
     * new file will be created if it does not exists
     * exiting file will be overwritten
     *
     * @param filePath               destination path with file name. will be overwritten or created.
     * @param telegramAuthentication authentication which is written to filePath
     */
    public static void writeToFile(Path filePath, TelegramAuthentication telegramAuthentication) throws IOException {
        String dataContent = telegramAuthentication.getDataCenter().getIp() + SPLIT_CHAR
                + telegramAuthentication.getDataCenter().getPort() + SPLIT_CHAR
                + Base64.getEncoder().encodeToString(telegramAuthentication.getAuthKey());
        FileUtils.writeStringToFile(filePath.toFile(), dataContent);
    }

    //TODO javadoc
    public static TelegramAuthentication readFromFile(Path filePath) throws IOException {
        String content = FileUtils.readFileToString(filePath.toFile());
        List<String> contentSplit = Splitter.on(SPLIT_CHAR).limit(3).splitToList(content);
        DataCenter dataCenter = new DataCenter(contentSplit.get(0), Integer.parseInt(contentSplit.get(1)));
        byte[] authKeyBytes = Base64.getDecoder().decode(contentSplit.get(2));
        return new TelegramAuthentication(authKeyBytes, dataCenter);
    }
}
