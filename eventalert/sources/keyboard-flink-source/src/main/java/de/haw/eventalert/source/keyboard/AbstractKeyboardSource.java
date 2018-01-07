package de.haw.eventalert.source.keyboard;

import org.apache.flink.api.common.functions.StoppableFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * abstract implementation of {@link RichSourceFunction} for a {@link NativeKeyListener}
 *
 * @param <OUT> see {@link RichSourceFunction}
 * @see KeyboardSourceFull
 * @see KeyboardSourceText
 */
abstract class AbstractKeyboardSource<OUT> extends RichSourceFunction<OUT> implements StoppableFunction {

    static {
        //add a bridge from java.util.logging (j.u.l) to slf4j because jnativehook uses j.u.l
        //(see https://www.slf4j.org/legacy.html#jul-to-slf4j and https://www.slf4j.org/api/org/slf4j/bridge/SLF4JBridgeHandler.html)
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private transient boolean isRunning = false;
    private transient Object waitLock;
    private transient NativeKeyListener listener;


    @Override
    public void open(Configuration parameters) throws Exception {
        waitLock = new Object();
    }

    void runWithListener(NativeKeyListener listener) throws Exception {
        if (!GlobalScreen.isNativeHookRegistered()) {
            GlobalScreen.registerNativeHook();
        }

        if (GlobalScreen.isNativeHookRegistered()) {
            this.listener = listener;
            GlobalScreen.addNativeKeyListener(listener);

            //wait
            isRunning = true;
            while (isRunning) {
                synchronized (waitLock) {
                    waitLock.wait(100L);
                }
            }
        } else {
            throw new Exception("native hook was not registered");
        }
    }

    @Override
    public abstract void run(SourceContext<OUT> ctx) throws Exception;

    @Override
    public void close() {
        isRunning = false;
        if (listener != null) {
            GlobalScreen.removeNativeKeyListener(listener);
        }

        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

        synchronized (waitLock) {
            waitLock.notify();
        }
    }

    @Override
    public void cancel() {
        close();
    }

    @Override
    public void stop() {
        close();
    }
}
