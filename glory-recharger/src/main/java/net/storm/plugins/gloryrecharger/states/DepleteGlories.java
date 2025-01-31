package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.storm.api.domain.actors.IPlayer;
import net.storm.plugins.commons.enums.RunningState;
import net.storm.plugins.commons.utils.TpJewelry;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
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

    TpJewelry tpJewelry = new TpJewelry();

    private void gloryTp(){
        IPlayer localPlayer = Players.getLocal();
        if(!localPlayer.isAnimating() && !Dialog.isOpen()) {
            Inventory.getFirst(tpJewelry.getGloryIds()).interact("Rub");
        } else if (Dialog.isOpen()) {
            int randomNumber = (int) (Math.random() * 4) + 1;

            Dialog.chooseOption(randomNumber);
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        IPlayer localPlayer = Players.getLocal();
        boolean containsTpableGlories = Inventory.contains(tpJewelry.getGloryIds());

        if(Inventory.contains(ItemID.AMULET_OF_ETERNAL_GLORY) && config.stopOnEternal()) {
            context.setCurrentRunningState(RunningState.STOPPED);
        }

        if(containsTpableGlories) {
            if (Inventory.getCount(false, tpJewelry.getGloryIds()) > 1) {
                gloryTp();
            } else if (!Inventory.contains(ItemID.AMULET_OF_GLORY1)) {
                gloryTp();
            } else {
                if(!localPlayer.isAnimating() && !Dialog.isOpen()) {
                    Inventory.getFirst(ItemID.AMULET_OF_GLORY1).interact("Rub");
                } else if (Dialog.isOpen()) {
                    // Make sure last tp is Edgeville, probably not needed but I don't think it matters
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