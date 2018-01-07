package de.haw.eventalert.source.keyboard;


import org.apache.flink.api.java.tuple.Tuple2;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


/**
 * this source collects all {@link NativeKeyEvent} registered by {@link NativeKeyListener} and transform it to a
 * ({@link InputType},{@link String key-text}) tuple.
 * <p>
 * notice: jnativehook uses {@link java.awt.Toolkit} to localize key-text. you can set the local by
 * calling {@link java.util.Locale Locale.setDefault(Locale)} before running the source.
 *
 * @see NativeKeyEvent#getKeyText(int)
 */
public class KeyboardSourceText extends AbstractKeyboardSource<Tuple2<InputType, String>> {

    private static Tuple2<InputType, String> createTuple2(NativeKeyEvent nativeKeyEvent) {
        InputType type;
        switch (nativeKeyEvent.getID()) {
            case NativeKeyEvent.NATIVE_KEY_PRESSED:
                type = InputType.PRESSED;
                break;
            case NativeKeyEvent.NATIVE_KEY_RELEASED:
                type = InputType.RELEASED;
                break;
            case NativeKeyEvent.NATIVE_KEY_TYPED:
                type = InputType.TYPED;
                break;
            default:
                type = InputType.UNKOWN;
        }
        return Tuple2.of(type, NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
    }

    @Override
    public void run(SourceContext<Tuple2<InputType, String>> ctx) throws Exception {
        super.runWithListener(new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
                ctx.collect(createTuple2(nativeKeyEvent));
            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
            }
        });
    }
}
