package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.action.Action;

public class EmptyTestAction implements Action {
    @Override
    public String getName() {
        return "EmptyTestAction";
    }

    @Override
    public void runAction() throws Exception {
    }

    @Override
    public String getConfigurationForLog() {
        return "empty action for testing";
    }
}
