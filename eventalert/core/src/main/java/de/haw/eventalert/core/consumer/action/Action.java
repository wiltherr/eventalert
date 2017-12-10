package de.haw.eventalert.core.consumer.action;

import java.io.Serializable;

/**
 * Created by Tim on 12.09.2017.
 */
public interface Action extends Serializable {
    String getName();

    void runAction() throws Exception;

    String getConfigurationForLog();
}
