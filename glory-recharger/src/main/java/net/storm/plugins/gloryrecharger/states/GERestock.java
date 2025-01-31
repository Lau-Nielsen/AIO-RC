package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemID;
import net.storm.api.Static;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.items.GrandExchange;

import java.util.List;

public class GERestock implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;
    int itemID;
    boolean collectedFlag = false;

    private int getRestockAmount() {
        if (this.itemID == ItemID.LAW_RUNE || this.itemID == ItemID.BLOOD_RUNE) {
            return config.runesLimit();
        } else if (this.itemID == ItemID.STAMINA_POTION1) {
            return config.staminaLimit();
        } else if (this.itemID == ItemID.AMULET_OF_GLORY) {
            return config.gloryLimit();
        } else {
            return config.tabsLimit();
        }
    }

    public GERestock(SharedContext context, int itemID) {
        this.context = context;
        this.config = context.getConfig();
        this.itemID = itemID;
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        List<GrandExchangeOffer> offers = GrandExchange.getOffers();
        int price = (int) Math.floor(Static.getItemManager().getItemPrice(this.itemID) * ((double) config.priceBuyMultiplier() / 100));


        if(GrandExchange.isOpen()) {
            if(offers.stream().anyMatch(o -> o.getItemId() == this.itemID)) {
                if(offers.stream().anyMatch(o -> o.getItemId() == this.itemID && o.getState() == GrandExchangeOfferState.BOUGHT)) {
                    GrandExchange.collect(true);
                    this.collectedFlag = true;
                }
            } else if (!collectedFlag) {
                GrandExchange.buy(this.itemID, getRestockAmount(), price);
            } else {
                if(this.itemID == ItemID.LAW_RUNE) {
                    stateMachine.setState(new GERestock(context, ItemID.BLOOD_RUNE), false);
                } else {
                    stateMachine.setState(new Banking(context), true);
                }
            }
        } else {
            GrandExchange.open();
        }
    }

    @Override
    public States getStateName() {
        return States.GERestock;
    }
}