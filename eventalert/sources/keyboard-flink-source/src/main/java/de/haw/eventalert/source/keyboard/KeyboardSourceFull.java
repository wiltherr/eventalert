package de.haw.eventalert.source.keyboard;


import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * this source collects all {@link NativeKeyEvent} registered by {@link NativeKeyListener}
 */
public class KeyboardSourceFull extends AbstractKeyboardSource<NativeKeyEvent> {

    @Override
    public void run(final SourceContext<NativeKeyEvent> ctx) throws Exception {
        super.runWithListener(new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
                ctx.collect(nativeEvent);
            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                ctx.collect(nativeEvent);
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                ctx.collect(nativeEvent);
            }
        });
    }
}
