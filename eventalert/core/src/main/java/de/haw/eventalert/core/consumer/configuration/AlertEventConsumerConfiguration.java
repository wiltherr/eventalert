package de.haw.eventalert.core.consumer.configuration;

import de.haw.eventalert.core.consumer.action.Actions;
import de.haw.eventalert.core.consumer.filter.Condition;
import de.haw.eventalert.core.consumer.filter.SimpleFilterRule;
import de.haw.eventalert.core.consumer.filter.manager.FilterRuleManager;
import de.haw.eventalert.core.consumer.filter.manager.TransientFilterRuleManager;
import de.haw.eventalert.core.producer.twitter.disasteralert.DisasterAlert;
import de.haw.eventalert.ledbridge.entity.color.Brightness;
import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.event.TimedColorEvent;
import de.haw.eventalert.source.email.entity.MailMessage;

/**
 * Class is used to manage currently hardcoded FilterRules.
 * Can be replaced by a persistent FilterRuleManager when a Database support is added
 */
public class AlertEventConsumerConfiguration {
    public static final FilterRuleManager filterRuleManager;

    static {
        TransientFilterRuleManager manager = new TransientFilterRuleManager();
        //id of the target arduino LED
        long targetLEDId = 0;
        TimedColorEvent blueLEDEvent = new TimedColorEvent();
        blueLEDEvent.setColor(Colors.createRGBW(0, 0, 255, 0));
        blueLEDEvent.setBrightness(Brightness.MAX);
        blueLEDEvent.setDuration(100);
        blueLEDEvent.setTargetLEDId(targetLEDId);

        TimedColorEvent redLEDEvent = new TimedColorEvent();
        redLEDEvent.setColor(Colors.createRGBW(255, 0, 0, 0));
        redLEDEvent.setBrightness(Brightness.MAX);
        redLEDEvent.setDuration(100);
        redLEDEvent.setTargetLEDId(targetLEDId);

        TimedColorEvent greenLEDEvent = new TimedColorEvent();
        greenLEDEvent.setColor(Colors.createRGBW(0, 255, 0, 0));
        greenLEDEvent.setBrightness(Brightness.MAX);
        greenLEDEvent.setDuration(100);
        greenLEDEvent.setTargetLEDId(targetLEDId);

        TimedColorEvent longRedLEDEvent = new TimedColorEvent();
        longRedLEDEvent.setColor(Colors.createRGBW(0, 0, 255, 0));
        longRedLEDEvent.setBrightness(Brightness.MAX);
        longRedLEDEvent.setDuration(3600000);
        longRedLEDEvent.setTargetLEDId(targetLEDId);

        manager.addFilter(new SimpleFilterRule(MailMessage.EVENT_TYPE, "from", new Condition(Condition.Type.CONTAINS, "tim@ksit.org"), Actions.createLEDEventAction(greenLEDEvent), 0));
        manager.addFilter(new SimpleFilterRule(MailMessage.EVENT_TYPE, "to", new Condition(Condition.Type.CONTAINS, "wittler"), Actions.createLEDEventAction(redLEDEvent), 0));

        manager.addFilter(new SimpleFilterRule(DisasterAlert.EVENT_TYPE, "city", new Condition(Condition.Type.CONTAINS, "hamburg"), Actions.createLEDEventAction(longRedLEDEvent),
                Integer.MAX_VALUE)); //set priority to max integer, for very very important events

        filterRuleManager = manager;
    }

}
