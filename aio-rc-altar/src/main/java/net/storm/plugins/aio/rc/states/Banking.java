package net.storm.plugins.aio.rc.states;

import lombok.Setter;
import net.runelite.api.ItemID;
import net.storm.api.events.AnimationChanged;
import net.storm.api.items.loadouts.LoadoutItem;
import net.storm.api.magic.Rune;
import net.storm.api.widgets.EquipmentSlot;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.enums.Banks;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.sdk.entities.Players;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;


@Setter
public class Banking implements StateMachineInterface {
    private void OpenBank(final Banks bank) {
        Bank.open(bank.getBankLocation());
    }

    public void onAnimationChanged(AnimationChanged e) {
        if(e.getActor().getId() == Players.getLocal().getId() && e.getActor().getAnimation() == 829) {

            e.getActor().setAnimation(-1);
        }
    }

    private void withdrawAndDrinkStamina(SharedContext sharedContext) {
        int stam_1 = ItemID.STAMINA_POTION1;
        int stam_2 = ItemID.STAMINA_POTION2;
        int stam_3 = ItemID.STAMINA_POTION3;
        int stam_4 = ItemID.STAMINA_POTION4;


        if(sharedContext.getConfig().useStamina()) {
            if(Movement.getRunEnergy() <= sharedContext.getConfig().staminaThreshold() &&
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
        }
    }

    private void bankForBindingNecklace(SharedContext sharedContext) {
        if(sharedContext.getConfig().bringBindingNecklace() &&
                sharedContext.getTripsCompleted() % sharedContext.getConfig().bindingNecklaceFrequency() == 0) {
            if(!Bank.Inventory.contains(ItemID.BINDING_NECKLACE)) {
                Bank.withdraw(ItemID.BINDING_NECKLACE, 1);
            }
        }
    }

    private void bankForTalisman(SharedContext context) {
        AIORCConfig config = context.getConfig();
        Integer talismanId = context.getTalismanNeededForComboRunes();
        int amountToWithdraw = context.talismansToWithdraw();

        if (!config.useImbue() && talismanId != null &&
                Inventory.getCount(false, talismanId) < amountToWithdraw) {

            System.out.println("ALO");
            if(Bank.contains(talismanId)) {
                for (int i = 0 ; i < amountToWithdraw ; i++) {
                    System.out.println("ALO : " + 1);
                    Bank.withdraw(talismanId, 1);
                }
            }
        }
    }

    private void withdrawEssence(boolean daeyalt) {
        int essenceID;

        if(daeyalt) {
            essenceID = ItemID.DAEYALT_ESSENCE;
        } else {
            essenceID = ItemID.PURE_ESSENCE;
        }

        if (Bank.Inventory.getCount(false, essenceID) < 5) {
            Bank.withdrawAll(essenceID);
        }
    }

    private void fullSendWithdraw(boolean daeyalt) {
        int essenceID;

        if(daeyalt) {
            essenceID = ItemID.DAEYALT_ESSENCE;
        } else {
            essenceID = ItemID.PURE_ESSENCE;
        }

        Bank.withdrawAll(essenceID);
    }

    private void bankForEssence(SharedContext sharedContext) {
        boolean daeyalt = sharedContext.getConfig().useDaeyalt();
        EssPouch small = EssPouch.SMALL;
        EssPouch medium = EssPouch.MEDIUM;
        EssPouch large = EssPouch.LARGE;
        EssPouch giant = EssPouch.GIANT;
        EssPouch colossal = EssPouch.COLOSSAL;


        if (sharedContext.isUsingSmallPouch() && small.getAmount() < small.maxAmount() ) {
            small.fill();
        }

        if (sharedContext.isUsingGiantPouch() && giant.getAmount() < giant.maxAmount() ) {
            withdrawEssence(daeyalt);
            giant.fill();
        }

        if (sharedContext.isUsingMediumPouch() && medium.getAmount() < medium.maxAmount() ) {
            withdrawEssence(daeyalt);
            medium.fill();
        }


        if (sharedContext.isUsingLargePouch() && large.getAmount() < large.maxAmount() ) {
            withdrawEssence(daeyalt);
            large.fill();
        }

        if (sharedContext.isUsingColossalPouch() && colossal.getAmount() < colossal.maxAmount() ) {
            withdrawEssence(daeyalt);
            colossal.fill();
        }

        fullSendWithdraw(daeyalt);

        sharedContext.checkTotalEssencesInInv();
        sharedContext.essenceInBank();
    }

    private void depositRunes(AIORCConfig config, SharedContext sharedContext) {
        int runeId = sharedContext.getCurrentlyCrafting().getItemID();
        if (Bank.Inventory.contains(runeId) && config.bankCraftedRunes()) {
           Bank.depositAll(runeId);
        }
    }

    private void withdrawAndEquipDuelingRing(SharedContext sharedContext) {
        if (sharedContext.isUsingDuelingRings() && !sharedContext.checkForDuelingRing()) {
            Bank.withdraw(ItemID.RING_OF_DUELING8, 1);

            if(Equipment.fromSlot(EquipmentSlot.RING) == null &&
                    Bank.Inventory.contains(ItemID.RING_OF_DUELING8)) {
                Bank.Inventory.getFirst(ItemID.RING_OF_DUELING8).interact("Wear");
            }
        }
    }

    private void withdrawAndEquipGlory(SharedContext sharedContext) {
        if (sharedContext.isUsingGlories() && !Bank.Inventory.contains(ItemID.AMULET_OF_GLORY) &&
                !Equipment.contains(ItemID.AMULET_OF_GLORY)) {
            Bank.withdraw(ItemID.AMULET_OF_GLORY6, 1);

            if(Equipment.fromSlot(EquipmentSlot.AMULET) == null &&
                    Bank.Inventory.contains(ItemID.AMULET_OF_GLORY6)) {
                Bank.Inventory.getFirst(ItemID.AMULET_OF_GLORY6).interact("Wear");
            }
        }

        if (Bank.Inventory.contains(ItemID.AMULET_OF_GLORY)) {
            Bank.deposit(ItemID.AMULET_OF_GLORY, 1);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        SharedContext context = stateMachine.getContext();
        AIORCConfig config = stateMachine.getContext().getConfig();

        if (!Bank.isOpen()) {
            OpenBank(config.bank());
        }

        if (Bank.isOpen()) {
            depositRunes(config, context);
            withdrawAndEquipDuelingRing(context);
            withdrawAndEquipGlory(context);
            bankForTalisman(context);
            bankForBindingNecklace(context);
            withdrawAndDrinkStamina(context);

            if(context.maxEssenceCapacity() >= context.getTotalEssencesInInv()) {
                bankForEssence(context);
            }
        }

        if(context.maxEssenceCapacity() == context.getTotalEssencesInInv()) {
            Bank.close();
            stateMachine.setState(new RepairPouch(), false);
        }
    }

    @Override
    public States getStateName() {
        return States.Banking;
    }
}