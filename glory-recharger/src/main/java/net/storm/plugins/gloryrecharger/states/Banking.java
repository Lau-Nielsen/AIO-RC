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
        int animatingObeliskID = 14825;
        int obeliskDestinationVarbit = 4966;

        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 15,o -> o != null && o.hasAction("Activate"));

        if(!Movement.isWalking() && obelisk == null && TileObjects.getNearest(animatingObeliskID) == null && context.wildyLevel() > 20) {
            Movement.walkTo(rougesCastle);
        }

        if(obelisk != null || TileObjects.getNearest(animatingObeliskID) != null && !localPlayer.isAnimating()) {
            if (Vars.getBit(Varbits.DIARY_WILDERNESS_HARD) == 1) {
                if (context.wildyLevel() >= 20) {
                    if (Vars.getBit(obeliskDestinationVarbit) != Obelisk.FEROX_SE.getVarbitValue()) {
                        context.setDestinationToFerox();
                    } else {
                        context.teleportToDestination();
                    }
                } else {
                    Bank.open(config.bank().getBankLocation());
                }
            } else {
                if(context.wildyLevel() > 20) {
                    if (TileObjects.getNearest(animatingObeliskID) == null) {
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

    private void withdrawGETransportItem() {
        Bank.depositInventory();
        switch(config.geTransportation()) {
            case GLORY:
                Bank.withdraw(ItemID.AMULET_OF_GLORY6, 1);
                break;
            case POH_TAB:
                Bank.withdraw(ItemID.TELEPORT_TO_HOUSE, 1);
                break;
            case RING_OF_WEALTH:
                Bank.withdraw(ItemID.RING_OF_WEALTH_5, 1);
                break;
            case VARROCK_TAB:
                Bank.withdraw(ItemID.VARROCK_TELEPORT, 1);
                break;
            case WALKER:
                break;
        }

        // Withdraw a dueling ring to get back to FEROX
        if(config.bank() == Banks.FEROX_ENCLAVE_BANK) {
            Bank.withdraw(i -> i.getName() != null && i.getName().contains("Ring of dueling"), 1);
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
            } else if(config.restockStaminas()) {
                withdrawGETransportItem();
                statemachine.setState(new GERestock(context, stam_1), false);
            } else {
                context.setCurrentRunningState(RunningState.STOPPED);
                MessageUtils.addMessage("Out of staminas, stopping plugin", Color.red);
            }
        }
    }

    private void withdrawAndEquipWildySword() {
        if(config.fountainTransport() == FountainTransportation.WILDERNESS_SWORD && !Equipment.contains(ItemID.WILDERNESS_SWORD_4)) {
            if(!Bank.contains(ItemID.WILDERNESS_SWORD_4)) {
                MessageUtils.addMessage("Out of wildy swords, buy more at Perdu, stopping plugin", Color.red);
                context.setCurrentRunningState(RunningState.STOPPED);
            }

            if(Bank.Inventory.contains(ItemID.WILDERNESS_SWORD_4)) {
                Bank.Inventory.getFirst(ItemID.WILDERNESS_SWORD_4).interact("Wield");
            } else {
                Bank.withdraw(ItemID.WILDERNESS_SWORD_4, 1);
            }
        }
    }

    private void withdrawAndEquipCraftingCape() {
        if(config.bank() == Banks.CRAFTING_GUILD && !Equipment.contains(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET)) {
            if(Bank.Inventory.contains(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET)) {
                Bank.Inventory.getFirst(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET).interact("Wear");
            } else {
                Bank.withdraw(e -> e.getName() != null &&  e.getName().contains("Crafting cape"), 1);
            }
        }
    }

    private void withdrawAnnakarlTPRunes(StateMachine stateMachine) {
        if(config.fountainTransport() == FountainTransportation.ANNAKARL_TP &&
                Inventory.getCount(true, ItemID.LAW_RUNE) < 2 && Inventory.getCount(true, ItemID.BLOOD_RUNE) < 2) {
            if(Bank.contains(ItemID.LAW_RUNE) && Bank.getCount(true, ItemID.LAW_RUNE) >= 2 &&
                    Bank.contains(ItemID.BLOOD_RUNE) && Bank.getCount(true, ItemID.BLOOD_RUNE) >= 2) {
                Bank.withdraw(ItemID.LAW_RUNE,1);
                Bank.withdraw(ItemID.LAW_RUNE,1);
                Bank.withdraw(ItemID.BLOOD_RUNE,1);
                Bank.withdraw(ItemID.BLOOD_RUNE,1);
            } else if (config.restockRunes()) {
                withdrawGETransportItem();
                stateMachine.setState(new GERestock(context, ItemID.LAW_RUNE), false);
            } else {
                MessageUtils.addMessage("Out of runes, stopping plugin", Color.red);
                context.setCurrentRunningState(RunningState.STOPPED);
            }
        }
    }

    private void withdrawAnnakarlTabs(StateMachine stateMachine) {
        if(config.fountainTransport() == FountainTransportation.ANNAKARL_TABLET && !Inventory.contains(ItemID.ANNAKARL_TELEPORT)) {
            if(Bank.contains(ItemID.ANNAKARL_TELEPORT)) {
                Bank.withdraw(ItemID.ANNAKARL_TELEPORT,1);
            } else if (config.restockTabs()){
                withdrawGETransportItem();
                stateMachine.setState(new GERestock(context, ItemID.ANNAKARL_TELEPORT), false);
            }
        }
    }

    private void withdrawGlories(StateMachine stateMachine) {
        if(Bank.Inventory.getCount(false, ItemID.AMULET_OF_GLORY) >= context.getConfig().gloriesToBring()){
            Bank.depositAll(ItemID.AMULET_OF_GLORY);
        }

        if (!Bank.contains(ItemID.AMULET_OF_GLORY) || Bank.getCount(true, ItemID.AMULET_OF_GLORY) < context.getConfig().gloriesToBring()) {
            if(config.restockGlories()) {
                withdrawGETransportItem();
                stateMachine.setState(new GESell(context), false);
            } else {
                MessageUtils.addMessage("Out of glories, stopping plugin", Color.red);
                context.setCurrentRunningState(RunningState.STOPPED);
            }
        } else {
            Bank.withdraw(ItemID.AMULET_OF_GLORY, context.getConfig().gloriesToBring());
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

            if(Bank.Inventory.contains(i -> i.getName() != null && i.getName().contains("Ring of wealth"))) {
                Bank.depositAll(i -> i.getName() != null && i.getName().contains("Ring of wealth"));
            }

            if(Bank.Inventory.contains(i -> i.getName() != null && i.getName().contains("Ring of dueling"))) {
                Bank.depositAll(i -> i.getName() != null && i.getName().contains("Ring of dueling"));
            }

            if(Bank.Inventory.contains(ItemID.AMULET_OF_GLORY6)) {
                Bank.depositAll(ItemID.AMULET_OF_GLORY6);
            }

            withdrawAndDrinkStamina(stateMachine);

            withdrawAndEquipWildySword();

            withdrawAndEquipCraftingCape();

            withdrawAnnakarlTPRunes(stateMachine);

            withdrawAnnakarlTabs(stateMachine);

            withdrawGlories(stateMachine);

            context.checkStock();
        }

        if(Bank.Inventory.getCount(false, ItemID.AMULET_OF_GLORY) == context.getConfig().gloriesToBring()) {
            if(config.useFeroxPool()) {
                stateMachine.setState(new UseFeroxPool(context), false);
            } else {
                stateMachine.setState(new WalkToFountain(context), true);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.Banking;
    }
}
