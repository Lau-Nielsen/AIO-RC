package net.storm.plugins.daeyalt.states;

import net.storm.plugins.daeyalt.SharedContext;
import net.storm.plugins.daeyalt.StateMachine;
import net.storm.plugins.daeyalt.StateMachineInterface;
import net.storm.plugins.daeyalt.enums.States;

public class Setup implements StateMachineInterface {
    SharedContext context;

    public Setup(SharedContext context) {
        this.context = context;
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        context.initTickManipMap();

        stateMachine.setState(new WalkToMine(context), false);
    }

    @Override
    public States getStateName() {
        return States.Setup;
    }
}