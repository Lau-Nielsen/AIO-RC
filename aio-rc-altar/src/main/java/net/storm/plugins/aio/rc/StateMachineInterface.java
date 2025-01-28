package net.storm.plugins.aio.rc;

import net.storm.plugins.aio.rc.enums.States;

public interface StateMachineInterface {
    void handleState(StateMachine stateMachine);
    States getStateName();
}
