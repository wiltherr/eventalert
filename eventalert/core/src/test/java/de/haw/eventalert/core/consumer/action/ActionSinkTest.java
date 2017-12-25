package de.haw.eventalert.core.consumer.action;

import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionSinkTest {

    public static final AtomicLong actionRunCounter = new AtomicLong();
    private static final long ACTION_COUNT = 1000;


    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testActionSink() throws Exception {
        List<Action> actionList = Lists.newArrayList();
        for (int i = 0; i < ACTION_COUNT; i++) {
            actionList.add(new TestAction(i));
        }
        Collections.shuffle(actionList);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        env.fromCollection(actionList)
                .addSink(new ActionSink());
        env.execute();

        assertEquals(actionList.size(), actionRunCounter.get());
    }

    public class TestAction implements Action {

        private int number;

        public TestAction(int number) {
            this.number = number;
        }

        @Override
        public String getName() {
            return "TestAction " + number;
        }

        @Override
        public void runAction() throws Exception {
            ActionSinkTest.actionRunCounter.incrementAndGet();
        }

        @Override
        public String getConfigurationForLog() {
            return "current run count: " + ActionSinkTest.actionRunCounter.get();
        }
    }
}