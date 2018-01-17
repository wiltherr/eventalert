package de.haw.eventalert.ledbridge.entity.event.type;

/**
 * Created by Tim on 12.05.2017.
 */
public interface TimeLimited {
    long getDuration();

    void setDuration(long durationInMs);
}
