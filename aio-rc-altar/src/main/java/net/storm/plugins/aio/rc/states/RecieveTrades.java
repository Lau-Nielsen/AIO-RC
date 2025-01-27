package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.items.Trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RecieveTrades implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    public RecieveTrades(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private boolean countFlag = false;
    private AtomicInteger tickSinceTrade = new AtomicInteger(0);

    @Subscribe
    private void onGameTick(GameTick tick) {
        if(countFlag) {
            tickSinceTrade.incrementAndGet();
        }
    }

    @Subscribe
    private void onPlayerSpawned(PlayerSpawned spawnedPlayer) {
        String[] runners = config.runnerNames().split(",");

        System.out.println("player spawned: " +  spawnedPlayer.getPlayer().getName());
        if(Arrays.stream(runners).anyMatch(e -> e.equals(spawnedPlayer.getPlayer().getName()))) {
            context.getTradeOrder().add(spawnedPlayer.getPlayer().getName());
        }
    }

    @Subscribe
    private void onPlayerDespawned(PlayerDespawned despawnedPlayer) {
        String[] runners = config.runnerNames().split(",");
        System.out.println("player despaned: " +  despawnedPlayer.getPlayer().getName());

        for (var i : runners) {
            System.out.println(i);
        }
        if(Arrays.stream(runners).anyMatch(e -> e.equals(despawnedPlayer.getPlayer().getName()))) {
            context.getTradeOrder().removeIf(e -> e.equals(despawnedPlayer.getPlayer().getName()));
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        IPlayer player = null;
        IPlayer localPlayer = Players.getLocal();

        if (!context.getTradeOrder().isEmpty()) {
            player = Players.getNearest(context.getTradeOrder().get(0));
        }

        if (player != null &&
                !Trade.isOpen() && !Inventory.contains(ItemID.PURE_ESSENCE) &&
                player.distanceTo(localPlayer.getWorldArea().toWorldPoint()) < 2 && tickSinceTrade.get() % config.resendTradeEvery() == 0) {
            player.interact("Trade with");
            countFlag = true;
        }

        for (var i : context.getTradeOrder()) {
            System.out.println(i);
        }

        if(Trade.isOpen()) {
            if(!Trade.getAll(true, ItemID.PURE_ESSENCE).isEmpty()) {
                if(Trade.isFirstScreenOpen() && !Trade.hasAcceptedFirstScreen(true)) {
                    Trade.accept();
                }
                if(Trade.isSecondScreenOpen() && !Trade.hasAcceptedSecondScreen(true)) {
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
