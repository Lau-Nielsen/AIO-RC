package net.storm.plugins.gloryrecharger.states;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.commons.enums.RunningState;
import net.storm.plugins.commons.utils.BankUtils;
import net.storm.plugins.commons.utils.WildyUtils;
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
    WildyUtils wildyUtils = new WildyUtils();
    BankUtils bankUtils = new BankUtils();

    private void obeliskHomeRoute() {
        WorldArea rougesCastle = new WorldArea(3305, 3915, 3, 3, 0);
        IPlayer localPlayer = Players.getLocal();
        int animatingObeliskID = 14825;
        int obeliskDestinationVarbit = 4966;

        ITileObject obelisk = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 15,o -> o != null && o.hasAction("Activate"));

        if(!Movement.isWalking() && obelisk == null && TileObjects.getNearest(animatingObeliskID) == null && wildyUtils.wildyLevel() > 20) {
            Movement.walkTo(rougesCastle);
            return;
        }

        if((obelisk != null || TileObjects.getNearest(animatingObeliskID) != null) && !localPlayer.isAnimating()) {
            if (Vars.getBit(Varbits.DIARY_WILDERNESS_HARD) == 1) {
                if (wildyUtils.wildyLevel() >= 20) {
                    if (Vars.getBit(obeliskDestinationVarbit) != Obelisk.FEROX_SE.getVarbitValue()) {
                        context.setDestinationToFerox();
                    } else {
                        context.teleportToDestination();
                    }
                } else {
                    Bank.open(config.bank().getBankLocation());
                }
            } else {
                if(wildyUtils.wildyLevel() > 20) {
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
        } else if (Game.isInWilderness() && wildyUtils.wildyLevel() == 0) {
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
        if(config.useStamina()) {
            if(!Bank.contains(i -> i.getName() != null && i.getName().contains("Stamina potion"))) {
                if(config.restockStaminas()) {
                    withdrawGETransportItem();
                    statemachine.setState(new GERestock(context, ItemID.STAMINA_POTION1), false);
                } else {
                    context.setCurrentRunningState(RunningState.STOPPED);
                    MessageUtils.addMessage("Out of staminas, stopping plugin", Color.red);
                }
                return;
            }

            if(Movement.getRunEnergy() <= config.staminaThreshold() && !Movement.isStaminaBoosted()) {
                bankUtils.withdrawAndDrinkStamina();
            }
            bankUtils.depositStamina();
        }
    }

    private void withdrawAndEquipWildySword() {
        if(config.fountainTransport() == FountainTransportation.WILDERNESS_SWORD && !Equipment.contains(ItemID.WILDERNESS_SWORD_4)) {
            if(!Bank.contains(ItemID.WILDERNESS_SWORD_4)) {
                MessageUtils.addMessage("Out of wildy swords, buy more at Perdu, stopping plugin", Color.red);
                context.setCurrentRunningState(RunningState.STOPPED);

                return;
            }

            bankUtils.withdrawAndEquip(ItemID.WILDERNESS_SWORD_4);
        }
    }

    private void withdrawAndEquipCraftingCape() {
        if(config.bank() == Banks.CRAFTING_GUILD && !Equipment.contains(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET)) {
            bankUtils.withdrawAndEquip(e -> e.getName() != null &&  e.getName().contains("Crafting cape"));
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
            } else {
                MessageUtils.addMessage("Out of Annakarl teleports, stopping plugin", Color.red);
                context.setCurrentRunningState(RunningState.STOPPED);
            }
        }
    }

    private void withdrawGlories(StateMachine stateMachine) {
        if(Bank.Inventory.getCount(false, ItemID.AMULET_OF_GLORY) > context.getConfig().gloriesToBring()){
            Bank.depositAll(ItemID.AMULET_OF_GLORY);
        }

        if (!Bank.contains(ItemID.AMULET_OF_GLORY) || (Bank.getCount(true, ItemID.AMULET_OF_GLORY) + Bank.Inventory.getCount(false, ItemID.AMULET_OF_GLORY)) < context.getConfig().gloriesToBring()) {
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
        wildyUtils.hopCheckAndHop(config.hopOnAttackablePlayer());
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
            bankUtils.setNotedWithdrawMode(false);

            if(config.stopOnEternal() && Inventory.contains(ItemID.AMULET_OF_ETERNAL_GLORY)) {
                context.setCurrentRunningState(RunningState.STOPPED);
            }

            bankUtils.depositAll(ItemID.AMULET_OF_ETERNAL_GLORY);
            bankUtils.depositAll(ItemID.AMULET_OF_GLORY5);
            bankUtils.depositAll(ItemID.AMULET_OF_GLORY6);

            bankUtils.depositAll(i -> i.getName() != null && i.getName().contains("Ring of wealth"));
            bankUtils.depositAll(i -> i.getName() != null && i.getName().contains("Ring of dueling"));

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
