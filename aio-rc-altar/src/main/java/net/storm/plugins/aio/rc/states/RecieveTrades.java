package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.events.PlayerDespawned;
import net.storm.api.events.PlayerSpawned;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.items.Trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecieveTrades implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    private List<String> tradeOrder = new ArrayList<String>();

    public RecieveTrades(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Subscribe
    private void onPlayerSpawned(PlayerSpawned spawnedPlayer) {
        String[] runners = config.runnerNames().split(",");
        if(Arrays.stream(runners).anyMatch(e -> e.equals(spawnedPlayer.getPlayer().getName()))) {
            tradeOrder.add(spawnedPlayer.getPlayer().getName());
        }
    }

    @Subscribe
    private void onPlayerDespawned(PlayerDespawned despawnedPlayer) {
        String[] runners = config.runnerNames().split(",");
        if(Arrays.stream(runners).anyMatch(e -> e.equals(despawnedPlayer.getPlayer().getName()))) {
            tradeOrder.removeIf(e -> e.equals(despawnedPlayer.getPlayer().getName()));
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        IPlayer player = Players.getNearest(tradeOrder.get(0));

        if (player != null &&
                !Trade.isOpen() && !Inventory.contains(ItemID.PURE_ESSENCE)) {
            player.interact("Trade with");
        }

        if(Trade.isOpen()) {
            if(!Trade.getAll(true, ItemID.PURE_ESSENCE).isEmpty()) {
                if(Trade.isFirstScreenOpen() && !Trade.hasAcceptedFirstScreen(false)) {
                    Trade.accept();
                }
                if(Trade.isSecondScreenOpen() && !Trade.hasAcceptedSecondScreen(false)) {
                    Trade.accept();
                }
            }
        }

        if (Inventory.contains(ItemID.PURE_ESSENCE)) {
            stateMachine.setState(new CraftRunes(context), true);
        }

    }

    @Override
    public States getStateName() {
        return States.Trade;
    }
}
