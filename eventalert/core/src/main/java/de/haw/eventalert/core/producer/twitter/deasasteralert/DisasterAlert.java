package de.haw.eventalert.core.producer.twitter.deasasteralert;

public class DisasterAlert {
    public static final String EVENT_TYPE = "DisasterAlert";
    private String city;

    public DisasterAlert(String city) {
        this.city = city;
    }

    public DisasterAlert() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
