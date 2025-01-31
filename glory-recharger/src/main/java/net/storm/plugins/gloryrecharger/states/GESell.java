package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemID;
import net.storm.api.Static;
import net.storm.api.movement.pathfinder.model.BankLocation;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.GrandExchange;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;

import java.util.List;

public class GESell implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;
    boolean collectedFlag = false;
    boolean withDrawnFlag = false;

    public GESell(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        List<GrandExchangeOffer> offers = GrandExchange.getOffers();
        int price = (int) Math.floor(Static.getItemManager().getItemPrice(ItemID.AMULET_OF_GLORY6) * ((double) config.sellMultiplier() / 100));



        if(Inventory.contains(11979) || this.withDrawnFlag) {
            this.withDrawnFlag = true;
            if(GrandExchange.isOpen()) {
                if(offers.stream().anyMatch(o -> o.getItemId() == ItemID.AMULET_OF_GLORY6)) {
                    if(offers.stream().anyMatch(o -> o.getItemId() == ItemID.AMULET_OF_GLORY6 && o.getState() == GrandExchangeOfferState.SOLD)) {
                        GrandExchange.collect(true);
                        this.collectedFlag = true;
                    }
                } else if (!collectedFlag) {
                    GrandExchange.sell(ItemID.AMULET_OF_GLORY6, Inventory.getCount(true, 11979), price);
                } else {
                    stateMachine.setState(new GERestock(context, ItemID.AMULET_OF_GLORY), false);
                }
            } else {
                GrandExchange.open();
            }
        } else {
            if (!Bank.isOpen() && !Movement.isWalking()) {
                Bank.open(BankLocation.GRAND_EXCHANGE_BANK);
            } else {
                Bank.setWithdrawMode(true);

                if(Bank.isNotedWithdrawMode()) {
                    Bank.withdrawAll(ItemID.AMULET_OF_GLORY6);
                    this.withDrawnFlag = true;
                }
            }
        }
    }

    @Override
    public States getStateName() {
        return States.GESell;
    }
}