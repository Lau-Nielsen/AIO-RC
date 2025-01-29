package net.storm.plugins.daeyalt;

import net.storm.plugins.daeyalt.enums.States;

public interface StateMachineInterface {
    void handleState(StateMachine stateMachine);
    States getStateName();
}
