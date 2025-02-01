package net.storm.plugins.aio.rc.states;

import lombok.Setter;
import net.runelite.api.ItemID;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.commons.enums.RunningState;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.plugins.commons.utils.BankUtils;
import net.storm.plugins.commons.utils.TpJewelry;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;


@Setter
public class Banking implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    BankUtils bankUtils = new BankUtils();
    TpJewelry tpJewelry = new TpJewelry();

    public Banking(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private void withdrawAndDrinkStamina() {
        if(config.useStamina()) {
            if (!Bank.contains(i -> i.getName() != null && i.getName().contains("Stamina potion"))) {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of staminas, stopping plugin", Color.red);
                return;
            }

            if(Movement.getRunEnergy() <= config.staminaThreshold() && !Movement.isStaminaBoosted()) {
                bankUtils.withdrawAndDrinkStamina();
            } else {
                bankUtils.depositStamina();
            }
        }
    }

    private void bankForBindingNecklace() {
        if(config.bringBindingNecklace()) {
            if(context.getTripsCompleted().get() % config.bindingNecklaceFrequency() == 0) {
                if(!Bank.contains(ItemID.BINDING_NECKLACE)) {
                    context.setCurrentRunningState(RunningState.STOPPED);
                    MessageUtils.addMessage("Out of binding necklaces, stopping plugin", Color.red);
                    return;
                }

                if(!Bank.Inventory.contains(ItemID.BINDING_NECKLACE)) {
                    bankUtils.withdraw(ItemID.BINDING_NECKLACE, 1);
                }
            }
        }
    }

    private void bankForTalisman() {
        Integer talismanId = context.getTalismanIDNeededForComboRune();
        int amountToWithdraw = context.talismansToWithdraw();

        if (!config.useImbue() && talismanId != null &&
                Inventory.getCount(false, talismanId) < amountToWithdraw) {

            if(Bank.contains(talismanId)) {
                for (int i = 0 ; i < amountToWithdraw ; i++) {
                    Bank.withdraw(talismanId, 1);
                }
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of talismans, stopping plugin", Color.red);
            }
        }
    }

    private void withdrawEssence(boolean daeyalt, int countCheck) {
        int essenceID;

        if(daeyalt) {
            essenceID = ItemID.DAEYALT_ESSENCE;
        } else {
            essenceID = ItemID.PURE_ESSENCE;
        }

        if(!Bank.contains(essenceID)) {
            context.setCurrentRunningState(RunningState.STOPPED);
            MessageUtils.addMessage("Out of essences, stopping plugin", Color.red);
            return;
        }

        if (Bank.Inventory.getCount(false, essenceID) <= countCheck) {
            bankUtils.withdrawAll(essenceID);
        }
    }

    private void bankForEssence() {
        boolean daeyalt = config.useDaeyalt();
        EssPouch small = EssPouch.SMALL;
        EssPouch medium = EssPouch.MEDIUM;
        EssPouch large = EssPouch.LARGE;
        EssPouch giant = EssPouch.GIANT;
        EssPouch colossal = EssPouch.COLOSSAL;
        context.checkEssenceInBank();


        if (context.isUsingSmallPouch() && small.getAmount() < small.maxAmount() ) {
            withdrawEssence(daeyalt, small.maxAmount());
            small.fill();
        }

        if (context.isUsingGiantPouch() && giant.getAmount() < giant.maxAmount() ) {
            withdrawEssence(daeyalt, giant.maxAmount());
            giant.fill();
        }

        if (context.isUsingMediumPouch() && medium.getAmount() < medium.maxAmount() ) {
            withdrawEssence(daeyalt, medium.maxAmount());
            medium.fill();
        }


        if (context.isUsingLargePouch() && large.getAmount() < large.maxAmount() ) {
            withdrawEssence(daeyalt, large.maxAmount());
            large.fill();
        }

        if (context.isUsingColossalPouch() && colossal.getAmount() < colossal.maxAmount() ) {
            withdrawEssence(daeyalt, 28);
            colossal.fill();
        }

        if(context.arePouchesFull()) {
            withdrawEssence(daeyalt, 28);
        }

        context.checkTotalEssencesInInv();
        context.checkEssenceInBank();
    }

    private void depositRunes() {
        int runeId = context.getCurrentlyCrafting().getItemID();
        if (config.bankCraftedRunes()) {
           bankUtils.depositAll(runeId);
        }
    }

    private void withdrawAndEquipDuelingRing() {
        if (context.isUsingDuelingRings() && !Equipment.contains(tpJewelry.getDuelingRingPredicate())) {
            if(!Bank.contains(tpJewelry.getDuelingRingIds())) {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of dueling rings, stopping plugin", Color.red);
                return;
            }

            bankUtils.withdrawAndEquip(tpJewelry.getDuelingRingPredicate());
        }
    }

    private void withdrawAndEquipGlory() {
        if (context.isUsingGlories()) {
            if(!Bank.contains(tpJewelry.getChargedGloryPredicate())) {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of glories, stopping plugin", Color.red);
                return;
            }

            if(!Equipment.contains(tpJewelry.getChargedGloryPredicate())) {
                bankUtils.withdrawAndEquip(tpJewelry.getChargedGloryPredicate());
                return;
            }

            bankUtils.deposit(ItemID.AMULET_OF_GLORY, 1);
        }
    }

    private void withdrawRunesForComboRunes(){
        int runeId = context.getOppositeRuneIDForComboRune();

        if(!Bank.Inventory.contains(runeId)) {
            if(!Bank.contains(runeId)) {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of runes needed for combo runes, stopping plugin", Color.red);
                return;
            }

            Bank.withdrawAll(runeId);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        if (!Bank.isOpen()) {
            Bank.open(config.bank().getBankLocation());
        }

        if(context.checkForBrokenPouch() && !config.repairOnDarkMage()) {
            stateMachine.setState(new RepairPouch(context), false);
        }

        if (Bank.isOpen()) {
            depositRunes();
            withdrawAndEquipDuelingRing();
            withdrawAndEquipGlory();
            withdrawRunesForComboRunes();
            bankForTalisman();
            bankForBindingNecklace();
            withdrawAndDrinkStamina();


            if(context.maxEssenceCapacity() >= context.getTotalEssencesInInv()) {
                bankForEssence();
            }
        }

        if(context.maxEssenceCapacity() == context.getTotalEssencesInInv()) {
            Bank.close();
            if (config.usePoolAtFerox()) {
                stateMachine.setState(new UseFeroxPool(context), true);
            } else {
                stateMachine.setState(new WalkToAltar(context), true);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.Banking;
    }
}