package de.haw.eventalert.core.producer.example;

import de.haw.eventalert.core.global.AlertEvents;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import de.haw.eventalert.core.producer.EventAlertProducer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Created by Tim on 01.11.2017.
 */
public class ExampleProducerJob {
    public static void main(String[] args) throws Exception {
        //get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        //create the desired source (must implement the SourceFunction<> interface)
        MyEventSource eventSource = new MyEventSource();
        //add source to environment (MyEvent have to be a simple POJO)
        DataStream<MyEvent> myEventStream = env.addSource(eventSource);
        //convert myEvents to alertEvents
        DataStream<AlertEvent> alertEventStream = myEventStream.flatMap((myEvent, out) -> {
            try {
                out.collect(AlertEvents.createEvent("myEventTypeName", myEvent));
            } catch (Exception e) {
                //Error logging if needed
            }
        });
        //provide alertEventStream to EventAlert
        EventAlertProducer.provideDataStream(alertEventStream);
        //execute the job
        env.execute("MyAlertEventProducer");
    }
}
