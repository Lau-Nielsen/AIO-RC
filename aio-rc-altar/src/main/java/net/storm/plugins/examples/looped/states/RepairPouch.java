package net.storm.plugins.examples.looped.states;

import lombok.Setter;
import net.storm.api.magic.SpellBook;
import net.storm.api.widgets.InterfaceAddress;
import net.storm.plugins.examples.looped.*;
import net.storm.plugins.examples.looped.enums.Banks;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.magic.Magic;
import net.storm.sdk.widgets.Dialog;
import net.storm.sdk.widgets.Widgets;

public class RepairPouch implements StateMachineInterface {
    @Setter
    private static boolean waitingForDialog = false;

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        ExampleLoopedConfig config = stateMachine.getContext().getConfig();
        if (Inventory.contains(26786)) {
            // TODO make sure this check is in SETUP

            if (SpellBook.LUNAR == SpellBook.getCurrent() && SpellBook.Lunar.NPC_CONTACT.canCast()) {
                if (!waitingForDialog) {
                    Magic.cast(SpellBook.Lunar.NPC_CONTACT);

                    Widgets.get(InterfaceAddress.SPELL_NPC_CONTACT).interact("Dark Mage");
                    setWaitingForDialog(true);
                }
            }

            if(Dialog.isOpen()) {
                Dialog.continueSpace();
            }

            if(Dialog.isViewingOptions()) {
                Dialog.chooseOption("Can you repair my pouches?");
            }

        } else {
            setWaitingForDialog(false);

            if (config.usePoolAtFerox() && config.bank() == Banks.FEROX_ENCLAVE_BANK && !config.poolBeforeBank()) {
                stateMachine.setState(new UseFeroxPool(), true);
            } else if (!config.useAbyss()) {
                stateMachine.setState(new WalkToAltar(), false);
            } else if (config.useAbyss()) {
                stateMachine.setState(new EnterAbyss(), false);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.RepairPouches;
    }
}