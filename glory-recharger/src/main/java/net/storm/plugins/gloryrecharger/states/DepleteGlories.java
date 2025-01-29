package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.storm.api.domain.actors.IPlayer;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.RunningState;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.widgets.Dialog;

public class DepleteGlories implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;

    public DepleteGlories(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private void gloryTp(){
        IPlayer localPlayer = Players.getLocal();
        if(!localPlayer.isAnimating() && !Dialog.isOpen()) {
            Inventory.getFirst(ItemID.AMULET_OF_GLORY6, ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY4, ItemID.AMULET_OF_GLORY3, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY1).interact("Rub");
        } else if (Dialog.isOpen()) {
            int randomNumber = (int) (Math.random() * 4) + 1;

            Dialog.chooseOption(randomNumber);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        IPlayer localPlayer = Players.getLocal();
        boolean containsTpableGlories = Inventory.contains(ItemID.AMULET_OF_GLORY6, ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY4, ItemID.AMULET_OF_GLORY3, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY1);

        if(Inventory.contains(ItemID.AMULET_OF_ETERNAL_GLORY) && config.stopOnEternal()) {
            context.setCurrentRunningState(RunningState.STOPPED);
        }

        if(containsTpableGlories) {
            if (Inventory.getCount(false, ItemID.AMULET_OF_GLORY6, ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY4, ItemID.AMULET_OF_GLORY3, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY1) > 1) {
                gloryTp();
            } else if (!Inventory.contains(ItemID.AMULET_OF_GLORY1)) {
                gloryTp();
            } else {
                if(!localPlayer.isAnimating() && !Dialog.isOpen()) {
                    Inventory.getFirst(ItemID.AMULET_OF_GLORY1).interact("Rub");
                } else if (Dialog.isOpen()) {
                    Dialog.chooseOption("Edgeville");
                }
            }
        } else {
            stateMachine.setState(new WalkToFountain(context), true);
        }
    }

    @Override
    public States getStateName() {
        return States.DepleteGlories;
    }
}