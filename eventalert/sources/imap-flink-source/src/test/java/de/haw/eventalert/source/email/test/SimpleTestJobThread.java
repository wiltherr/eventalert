package de.haw.eventalert.source.email.test;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * creates a simple flink job in a standalone cluster which runs in one {@link Thread}.
 * <p/>
 * the flink job connects one source and one sink of the same data type.
 *
 * @param <T> data type of source function and sink function
 */
public class SimpleTestJobThread<T> extends Thread {
    private final SourceFunction<T> testSource;
    private final SinkFunction<T> testSink;

    /**
     * creates a simple flink job in a standalone cluster which runs in one {@link Thread}.
     * can be used for testing flink sources.
     * <p/>
     * the flink job connects one source and one sink of the same type.
     * call {@link SimpleTestJobThread#start()} to execute the job.
     *
     * @param testSource {@link SourceFunction} which produces elements of type T
     * @param testSink   {@link SinkFunction} which consumes elements of type T
     */
    public SimpleTestJobThread(SourceFunction<T> testSource, SinkFunction<T> testSink) {
        this.testSource = testSource;
        this.testSink = testSink;
    }

    /**
     * creates a flink {@link StreamExecutionEnvironment} and executes the job.
     */
    @Override
    public void run() {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<T> mailMessageDataStream = env.addSource(testSource).name("Test Source");
        mailMessageDataStream.addSink(testSink).name("Test Sink");
        try {
            env.execute("Test Job");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * cancels the testSource
     */
    @Override
    public void interrupt() {
        testSource.cancel();
    }
}

