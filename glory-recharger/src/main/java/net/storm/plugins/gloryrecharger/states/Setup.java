package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.quests.Quests;
import net.storm.sdk.utils.MessageUtils;
import net.storm.sdk.widgets.Widgets;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Setup implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;

    public Setup(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Override
    public void handleState(StateMachine stateMachine) {

        stateMachine.setState(new Banking(context), false);
    }

    @Override
    public States getStateName() {
        return States.Setup;
    }
}