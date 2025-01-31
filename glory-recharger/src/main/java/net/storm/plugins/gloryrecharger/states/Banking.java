package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.gloryrecharger.GloryRechargerConfig;
import net.storm.plugins.gloryrecharger.SharedContext;
import net.storm.plugins.gloryrecharger.StateMachine;
import net.storm.plugins.gloryrecharger.StateMachineInterface;
import net.storm.plugins.gloryrecharger.enums.*;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.Game;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;

public class Banking implements StateMachineInterface {
    SharedContext context;
    GloryRechargerConfig config;

    public Banking(SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private void obeliskHomeRoute() {
        WorldArea rougesCastle = new WorldArea(3305, 3915, 3, 3, 0);
        IPlayer localPlayer = Players.getLocal();

        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 15,o -> o != null && o.hasAction("Activate"));

        if(!Movement.isWalking() && obelisk == null && TileObjects.getNearest(14825) == null && context.wildyLevel() > 20) {
            Movement.walkTo(rougesCastle);
        }

        if(obelisk != null || TileObjects.getNearest(14825) != null && !localPlayer.isAnimating()) {
            if (Vars.getBit(Varbits.DIARY_WILDERNESS_HARD) == 1) {
                if (context.wildyLevel() >= 20) {
                    if (Vars.getBit(4966) != 4) {
                        context.setDestinationToFerox();
                    } else {
                        context.teleportToDestination();
                    }
                } else {
                    Bank.open(config.bank().getBankLocation());
                }
            } else {
                if(context.wildyLevel() > 20) {
                    if (TileObjects.getNearest(14825) == null) {
                        obelisk.interact("Activate");
                    } else {
                        Movement.walkTo(context.calculateMiddleOfObelisk());
                    }
                } else {
                    Bank.open(config.bank().getBankLocation());
                }
            }
            // Ferox safe zone outside of barriers check
        } else if (Game.isInWilderness() && context.wildyLevel() == 0) {
            Bank.open(config.bank().getBankLocation());
        }
    }

    private void withdrawAndDrinkStamina(StateMachine statemachine) {
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
                if(config.restockStaminas()) {
                    statemachine.setState(new GERestock(context, stam_1), false);
                } else {
                    context.setCurrentRunningState(RunningState.STOPPED);
                    MessageUtils.addMessage("Out of staminas, stopping plugin", Color.red);
                }
            }
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        context.hopCheck();
        context.handleProtectItem();

        if(!Bank.isOpen() && !Movement.isWalking()) {
            if(config.bankTransportation() == BankTransportation.OBELISK && Game.isInWilderness()) {
                obeliskHomeRoute();
            } else {
                Bank.open(context.getConfig().bank().getBankLocation());
            }
        }

        if (!Game.isInWilderness() && config.depleteInsteadOfBanking() && !Bank.isOpen()) {
            stateMachine.setState(new DepleteGlories(context), true);
        }

        if(Bank.isOpen()) {
            if (Bank.isNotedWithdrawMode()) {
                Bank.setWithdrawMode(false);
            }

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

            withdrawAndDrinkStamina(stateMachine);

            if(config.fountainTransport() == FountainTransportation.WILDERNESS_SWORD && !Equipment.contains(ItemID.WILDERNESS_SWORD_4)) {
                if(Bank.Inventory.contains(ItemID.WILDERNESS_SWORD_4)) {
                    Bank.Inventory.getFirst(ItemID.WILDERNESS_SWORD_4).interact("Wield");
                } else {
                    Bank.withdraw(ItemID.WILDERNESS_SWORD_4, 1);
                }
            }

            if(config.bank() == Banks.CRAFTING_GUILD && !Equipment.contains(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET)) {
                if(Bank.Inventory.contains(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET)) {
                    Bank.Inventory.getFirst(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET).interact("Wear");
                } else {
                    Bank.withdraw(e -> e.getName() != null &&  e.getName().contains("Crafting cape"), 1);
                }
            }

            if(config.fountainTransport() == FountainTransportation.ANNAKARL_TP &&
                    Inventory.getCount(true, ItemID.LAW_RUNE) < 2 && Inventory.getCount(true, ItemID.BLOOD_RUNE) < 2) {
                if(Bank.contains(ItemID.LAW_RUNE) && Bank.getCount(true, ItemID.LAW_RUNE) >= 2 &&
                        Bank.contains(ItemID.BLOOD_RUNE) && Bank.getCount(true, ItemID.BLOOD_RUNE) >= 2) {
                    Bank.withdraw(ItemID.LAW_RUNE,1);
                    Bank.withdraw(ItemID.LAW_RUNE,1);
                    Bank.withdraw(ItemID.BLOOD_RUNE,1);
                    Bank.withdraw(ItemID.BLOOD_RUNE,1);
                } else {
                    stateMachine.setState(new GERestock(context, ItemID.LAW_RUNE), false);
                }
            }

            if(config.fountainTransport() == FountainTransportation.ANNAKARL_TABLET && !Inventory.contains(ItemID.ANNAKARL_TELEPORT)) {
               if(Bank.contains(ItemID.ANNAKARL_TELEPORT)) {
                   Bank.withdraw(ItemID.ANNAKARL_TELEPORT,1);
               } else {
                   stateMachine.setState(new GERestock(context, ItemID.ANNAKARL_TELEPORT), false);
               }
            }

            if (!Bank.contains(ItemID.AMULET_OF_GLORY)) {
                if(config.restockGlories()) {
                    stateMachine.setState(new GERestock(context, ItemID.AMULET_OF_GLORY), false);
                } else {
                    MessageUtils.addMessage("Out of glories, stopping plugin", Color.red);
                    context.setCurrentRunningState(RunningState.STOPPED);
                }
            }

            context.checkStock();
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
