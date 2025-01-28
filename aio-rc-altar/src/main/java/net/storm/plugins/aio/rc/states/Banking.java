package net.storm.plugins.aio.rc.states;

import lombok.Setter;
import net.runelite.api.ItemID;
import net.storm.api.events.AnimationChanged;
import net.storm.api.widgets.EquipmentSlot;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.RunningState;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.sdk.entities.Players;
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

    public Banking(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    public void onAnimationChanged(AnimationChanged e) {
        if(e.getActor().getId() == Players.getLocal().getId() && e.getActor().getAnimation() == 829) {

            e.getActor().setAnimation(-1);
        }
    }

    private void withdrawAndDrinkStamina() {
        int stam_1 = ItemID.STAMINA_POTION1;
        int stam_2 = ItemID.STAMINA_POTION2;
        int stam_3 = ItemID.STAMINA_POTION3;
        int stam_4 = ItemID.STAMINA_POTION4;


        if(config.useStamina()) {
            if (Bank.contains(stam_1, stam_2, stam_3, stam_4)) {
                if(Movement.getRunEnergy() <= config.staminaThreshold() &&
                        !Movement.isStaminaBoosted() &&
                        Bank.isOpen() &&
                        !Bank.Inventory.contains(stam_1, stam_2, stam_3, stam_4)) {
                    Bank.withdraw(ItemID.STAMINA_POTION1, 1);
                }

                if(Bank.Inventory.contains(stam_1, stam_2, stam_3, stam_4) && !Movement.isStaminaBoosted()) {
                    Bank.Inventory.getFirst(stam_1, stam_2, stam_3, stam_4).interact("Drink");
                } else if (Movement.isStaminaBoosted() && Bank.Inventory.contains(stam_1, stam_2, stam_3, stam_4)) {
                    Bank.depositAll(stam_1, stam_2, stam_3, stam_4);
                }
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of staminas, stopping plugin", Color.red);
            }
        }
    }

    private void bankForBindingNecklace() {
        if(config.bringBindingNecklace() &&
                context.getTripsCompleted().get() % config.bindingNecklaceFrequency() == 0) {
            if(!Bank.Inventory.contains(ItemID.BINDING_NECKLACE)) {
                Bank.withdraw(ItemID.BINDING_NECKLACE, 1);
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of binding necklaces, stopping plugin", Color.red);
            }
        }
    }

    private void bankForTalisman() {
        Integer talismanId = context.getTalismanNeededForComboRunes();
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

        if(Bank.contains(essenceID)) {
            if (Bank.Inventory.getCount(false, essenceID) <= countCheck) {
                Bank.withdrawAll(essenceID);
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of essences, stopping plugin", Color.red);
            }
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
            withdrawEssence(daeyalt, 0);
            colossal.fill();
        }

        withdrawEssence(daeyalt, 0);

        context.checkTotalEssencesInInv();
        context.checkEssenceInBank();
    }

    private void depositRunes() {
        int runeId = context.getCurrentlyCrafting().getItemID();
        if (Bank.Inventory.contains(runeId) && config.bankCraftedRunes()) {
           Bank.depositAll(runeId);
        }
    }

    private void withdrawAndEquipDuelingRing() {
        int duelingId = ItemID.RING_OF_DUELING8;
        if (context.isUsingDuelingRings() && !context.checkForDuelingRing()) {
            if(!Bank.contains(duelingId)) {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of dueling rings, stopping plugin", Color.red);
            } else {
                Bank.withdraw(duelingId, 1);

                if(Equipment.fromSlot(EquipmentSlot.RING) == null &&
                        Bank.Inventory.contains(duelingId)) {
                    Bank.Inventory.getFirst(duelingId).interact("Wear");
                }
            }
        }
    }

    private void withdrawAndEquipGlory() {
        int gloryID = ItemID.AMULET_OF_GLORY6;
        if (context.isUsingGlories() && (Bank.Inventory.contains(ItemID.AMULET_OF_GLORY) ||
                Equipment.contains(ItemID.AMULET_OF_GLORY)) && Bank.contains(gloryID)) {
            if(Bank.contains(gloryID)) {
                Equipment.fromSlot(EquipmentSlot.AMULET).interact("Remove");
                Bank.withdraw(gloryID, 1);

                if(Equipment.fromSlot(EquipmentSlot.AMULET) == null &&
                        Bank.Inventory.contains(ItemID.AMULET_OF_GLORY6)) {
                    Bank.Inventory.getFirst(gloryID).interact("Wear");
                }
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of glories, stopping plugin", Color.red);
            }
        }

        if (Bank.Inventory.contains(ItemID.AMULET_OF_GLORY)) {
            Bank.deposit(ItemID.AMULET_OF_GLORY, 1);
        }
    }

    private void withdrawRunesForComboRunes(){
        if(!Bank.Inventory.contains(context.getRuneNeededForComboRunesId())) {
            if(Bank.contains(context.getRuneNeededForComboRunesId())) {
                Bank.withdrawAll(context.getRuneNeededForComboRunesId());
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of runes needed for combo runes, stopping plugin", Color.red);
            }
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        if (!Bank.isOpen()) {
            Bank.open(config.bank().getBankLocation());
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
            if(context.getEssenceInBank() == 0) {
                context.setCurrentRunningState(RunningState.STOPPED);
            } else if (config.useStamina() && context.getStaminaDoses() == 0) {
                context.setCurrentRunningState(RunningState.STOPPED);
            }

            Bank.close();
            if(context.checkForBrokenPouch()) {
                stateMachine.setState(new RepairPouch(context), false);
            } else if (config.usePoolAtFerox()) {
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