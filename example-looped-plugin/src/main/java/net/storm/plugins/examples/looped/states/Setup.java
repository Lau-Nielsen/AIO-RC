package net.storm.plugins.examples.looped.states;

import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.storm.api.domain.Identifiable;
import net.storm.api.domain.items.IItem;
import net.storm.api.items.loadouts.LoadoutItem;
import net.storm.api.magic.SpellBook;
import net.storm.plugins.examples.looped.*;
import net.storm.plugins.examples.looped.enums.Altar;
import net.storm.plugins.examples.looped.enums.Banks;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.game.Skills;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.quests.Quests;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Setup implements StateMachineInterface {
    private boolean forceAddressErrors = false;

    private void questCheck(Quest quest, Altar altar, ExampleLoopedConfig config) {
        if (config.runes() == altar && !Quests.isFinished(quest)) {
            MessageUtils.addMessage("You cannot  " +
                            (config.isRunner() ? "run essences to " + altar.name()  : "craft " + altar.name() + " runes") +
                            " without completion of" +
                            quest.getName()
                    , Color.RED);

            forceAddressErrors = true;
        }
    }

    private boolean hasPickaxe() {
        List<Integer> pickaxes = Arrays.asList(ItemID.BRONZE_PICKAXE, ItemID.IRON_PICKAXE, ItemID.STEEL_PICKAXE, ItemID.BLACK_PICKAXE,
                ItemID.MITHRIL_PICKAXE, ItemID.ADAMANT_PICKAXE, ItemID.RUNE_PICKAXE, ItemID.DRAGON_PICKAXE, ItemID.DRAGON_PICKAXE_OR,
                ItemID.DRAGON_PICKAXE_OR_25376, ItemID.DRAGON_PICKAXE_OR_30351, ItemID.CRYSTAL_PICKAXE, ItemID.GILDED_PICKAXE);

        return Inventory.getAll().stream().anyMatch(item -> pickaxes.contains(item.getId())) ||
                Equipment.getAll().stream().anyMatch(item -> pickaxes.contains(item.getId()));
    }

    private boolean hasAxe() {
        List<Integer> axes = Arrays.asList(ItemID.BRONZE_AXE, ItemID.IRON_AXE, ItemID.STEEL_AXE, ItemID.BLACK_AXE,
                ItemID.MITHRIL_AXE, ItemID.ADAMANT_AXE, ItemID.RUNE_AXE, ItemID.DRAGON_AXE, ItemID.INFERNAL_AXE,
                ItemID.DRAGON_AXE_OR, ItemID.DRAGON_AXE_OR_30352, ItemID.INFERNAL_AXE_OR, ItemID.INFERNAL_AXE_OR_30347, ItemID.CRYSTAL_AXE,
                ItemID.GILDED_AXE);

        return Inventory.getAll().stream().anyMatch(item -> axes.contains(item.getId())) ||
                Equipment.getAll().stream().anyMatch(item -> axes.contains(item.getId()));
    }

    private boolean hasTinderBox() {
        List<Integer> tinderboxes = Arrays.asList(ItemID.TINDERBOX, ItemID.TINDERBOX_7156);

        return Inventory.getAll().stream().anyMatch(item -> tinderboxes.contains(item.getId())) ||
                Equipment.getAll().stream().anyMatch(item -> tinderboxes.contains(item.getId()));
    }

    private void abyssCheck(ExampleLoopedConfig config) {
        if(config.useAbyss()) {
            if(config.abyssRock() && !hasPickaxe()) {
                MessageUtils.addMessage("Deselect rocks from abyss, or add a pickaxe to your loadout.", Color.red);
                forceAddressErrors = true;
            }

            if(config.abyssTendrils() && !hasAxe()) {
                MessageUtils.addMessage("Deselect tendrils from abyss, or add an axe to your loadout.", Color.red);
                forceAddressErrors = true;
            }

            if(config.abyssBoil() && !hasTinderBox()) {
                MessageUtils.addMessage("Deselect boil from abyss, or add a tinderbox to your loadout.", Color.red);
                forceAddressErrors = true;
            }

            if(!Quests.isFinished(Quest.ENTER_THE_ABYSS)) {
                MessageUtils.addMessage("Complete Enter the Abyss before using the Abyss.", Color.red);
                forceAddressErrors = true;
            }
        }

    }

    private void imbueCheck(ExampleLoopedConfig config) {
        if(config.useImbue()) {
            if(!SpellBook.Lunar.MAGIC_IMBUE.haveRunesAvailable()) {
                MessageUtils.addMessage("You're either missing runes to cast Imbue add them to the loadout.", Color.red);
                forceAddressErrors = true;
            }
            if(SpellBook.getCurrent() != SpellBook.LUNAR) {
                MessageUtils.addMessage("You're not on the Lunar spell book to cast Imbue.", Color.red);
                forceAddressErrors = true;
            }
            if(Skills.getLevel(Skill.MAGIC) < SpellBook.Lunar.MAGIC_IMBUE.getLevel()) {
                MessageUtils.addMessage("You do not have the required Magic level to cast Imbue.", Color.red);
                forceAddressErrors = true;
            }
            if (!Quests.isFinished(Quest.LUNAR_DIPLOMACY)) {
                MessageUtils.addMessage("Missing Lunar diplomacy completion to cast Imbue.", Color.red);
                forceAddressErrors = true;
            }
        }
    }

    private void npcContactCheck(ExampleLoopedConfig config) {
        List<Integer> rcPerkCapes = Arrays.asList(ItemID.MAX_CAPE, ItemID.RUNECRAFT_CAPE, ItemID.RUNECRAFT_CAPET, ItemID.MAX_CAPE_13342);
        boolean rcCapePerk = false;
        IItem myCape = Equipment.get(LoadoutItem.Slot.CAPE.getSlotIndex());

        if(myCape != null) {
            rcCapePerk = rcPerkCapes.contains(myCape.getId());
        }

        if(!rcCapePerk || !(config.useAbyss() && config.repairOnDarkMage())) {
            if(!SpellBook.Lunar.NPC_CONTACT.haveRunesAvailable()) {
                MessageUtils.addMessage("You're either missing runes to cast NPC Contact add them to the loadout", Color.ORANGE);
                MessageUtils.addMessage("If you want your pouches repaired", Color.ORANGE);
                forceAddressErrors = true;
            }
            if(SpellBook.getCurrent() != SpellBook.LUNAR) {
                MessageUtils.addMessage("You're not on the Lunar spell book to cast NPC Contact.", Color.ORANGE);
                forceAddressErrors = true;
            }
            if(Skills.getLevel(Skill.MAGIC) < SpellBook.Lunar.MAGIC_IMBUE.getLevel()) {
                MessageUtils.addMessage("You do not have the required Magic level to cast NPC Contact.", Color.ORANGE);
                forceAddressErrors = true;
            }
            if (!Quests.isFinished(Quest.LUNAR_DIPLOMACY)) {
                MessageUtils.addMessage("Missing Lunar diplomacy completion to cast NPC Contact.", Color.ORANGE);
                forceAddressErrors = true;
            }
        }
    }

    private void insufficientRcLevel(String pouch) {
        MessageUtils.addMessage("You do not have the runecrafting level to use: " + pouch, Color.RED);
        forceAddressErrors = true;
    }

    private void essencePouchCheck(ExampleLoopedConfig config, SharedContext sharedContext) {
        Collection<LoadoutItem> items = config.loadout().getItems();
        int rcLevel = Skills.getLevel(Skill.RUNECRAFT);

        if (items.stream().anyMatch( item -> item.getId() == ItemID.SMALL_POUCH)) {
            sharedContext.setUsingSmallPouch(true);
        }

        if (items.stream().anyMatch( item -> item.getId() == ItemID.MEDIUM_POUCH)) {
            if(rcLevel < 25) {
                insufficientRcLevel("Medium Pouch");
            } else {
                sharedContext.setUsingMediumPouch(true);
            }
        }
        if (items.stream().anyMatch( item -> item.getId() == ItemID.LARGE_POUCH)) {
            if(rcLevel < 50) {
                insufficientRcLevel("Large Pouch");
            } else {
                sharedContext.setUsingLargePouch(true);
            }

        }

        if (items.stream().anyMatch( item -> item.getId() == ItemID.GIANT_POUCH)) {
            if(rcLevel < 75) {
                insufficientRcLevel("Giant Pouch");
            } else {
                sharedContext.setUsingGiantPouch(true);
            }
        }

        boolean pouchMismatch = sharedContext.isUsingGiantPouch() || sharedContext.isUsingSmallPouch() ||
                sharedContext.isUsingMediumPouch() || sharedContext.isUsingLargePouch();

        if (items.stream().anyMatch( item -> item.getId() == ItemID.COLOSSAL_POUCH)) {
            if(pouchMismatch) {
                MessageUtils.addMessage("You cannot use normal pouches and colossal at the same time!.", Color.red);
            } else if(rcLevel < 25) {
                insufficientRcLevel("Colossal Pouch");
            } else {
                sharedContext.setUsingColossalPouch(true);
            }
        }
    }

    private void riftAccess(ExampleLoopedConfig config, SharedContext sharedContext) {
        boolean inventoryCheck = false;
        boolean equipmentCheck = false;

        List<Integer> equipmentList = Equipment.getAll().stream().map(Identifiable::getId).collect(Collectors.toList());
        boolean hatTiara = sharedContext.isHatOfTheEyeCatalytic();

        if(hatTiara) {
            equipmentList.add(ItemID.CATALYTIC_TIARA);
        } else {
            equipmentList.add(ItemID.ELEMENTAL_TIARA);
        }

        if(config.runes() == Altar.ASTRAL) {
            inventoryCheck = true;
            equipmentCheck = true;
        } else {
            inventoryCheck = Inventory.getAll().stream().noneMatch(item -> config.runes().getValidTalismanAndTiaraIds().contains(item.getId()));
            equipmentCheck = equipmentList.stream().noneMatch(id -> config.runes().getValidTalismanAndTiaraIds().contains(id));
        }


        if(!config.useAbyss()) {
            if (!inventoryCheck && !equipmentCheck) {
                MessageUtils.addMessage("You do not have a talisman or tiara that gives you access to: " + config.runes().name());
                forceAddressErrors = true;
            }
        }
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        forceAddressErrors = false;
        ExampleLoopedConfig config = stateMachine.getContext().getConfig();

        questCheck(Quest.MOURNINGS_END_PART_II, Altar.DEATH, config);
        questCheck(Quest.LUNAR_DIPLOMACY, Altar.ASTRAL, config);
        questCheck(Quest.SINS_OF_THE_FATHER, Altar.BLOOD, config);
        questCheck(Quest.LOST_CITY, Altar.COSMIC, config);
        questCheck(Quest.CHILDREN_OF_THE_SUN, Altar.SUNFIRE, config);
        questCheck(Quest.DRAGON_SLAYER_II, Altar.WRATH, config);

        abyssCheck(config);
        stateMachine.getContext().setComboRuneRequirementIds();
        imbueCheck(config);
        npcContactCheck(config);
        essencePouchCheck(config, stateMachine.getContext());
        riftAccess(config, stateMachine.getContext());

        if(!forceAddressErrors) {
            if(config.usePoolAtFerox() && config.bank() == Banks.FEROX_ENCLAVE_BANK && config.poolBeforeBank()) {
                stateMachine.setState(new UseFeroxPool(), true);
            } else {
                stateMachine.setState(new Banking(), false);
            }
        }

    }

    @Override
    public States getStateName() {
        if (forceAddressErrors) {
            return States.ForceAwaitErrors;
        } else {
            return States.Setup;
        }
    }
}