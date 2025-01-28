package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.events.InventoryChanged;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.input.Keyboard;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.items.Trade;
import net.storm.sdk.widgets.Dialog;

import java.util.concurrent.atomic.AtomicInteger;

public class RunnerTradePlayer implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    public RunnerTradePlayer(final SharedContext context) {
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
    private void onChatMessage(ChatMessage message) {
        if (message.getMessage().equals("Sending trade offer...")) {
            this.countFlag = true;
        }
    }

    @Subscribe
    private void onInventoryChanged(InventoryChanged invChange) {
        if(invChange.getItemId() == ItemID.PURE_ESSENCE) {
            context.setEssencesTraded(context.getEssencesTraded() + invChange.getAmount());
        }
    }

    private int validatePureEssenceAmount(boolean containsBindingNecklace) {
        int maxItemsToTrade =  config.maxTradeVolume();
        int maxPureEssenceAmount = containsBindingNecklace ? maxItemsToTrade -1 : maxItemsToTrade;

        return Math.min(Inventory.getCount(ItemID.PURE_ESSENCE), maxPureEssenceAmount);
    }

    private boolean hasSpace(int amount) {
        return Inventory.getFreeSlots() > amount;
    }

    private void emptyPouches() {
        EssPouch small = EssPouch.SMALL;
        EssPouch medium = EssPouch.MEDIUM;
        EssPouch large = EssPouch.LARGE;
        EssPouch giant = EssPouch.GIANT;
        EssPouch colossal = EssPouch.COLOSSAL;

        if(context.isUsingSmallPouch() && small.getAmount() > 0 && hasSpace(small.getAmount())) {
            small.empty();
        }

        if (context.isUsingGiantPouch() && giant.getAmount() > 0 && hasSpace(giant.getAmount())) {
            giant.empty();
        }

        if (context.isUsingMediumPouch() && medium.getAmount() > 0 && hasSpace(medium.getAmount())) {
            medium.empty();
        }

        if (context.isUsingLargePouch() && large.getAmount() > 0 && hasSpace(large.getAmount())) {
            large.empty();
        }

        if (context.isUsingColossalPouch() && colossal.getAmount() > 0) {
            colossal.empty();
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        String runecrafterName = config.runecrafterName();
        int tradeEveryXTicks = config.resendTradeEvery();
        IPlayer player = Players.getNearest(runecrafterName);
        IPlayer localPlayer = Players.getLocal();
        context.checkTotalEssencesInInv();

        if(!Trade.isOpen() && Inventory.getFreeSlots() > 0) {
            emptyPouches();
        }

        if (player != null && (tickSinceTrade.get() % tradeEveryXTicks) == 0 && !Trade.isOpen() &&
                !localPlayer.isInteracting() && context.getTotalEssencesInInv() > config.minTradeVolume()) {
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
                Trade.offer(ItemID.PURE_ESSENCE, validatePureEssenceAmount(containsBindingNecklace));
            }
        }

        context.checkTotalEssencesInInv();
        if (context.getTotalEssencesInInv() < config.minTradeVolume() && !Trade.isOpen()) {
            context.checkChargesOnRote();
            stateMachine.setState(new RechargeROTE(context), false);
        }

    }

    @Override
    public States getStateName() {
        return States.Trade;
    }
}
