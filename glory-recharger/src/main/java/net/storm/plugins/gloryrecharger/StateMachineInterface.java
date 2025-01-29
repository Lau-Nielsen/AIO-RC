package net.storm.plugins.gloryrecharger;

import net.storm.plugins.gloryrecharger.enums.States;

public interface StateMachineInterface {
    void handleState(StateMachine stateMachine);
    States getStateName();
}
