package de.haw.eventalert.core.producer.keyboard;

import de.haw.eventalert.core.consumer.action.Action;
import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.action.ledevent.LEDEventAction;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegment;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;
import de.haw.eventalert.ledbridge.entity.event.ColorSegmentationEvent;
import de.haw.eventalert.source.keyboard.KeyboardSourceFull;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class KeyboardLeftRight {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardLeftRight.class);
    private static final int NUM_SEGMENTS = 20;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<Integer> pressedKeyCodes = env.addSource(new KeyboardSourceFull())
                .filter(keyevent -> keyevent.getID() == NativeKeyEvent.NATIVE_KEY_PRESSED).map(NativeKeyEvent::getKeyCode);

        AtomicInteger currentPosition = new AtomicInteger(0);

        DataStream<Action> actionDataStream = pressedKeyCodes.flatMap((keyCode, out) -> {
            int current = currentPosition.get();
            if (keyCode == NativeKeyEvent.VC_LEFT) {
                if (current >= NUM_SEGMENTS) {
                    LOG.warn("max position reached: {}", current);
                } else {
                    out.collect(new LEDEventAction(getSegmentationEvent(0, currentPosition.getAndIncrement(), Colors.BLUE.getColor())));
                }
            } else if (keyCode == NativeKeyEvent.VC_RIGHT) {
                if (current <= 0) {
                    LOG.warn("min position reached: {}", current);
                } else {
                    out.collect(new LEDEventAction(getSegmentationEvent(currentPosition.getAndDecrement() - 1, NUM_SEGMENTS, Colors.OFF.getColor())));

                }
            }
            LOG.debug("Current Position: {}", currentPosition.get());
        });
        actionDataStream.print();
        actionDataStream.addSink(new ActionSink());

        env.execute();
    }

    private static ColorSegmentationEvent getSegmentationEvent(int segmentStart, int segmentEnd, de.haw.eventalert.ledbridge.entity.color.types.Color color) {
        ColorSegmentation colorSegmentation = ColorSegmentation.create(NUM_SEGMENTS);
        boolean segmentSet = colorSegmentation.setSegment(ColorSegment.create(color, segmentStart, segmentEnd));
        LOG.debug("segment: {}-{} / segmentSet: {}", segmentStart, segmentEnd, segmentSet);
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