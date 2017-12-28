package de.haw.eventalert.core.producer.example;

/**
 * example event for {@link ExampleProducerJob}
 */
public class MyEvent {
    private String myField1;
    private int myField2;

    public MyEvent() {
    }

    public String getMyField1() {
        return myField1;
    }

    public void setMyField1(String myField1) {
        this.myField1 = myField1;
    }

    public int getMyField2() {
        return myField2;
    }

    public void setMyField2(int myField2) {
        this.myField2 = myField2;
    }
}
