package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.storm.api.domain.items.IItem;
import net.storm.api.widgets.EquipmentSlot;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.commons.enums.RunningState;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.utils.MessageUtils;
import net.storm.sdk.widgets.Dialog;

import java.awt.*;

public class RechargeROTE implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    public RechargeROTE(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        int waterID = ItemID.WATER_RUNE;
        int fireID = ItemID.FIRE_RUNE;
        int earthID = ItemID.EARTH_RUNE;
        int airID = ItemID.AIR_RUNE;
        int lawID = ItemID.LAW_RUNE;

        int chargedRotEID = ItemID.RING_OF_THE_ELEMENTS_26818;

        boolean containsRunes = Inventory.containsAll(earthID, waterID, fireID, airID, lawID);

        if (!Bank.isOpen() && !containsRunes) {
            Bank.open(config.bank().getBankLocation());
        }

        if (Bank.isOpen()) {
            Bank.withdrawAll(lawID);
            Bank.withdrawAll(airID);
            Bank.withdrawAll(earthID);
            Bank.withdrawAll(waterID);
            Bank.withdrawAll(fireID);

            if(Bank.Inventory.getCount(true, lawID) < config.roteChargesToAdd() ||
                    Bank.Inventory.getCount(true, airID) < config.roteChargesToAdd() ||
                    Bank.Inventory.getCount(true, earthID) < config.roteChargesToAdd() ||
                    Bank.Inventory.getCount(true, fireID) < config.roteChargesToAdd() ||
                    Bank.Inventory.getCount(true, waterID) < config.roteChargesToAdd()
            ) {
                MessageUtils.addMessage("Insufficient runes to charge RotE, stopping plugin...", Color.RED);
                context.setCurrentRunningState(RunningState.STOPPED);
                return;
            }
        }

        if (containsRunes) {
            if(Bank.isOpen()) {
                Bank.close();
            }

            if(!Bank.isOpen()) {
                IItem ring = Equipment.get(EquipmentSlot.RING.getSlot());

                if(ring != null && ring.getId() == chargedRotEID) {
                    Equipment.get(EquipmentSlot.RING.getSlot()).interact("Remove");
                }

                if(Inventory.contains(chargedRotEID)) {
                    Inventory.getFirst(lawID).useOn(Inventory.getFirst(chargedRotEID));
                }
            }
        }

        if(Dialog.isOpen() && Dialog.isEnterInputOpen()) {
            Dialog.enterAmount(config.roteChargesToAdd());
        }

        context.checkChargesOnRote();
        if(context.getChargesOnRingOfElement() > config.roteChargeAt() || !context.isUsingRingOfElements()) {
            context.getTripsCompleted().incrementAndGet();
            stateMachine.setState(new Banking(context), true);
        }
    }

    @Override
    public States getStateName() {
        return States.RechargeRingOfElements;
    }
}