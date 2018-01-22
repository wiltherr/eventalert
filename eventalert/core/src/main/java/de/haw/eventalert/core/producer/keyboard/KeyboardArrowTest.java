package de.haw.eventalert.core.producer.keyboard;

import de.haw.eventalert.core.consumer.action.Action;
import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.action.ledevent.LEDEventAction;

import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;

import de.haw.eventalert.ledbridge.entity.event.ColorSegmentationEvent;
import de.haw.eventalert.source.keyboard.KeyboardSourceFull;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class KeyboardArrowTest {

    private static final int NUM_SEGMENTS = 25;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<Integer> pressedKeyCodes = env.addSource(new KeyboardSourceFull())
                .filter(keyevent -> keyevent.getID() == NativeKeyEvent.NATIVE_KEY_PRESSED).map(NativeKeyEvent::getKeyCode);

        AtomicInteger currentPosition =  new AtomicInteger();

        DataStream<Action> actionDataStream = pressedKeyCodes.flatMap((keyCode, out) -> {
            if(currentPosition.get() <= 0) {
                return;
            }
            if (keyCode == NativeKeyEvent.VC_LEFT) {
                out.collect(new LEDEventAction(getSegmentationEvent(currentPosition.getAndIncrement(),Colors.BLUE.getColor())));
            } else if (keyCode == NativeKeyEvent.VC_RIGHT) {
                out.collect(new LEDEventAction(getSegmentationEvent(currentPosition.getAndDecrement(),Colors.OFF.getColor())));
            } else if (keyCode == NativeKeyEvent.VC_UP) {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.UP_EVENT.getEvent()));
            } else if (keyCode == NativeKeyEvent.VC_DOWN) {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.DOWN_EVENT.getEvent()));
            } else {
                out.collect(new LEDEventAction(KeyboardTest.ColorEvents.ANY_KEY_EVENT.getEvent()));
            }
        });
        actionDataStream.print();
        actionDataStream.addSink(new ActionSink());

        env.execute();
    }

    private static ColorSegmentationEvent getSegmentationEvent(int segmentPosition, de.haw.eventalert.ledbridge.entity.color.types.Color color) {
        ColorSegmentation colorSegmentation = ColorSegmentation.create(NUM_SEGMENTS);
        colorSegmentation.setSegment(segmentPosition, color);
        ColorSegmentationEvent colorSegmentationEvent = new ColorSegmentationEvent();
        colorSegmentationEvent.setNullColorIntentionally(false);
        colorSegmentationEvent.setColorSegmentation(colorSegmentation);
        return colorSegmentationEvent;
    }

    public enum Colors {
        BLUE(de.haw.eventalert.ledbridge.entity.color.Colors.createRGBW(0,0,255,0)),
        RED(de.haw.eventalert.ledbridge.entity.color.Colors.createRGBW(255,0,0,0)),
        OFF(de.haw.eventalert.ledbridge.entity.color.Colors.createRGBW(0,0,0,0))
        ;

        private de.haw.eventalert.ledbridge.entity.color.types.Color color;

        Colors(de.haw.eventalert.ledbridge.entity.color.types.Color color) {
            this.color = color;
        }

        public de.haw.eventalert.ledbridge.entity.color.types.Color getColor() {
            return color;
        }
    }
}