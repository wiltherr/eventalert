package de.haw.eventalert.core;

import de.haw.eventalert.core.consumer.AlertEventConsumerJob;
import de.haw.eventalert.core.producer.email.EMailAlertEventProducerJob;
import de.haw.eventalert.core.producer.telegram.TelegramAlertEventProducerJob;

public class Launcher {

    public static void main(String... args) throws Exception {

        if (args[0].startsWith("job")) {
            switch (args[0]) {
                case "job:consumer":
                    AlertEventConsumerJob.main(args);
                    return;
                case "job:producer:email":
                    EMailAlertEventProducerJob.main(args);
                    return;
                case "job:producer:telegram":
                    TelegramAlertEventProducerJob.main(args);
                    return;
            }

        }
    }
}