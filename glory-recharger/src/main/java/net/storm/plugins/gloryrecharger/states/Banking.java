package net.storm.plugins.gloryrecharger.states;

import com.google.inject.spi.Message;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.Banks;
import net.storm.plugins.gloryrecharger.enums.RunningState;
import net.storm.plugins.gloryrecharger.enums.States;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Game;
import net.storm.sdk.game.Vars;
import net.storm.sdk.input.Keyboard;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.utils.MessageUtils;
import net.storm.sdk.widgets.Widgets;

import java.awt.*;
import java.util.List;

public class Banking implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;

    public Banking(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private WorldArea calculateMiddleOfObelisk() {
        int sumX = 0;
        int sumY = 0;
        List<ITileObject> obelisk = TileObjects.getAll(o -> o.getName() != null && (o.getId() == 14825 || o.getName().equals("Obelisk")));

        for (ITileObject o : obelisk) {
            sumX += o.getWorldX();
            sumY += o.getWorldY();
        }

        System.out.println(obelisk.size());
        System.out.println(sumX / 4 + " " + sumY / 4);

        return new WorldArea(sumX / 4, sumY / 4, 1, 1, 0);
    }

    private void teleportToDestination() {
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));
        if (TileObjects.getNearest(14825) == null) {
            obelisk.interact("Teleport to Destination");
        } else {
            Movement.walkTo(calculateMiddleOfObelisk());
        }
    }

    private void setDestination() {
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));

        if (Widgets.isVisible(12255235)) {
            Keyboard.type(1);
        } else {
            obelisk.interact("Set Destination");
        }
    }

    private void obeliskHomeRoute() {
        WorldArea rougesCastle = new WorldArea(3305, 3915, 3, 3, 0);
        IPlayer localPlayer = Players.getLocal();
        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10,o -> o != null && o.hasAction("Activate"));

        if(!Movement.isWalking() && obelisk == null && TileObjects.getNearest(14825) == null) {
            Movement.walkTo(rougesCastle);
        }

        if(obelisk != null || TileObjects.getNearest(14825) != null && !localPlayer.isAnimating()) {
            if (Vars.getBit(Varbits.DIARY_WILDERNESS_HARD) == 1) {
                if (context.wildyLevel() >= 20) {
                    if (config.bank() == Banks.FEROX_ENCLAVE_BANK || Vars.getBit(4966) == 0) {
                        if(Vars.getBit(4966) == 4 || Vars.getBit(4966) == 5) {
                            teleportToDestination();
                        } else {
                            setDestination();
                        }
                    } else {
                        if (Vars.getBit(4966) != 4) {
                            setDestination();
                        } else {
                            teleportToDestination();
                        }
                    }
                } else {
                    Bank.open(config.bank().getBankLocation());
                }
            } else {
                if(context.wildyLevel() > 20) {
                    if (TileObjects.getNearest(14825) == null) {
                        obelisk.interact("Activate");
                    } else {
                        Movement.walkTo(calculateMiddleOfObelisk());
                    }
                } else {
                    Bank.open(config.bank().getBankLocation());
                }
            }
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

    @Override
    public void handleState(StateMachine stateMachine) {
        context.hopCheck();

        if(!Bank.isOpen() && !Movement.isWalking()) {
            if(config.useObelisk() && Game.isInWilderness()) {
                obeliskHomeRoute();
            } else {
                Bank.open(context.getConfig().bank().getBankLocation());
            }
        }

        if (!Game.isInWilderness() && config.depleteInsteadOfBanking()) {
            stateMachine.setState(new DepleteGlories(context), true);
        }

        if(Bank.isOpen()) {
            if(config.stopOnEternal() && Inventory.contains(ItemID.AMULET_OF_ETERNAL_GLORY)) {
                context.setCurrentRunningState(RunningState.STOPPED);
            } else {
                Bank.depositAll(ItemID.AMULET_OF_ETERNAL_GLORY);
            }

            if(Bank.Inventory.contains(ItemID.AMULET_OF_GLORY5)) {
                Bank.depositAll(ItemID.AMULET_OF_GLORY5);
            }

            if(Bank.Inventory.contains(ItemID.AMULET_OF_GLORY6)) {
                Bank.depositAll(ItemID.AMULET_OF_GLORY6);
            }

            withdrawAndDrinkStamina();

            if (!Bank.contains(ItemID.AMULET_OF_GLORY)) {
                MessageUtils.addMessage("Out of glories, stopping plugin", Color.red);
            }
        }

        if(Bank.Inventory.getCount(false, ItemID.AMULET_OF_GLORY) == context.getConfig().gloriesToBring()) {
            if(config.useFeroxPool()) {
                stateMachine.setState(new UseFeroxPool(context), true);
            } else {
                stateMachine.setState(new WalkToFountain(context), true);
            }
        } else if(Bank.Inventory.getCount(false, ItemID.AMULET_OF_GLORY) >= context.getConfig().gloriesToBring()){
            Bank.depositAll(ItemID.AMULET_OF_GLORY);
        } else {
            Bank.withdraw(ItemID.AMULET_OF_GLORY, context.getConfig().gloriesToBring());
        }
    }

    @Override
    public States getStateName() {
        return States.Banking;
    }
}
