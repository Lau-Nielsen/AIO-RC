package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.storm.api.domain.Identifiable;
import net.storm.api.domain.items.IItem;
import net.storm.api.items.loadouts.LoadoutItem;
import net.storm.api.magic.SpellBook;
import net.storm.api.widgets.EquipmentSlot;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.enums.Altar;
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
    private final SharedContext context;
    private final AIORCConfig config;

    public Setup(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private boolean forceAddressErrors = false;

    private void questCheck(Quest quest, Altar altar) {
        if (config.altar() == altar && !Quests.isFinished(quest)) {
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

    private void abyssObstacleRequirementCheck() {
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

            if(!config.abyssBoil() && !config.abyssGap() && !config.abyssRock() &&
                    !config.abyssEyes() && !config.abyssTendrils() && !config.abyssPassage()) {
                MessageUtils.addMessage("Select at least one obstacle to use in the abyss.", Color.red);
                forceAddressErrors = true;
            }
        }

    }

    private void imbueRequirementCheck() {
        if(config.useImbue() && !config.isRunner()) {
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

    private boolean hasRcCapePerk() {
        List<Integer> rcPerkCapes = Arrays.asList(ItemID.MAX_CAPE, ItemID.RUNECRAFT_CAPE, ItemID.RUNECRAFT_CAPET, ItemID.MAX_CAPE_13342);
        boolean rcCapePerk = false;
        IItem myCape = Equipment.get(EquipmentSlot.CAPE.getSlot());

        if(myCape != null) {
            rcCapePerk = rcPerkCapes.contains(myCape.getId());
        }

        return rcCapePerk;
    }

    private void npcContactRequirementCheck() {
        boolean rcCapePerk = hasRcCapePerk();

        if(config.isRunner() || config.isUsingRunners()) {
            return;
        }

        if(!rcCapePerk && !config.useAbyss() && !config.repairOnDarkMage()) {
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

    private void essencePouchRequirementCheck() {
        Collection<LoadoutItem> items = config.loadout().getItems();
        int rcLevel = Skills.getLevel(Skill.RUNECRAFT);

        if (items.stream().anyMatch( item -> item.getId() == ItemID.SMALL_POUCH)) {
            context.setUsingSmallPouch(true);
        }

        if (items.stream().anyMatch( item -> item.getId() == ItemID.MEDIUM_POUCH)) {
            if(rcLevel < 25) {
                insufficientRcLevel("Medium Pouch");
            } else {
                context.setUsingMediumPouch(true);
            }
        }
        if (items.stream().anyMatch( item -> item.getId() == ItemID.LARGE_POUCH)) {
            if(rcLevel < 50) {
                insufficientRcLevel("Large Pouch");
            } else {
                context.setUsingLargePouch(true);
            }

        }

        if (items.stream().anyMatch( item -> item.getId() == ItemID.GIANT_POUCH)) {
            if(rcLevel < 75) {
                insufficientRcLevel("Giant Pouch");
            } else {
                context.setUsingGiantPouch(true);
            }
        }

        boolean pouchMismatch = context.isUsingGiantPouch() || context.isUsingSmallPouch() ||
                context.isUsingMediumPouch() || context.isUsingLargePouch();

        if (items.stream().anyMatch( item -> item.getId() == ItemID.COLOSSAL_POUCH)) {
            if(pouchMismatch) {
                MessageUtils.addMessage("You cannot use normal pouches and colossal at the same time!.", Color.red);
            } else if(rcLevel < 25) {
                insufficientRcLevel("Colossal Pouch");
            } else {
                context.setUsingColossalPouch(true);
            }
        }
    }

    private void checkAltarAccessRequirements() {
        boolean inventoryCheck = false;
        boolean equipmentCheck = false;

        List<Integer> equipmentList = Equipment.getAll().stream().map(Identifiable::getId).collect(Collectors.toList());
        boolean catalyticHat = context.isHatOfTheEyeCatalytic();

        if(hasRcCapePerk()) {
            return;
        }

        if(catalyticHat) {
            equipmentList.add(ItemID.CATALYTIC_TIARA);
        } else {
            equipmentList.add(ItemID.ELEMENTAL_TIARA);
        }

        if(config.altar() == Altar.ASTRAL) {
            inventoryCheck = true;
            equipmentCheck = true;
        } else {
            inventoryCheck = Inventory.getAll().stream().anyMatch(item -> config.altar().getValidTalismanAndTiaraIds().contains(item.getId()));
            equipmentCheck = equipmentList.stream().anyMatch(id -> config.altar().getValidTalismanAndTiaraIds().contains(id));
        }

        if(!config.useAbyss()) {
            if (!inventoryCheck && !equipmentCheck) {
                MessageUtils.addMessage("You do not have a talisman or tiara that gives you access to: " + config.altar().name());
                forceAddressErrors = true;
            }
        }

    }

    @Override
    public void handleState(StateMachine stateMachine) {
        forceAddressErrors = false;

        questCheck(Quest.MOURNINGS_END_PART_II, Altar.DEATH);
        questCheck(Quest.LUNAR_DIPLOMACY, Altar.ASTRAL);
        questCheck(Quest.SINS_OF_THE_FATHER, Altar.BLOOD);
        questCheck(Quest.LOST_CITY, Altar.COSMIC);
        questCheck(Quest.CHILDREN_OF_THE_SUN, Altar.SUNFIRE);
        questCheck(Quest.DRAGON_SLAYER_II, Altar.WRATH);

        abyssObstacleRequirementCheck();
        imbueRequirementCheck();
        npcContactRequirementCheck();
        essencePouchRequirementCheck();
        checkAltarAccessRequirements();

        if(!forceAddressErrors) {
            stateMachine.setState(new Banking(context), true);
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