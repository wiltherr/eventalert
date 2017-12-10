package de.haw.eventalert.core.consumer.action.example;

import de.haw.eventalert.core.consumer.action.Action;

/**
 * Created by Tim on 01.11.2017.
 */
public class MyAction implements Action {

    @Override
    public String getName() {
        //unique name
        return "MyAction";
    }

    @Override
    public void runAction() {
        //this method will be triggered by the action sink when a filter uses the action
    }

    @Override
    public String getConfigurationForLog() {
        return "";
    }
}
