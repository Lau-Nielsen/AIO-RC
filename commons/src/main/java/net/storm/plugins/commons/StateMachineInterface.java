package net.storm.plugins.commons;

public interface StateMachineInterface {
    void handleState(StateMachine stateMachine);
    String getStateName();
}
