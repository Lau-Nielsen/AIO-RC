package net.storm.plugins.examples.looped.states;

import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.plugins.examples.looped.StateMachine;
import net.storm.plugins.examples.looped.StateMachineInterface;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.items.Trade;

import java.util.concurrent.atomic.AtomicInteger;

public class TradePlayer implements StateMachineInterface {
    private boolean countFlag = false;
    private AtomicInteger tickSinceTrade = new AtomicInteger(0);

    @Subscribe
    private void onGameTick(GameTick tick) {
        if(countFlag) {
            tickSinceTrade.incrementAndGet();
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage message) {
        if (message.getMessage().equals("Sending trade offer...")) {
            this.countFlag = true;
        }
    }

    private int validatePureEssenceAmount(StateMachine stateMachine, boolean containsBindingNecklace) {
        int maxItemsToTrade =  stateMachine.getContext().getConfig().maxTradeVolume();
        int maxPureEssenceAmount = containsBindingNecklace ? maxItemsToTrade -1 : maxItemsToTrade;

        return Math.min(Inventory.getCount(ItemID.PURE_ESSENCE), maxPureEssenceAmount);
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        String runecrafterName = stateMachine.getContext().getConfig().runecrafterName();
        int tradeEveryXTicks = stateMachine.getContext().getConfig().resendTradeEvery();
        IPlayer player = Players.getNearest(runecrafterName);

        if (player != null && (tickSinceTrade.get() % tradeEveryXTicks) == 0 && !Trade.isOpen()) {
            player.interact("Trade with");
        }

        if(Trade.isOpen()) {
            if(!Trade.getAll(false, ItemID.PURE_ESSENCE).isEmpty()) {
                if(Trade.isFirstScreenOpen() && !Trade.hasAcceptedFirstScreen(false)) {
                    Trade.accept();
                }
                if(Trade.isSecondScreenOpen() && !Trade.hasAcceptedSecondScreen(false)) {
                    Trade.accept();
                }
            } else {
                boolean containsBindingNecklace = Inventory.contains(ItemID.BINDING_NECKLACE);

                if(containsBindingNecklace) {
                    Trade.offer(ItemID.BINDING_NECKLACE, 1);
                }
                Trade.offer(ItemID.PURE_ESSENCE, validatePureEssenceAmount(stateMachine, containsBindingNecklace));
            }
        }

    }

    @Override
    public States getStateName() {
        return States.Trade;
    }
}
