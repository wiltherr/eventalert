package de.haw.eventalert.core.producer.keyboard;

import de.haw.eventalert.core.consumer.action.Action;
import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.action.ledevent.LEDEventAction;
import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegment;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.ColorSegmentationEvent;
import de.haw.eventalert.ledbridge.entity.event.DimEvent;
import de.haw.eventalert.source.keyboard.KeyboardSourceFull;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * f5 = mute
 * f6 = unmute
 * <p>
 * f2 or page up = less
 * f3 = more or page down
 * <p>
 * 1= less brightness
 * 2 = more brightness
 */
public class KeyboardLeftRight {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardLeftRight.class);
    private static final int NUM_SEGMENTS = 19;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<Integer> pressedKeyCodes = env.addSource(new KeyboardSourceFull())
                .filter(keyevent -> keyevent.getID() == NativeKeyEvent.NATIVE_KEY_PRESSED).map(NativeKeyEvent::getKeyCode);

        AtomicInteger brightness = new AtomicInteger(100);
        AtomicBoolean isMute = new AtomicBoolean(false);
        AtomicInteger currentPosition = new AtomicInteger(0);

        final Color[] STRIPED_COLORS = new Color[]{KeyboardColors.BLUE.getColor(), KeyboardColors.GREEN.getColor(), KeyboardColors.RED.getColor()};
        final Color[] HAW_COLORS = new Color[]{KeyboardColors.HAW_LIGHT_BLUE.getColor(), KeyboardColors.HAW_DARK_BLUE.getColor()};

        DataStream<Action> actionDataStream = pressedKeyCodes.flatMap((keyCode, out) -> {
            if (keyCode == NativeKeyEvent.VC_F6) { //unmute
                isMute.set(false);
                return;
            } else if (keyCode == NativeKeyEvent.VC_F5) { //mute
                isMute.set(true);
                return;
            }

            if (isMute.get())
                return;

            if (keyCode == NativeKeyEvent.VC_PAGE_DOWN || keyCode == NativeKeyEvent.VC_F3) {
                if (currentPosition.get() >= NUM_SEGMENTS) {
                    LOG.warn("max position reached: {}", currentPosition.get());
                } else {

                    //color
                    Color color = HAW_COLORS[currentPosition.get() % HAW_COLORS.length];

                    out.collect(new LEDEventAction(getSegmentationEvent(currentPosition.get(), currentPosition.getAndIncrement(), color)));
                }
            } else if (keyCode == NativeKeyEvent.VC_PAGE_UP || keyCode == NativeKeyEvent.VC_F2) {
                if (currentPosition.get() <= 0) {
                    LOG.warn("min position reached: {}", currentPosition.get());
                } else {
                    out.collect(new LEDEventAction(getSegmentationEvent(currentPosition.getAndDecrement() - 1, NUM_SEGMENTS, KeyboardColors.OFF.getColor())));

                }
            } else if (keyCode == NativeKeyEvent.VC_2) {
                int newBrightness = brightness.get() + 5;
                if (newBrightness > 255) {
                    LOG.warn("brightness max reached: {}", brightness.get());
                } else {
                    out.collect(new LEDEventAction(getDimEvent(newBrightness)));
                    brightness.set(newBrightness);
                }
            } else if (keyCode == NativeKeyEvent.VC_1) {
                int newBrightness = brightness.get() - 5;
                if (newBrightness <= 0) {
                    LOG.warn("brightness min reached: {}", brightness.get());
                } else {
                    out.collect(new LEDEventAction(getDimEvent(newBrightness)));
                    brightness.set(newBrightness);
                }
            }
            LOG.debug("Current Position: {}", currentPosition.get());
        });
        actionDataStream.print();
        actionDataStream.addSink(new ActionSink());

        env.execute();
    }

    private static DimEvent getDimEvent(int brightness) {
        DimEvent dimEvent = new DimEvent();
        dimEvent.setBrightness(brightness);
        return dimEvent;
    }

    private static ColorSegmentationEvent getSegmentationEvent(int segmentStart, int segmentEnd, Color color) {
        ColorSegmentation colorSegmentation = ColorSegmentation.create(NUM_SEGMENTS);
        boolean segmentSet = colorSegmentation.setSegment(ColorSegment.create(color, segmentStart, segmentEnd));
        LOG.debug("segment: {}-{} / segmentSet: {}", segmentStart, segmentEnd, segmentSet);
        ColorSegmentationEvent colorSegmentationEvent = new ColorSegmentationEvent();
        colorSegmentationEvent.setNullColorIntentionally(false);
        colorSegmentationEvent.setColorSegmentation(colorSegmentation);
        return colorSegmentationEvent;
    }

    public enum KeyboardColors {
        BLUE(Colors.createRGBW(0, 0, 255, 0)),
        RED(Colors.createRGBW(255, 0, 0, 0)),
        GREEN(Colors.createRGBW(0, 255, 0, 0)),
        WHITE(Colors.createRGBW(0, 0, 0, 255)),
        HAW_DARK_BLUE(Colors.createRGBW(27, 63, 255, 0)),
        HAW_LIGHT_BLUE(Colors.createRGBW(154, 189, 217, 0)),
        OFF(Colors.createRGBW(0, 0, 0, 0));

        private Color color;

        KeyboardColors(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }
}