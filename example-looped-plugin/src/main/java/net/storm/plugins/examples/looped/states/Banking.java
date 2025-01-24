package net.storm.plugins.examples.looped.states;

import lombok.Setter;
import net.runelite.api.ItemID;
import net.storm.plugins.examples.looped.*;
import net.storm.plugins.examples.looped.enums.Banks;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.plugins.examples.looped.enums.EssPouch;
import net.storm.sdk.items.Bank;
import net.storm.sdk.movement.Movement;


@Setter
public class Banking implements StateMachineInterface {
    private void OpenBank(final Banks bank) {
        Bank.open(bank.getBankLocation());
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

        withdrawEssence(daeyalt);

        if (sharedContext.isUsingSmallPouch() && small.getAmount() < small.maxAmount() ) {
            small.fill();
        }

        withdrawEssence(daeyalt);

        if (sharedContext.isUsingMediumPouch() && medium.getAmount() < medium.maxAmount() ) {
            medium.fill();
        }

        withdrawEssence(daeyalt);

        if (sharedContext.isUsingLargePouch() && large.getAmount() < large.maxAmount() ) {
            large.fill();
        }

        withdrawEssence(daeyalt);

        if (sharedContext.isUsingGiantPouch() && giant.getAmount() < giant.maxAmount() ) {
            giant.fill();
        }

        withdrawEssence(daeyalt);

        if (sharedContext.isUsingColossalPouch() && colossal.getAmount() < colossal.maxAmount() ) {
            colossal.fill();
        }

        fullSendWithdraw(daeyalt);

        sharedContext.checkTotalEssencesInInv();
        sharedContext.essenceInBank();
    }


    @Override
    public void handleState(StateMachine stateMachine, States state) {
        SharedContext context = stateMachine.getContext();
        ExampleLoopedConfig config = stateMachine.getContext().getConfig();

        if (state == States.Banking) {
            if (!Bank.isOpen()) {
                OpenBank(config.bank());
            }

            if (Bank.isOpen()) {
                bankForBindingNecklace(context);
                withdrawAndDrinkStamina(context);

                if(context.maxEssenceCapacity() >= context.getTotalEssencesInInv()) {
                    bankForEssence(context);
                }
            }

            // Bank.Inventory.getCount(ItemID.PURE_ESSENCE) > 20
            if(context.maxEssenceCapacity() == context.getTotalEssencesInInv()) {
                Bank.close();
                stateMachine.setState(new RepairPouch(), false);
            }
        } else {
            System.out.println("Invalid event in PAUSED state.");
        }
    }

    @Override
    public States getStateName() {
        return States.Banking;
    }
}