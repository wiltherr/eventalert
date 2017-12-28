package de.haw.eventalert.core.consumer.action.example;

import de.haw.eventalert.core.consumer.action.Action;

/**
 * Example Action
 */
public class ExampleAction implements Action {

    @Override
    public String getName() {
        //unique name
        return "ExampleAction";
    }

    @Override
    public void runAction() {
        //this method will be triggered by the action sink when a filter uses the action
    }

    @Override
    public String getConfigurationForLog() {
        return "ExampleAction";
    }
}
