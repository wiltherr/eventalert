package de.haw.eventalert.core.producer.example;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.concurrent.TimeUnit;

/**
 * example source function for {@link ExampleProducerJob}
 */
public class MyEventSource implements SourceFunction<MyEvent> {
    private boolean stop = false;

    @Override
    public void run(SourceContext<MyEvent> ctx) throws Exception {
        //produces myEvent every second when not stopped
        for (int i = 0; !stop; i++) {
            //create new myEvent
            MyEvent myEvent = new MyEvent();
            myEvent.setMyField1("TestValue");
            myEvent.setMyField2(i);
            //collect event to source output
            ctx.collect(myEvent);
            //sleep one second
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public void cancel() {
        stop = true;
    }
}
