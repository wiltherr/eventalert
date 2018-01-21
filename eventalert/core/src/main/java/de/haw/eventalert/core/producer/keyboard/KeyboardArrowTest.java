package de.haw.eventalert.core.producer.keyboard;

import de.haw.eventalert.core.consumer.action.Action;
import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.action.ledevent.LEDEventAction;
import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.types.RGB;
import de.haw.eventalert.ledbridge.entity.event.ColorSegmentationEvent;
import de.haw.eventalert.source.keyboard.KeyboardSourceFull;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;

public class KeyboardArrowTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<Integer> pressedKeyCodes = env.addSource(new KeyboardSourceFull()).filter(keyevent -> keyevent.getID() == NativeKeyEvent.NATIVE_KEY_PRESSED).map(NativeKeyEvent::getKeyCode);
        DataStream<Action> actionDataStream = pressedKeyCodes.flatMap((keyCode, out) -> {
            if (keyCode == NativeKeyEvent.VC_LEFT) {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.LEFT_EVENT.getEvent()));
            } else if (keyCode == NativeKeyEvent.VC_RIGHT) {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.RIGHT_EVENT.getEvent()));
            } else if (keyCode == NativeKeyEvent.VC_UP) {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.UP_EVENT.getEvent()));
            } else if (keyCode == NativeKeyEvent.VC_DOWN) {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.DOWN_EVENT.getEvent()));
            } else {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.ANY_KEY_EVENT.getEvent()));
            }
        });
        actionDataStream.addSink(new ActionSink());

        env.execute();
    }

    public enum ColorEvents {
        LEFT_EVENT(255, 25, Colors.createRGB(Color.ORANGE)),
        RIGHT_EVENT(255, 25, Colors.createRGB(Color.GREEN)),
        UP_EVENT(255, 25, Colors.createRGBW(Color.GRAY, 50)),
        DOWN_EVENT(255, 25, Colors.createRGB(Color.MAGENTA)),
        ANY_KEY_EVENT(10, 5, Colors.createRGB(Color.cyan));

        private ColorSegmentationEvent event;

        ColorEvents(int start, long end, RGB color) {
            this.event = new ColorSegmentationEvent();
//            this.event.setColorSegmentation(Co);
//            this.event.setBrightness(brightness);
//            this.event.setDuration(duration);
//            this.event.setColor(color);
        }

        ColorSegmentationEvent getEvent() {
            return event;
        }
    }
}