package de.haw.eventalert.source.keyboard;


import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


/**
 * this source collects all {@link NativeKeyEvent} registered by {@link NativeKeyListener} when key is pressed.
 * it converts every event to its key-text as string
 * <p>
 * notice: jnativehook uses the default locale to localize key-text with {@link java.awt.Toolkit}. you can change the local by
 * calling {@link java.util.Locale Locale.setDefault(Locale)} before running the source.
 *
 * @see NativeKeyEvent#getKeyText(int)
 */
public class KeyboardSourceText extends AbstractKeyboardSource<String> {

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        super.runWithListener(new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                ctx.collect(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
            }
        });
    }
}
