package net.storm.plugins.aio.rc.states;

import lombok.Setter;
import net.storm.api.magic.SpellBook;
import net.storm.api.widgets.InterfaceAddress;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.enums.Banks;
import net.storm.sdk.items.Bank;
import net.storm.sdk.magic.Magic;
import net.storm.sdk.widgets.Dialog;
import net.storm.sdk.widgets.Widgets;

public class RepairPouch implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    public RepairPouch(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    @Setter
    private static boolean waitingForDialog = false;

    @Override
    public void handleState(StateMachine stateMachine) {
        if(!Bank.isOpen()) {
            if (context.hasBrokenPouch()) {
                if (SpellBook.Lunar.NPC_CONTACT.canCast()) {
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
                if(context.maxEssenceCapacity() != context.getTotalEssencesInInv()) {
                    stateMachine.setState(new Banking(context), false);
                } else if (config.usePoolAtFerox() && config.bank() == Banks.FEROX_ENCLAVE_BANK) {
                    stateMachine.setState(new UseFeroxPool(context), true);
                } else {
                    stateMachine.setState(new WalkToAltar(context), true);
                }
            }
        } else {
            Bank.close();
        }
    }

    @Override
    public States getStateName() {
        return States.RepairPouches;
    }
}