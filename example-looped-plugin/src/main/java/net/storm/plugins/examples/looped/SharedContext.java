package net.storm.plugins.examples.looped;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemVariationMapping;
import net.storm.plugins.examples.looped.enums.*;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;

import java.util.Collection;

@Setter
@Getter
public class SharedContext {
    private Integer chargesOnRingOfElement; // done
    private Integer tripsCompleted = 0;
    private Integer staminaDoses; // done
    private Integer essenceInBank; // done
    private Integer bindingNecklacesInBank; // done
    private Integer essencesTraded = 0;
    private Integer runesCrafted = 0;
    private Integer expGained = 0;
    private Integer totalEssencesInInv = 0; // done
    private Integer duelingRingsInBank; // done
    private Integer gloriesInBank; // done
    private Integer runeNeededForComboRunesId; // done
    private Integer talismanNeededForComboRunes; // done
    private boolean isUsingDuelingRings; // done
    private boolean isUsingGlories; // done
    private boolean isUsingEternalGlory; // done
    private boolean isUsingRingOfElements; // done
    private boolean isUsingSmallPouch; // done
    private boolean isUsingMediumPouch; // done
    private boolean isUsingLargePouch; // done
    private boolean isUsingGiantPouch; // done
    private boolean isUsingColossalPouch; // done
    private boolean isHatOfTheEyeCatalytic; // done


    private ExampleLoopedConfig config;

    public SharedContext (ExampleLoopedConfig config) {
        this.config = config;
    }

    public boolean checkForDuelingRing() {
        Collection<Integer> ringIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RING_OF_DUELING8));

        boolean isInEquipment = Equipment.contains(i -> ringIds.contains(i.getId()));;
        boolean isInInv = Inventory.contains(i -> ringIds.contains(i.getId()));

        return isInEquipment || isInInv;
    }

    public boolean checkForGlories() {
        Collection<Integer> amuletIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.AMULET_OF_GLORY6));

        boolean isInEquipment = Equipment.contains(i -> amuletIds.contains(i.getId()));;
        boolean isInInv = Inventory.contains(i -> amuletIds.contains(i.getId()));

        return isInEquipment || isInInv;
    }

    public boolean checkForEternalGlory() {
        boolean isInEquipment = Equipment.contains(ItemID.AMULET_OF_ETERNAL_GLORY);;
        boolean isInInv = Inventory.contains(ItemID.AMULET_OF_ETERNAL_GLORY);

        return isInEquipment || isInInv;
    }

    public boolean checkForRingOfElements() {
        boolean isInEquipment = Equipment.contains(ItemID.RING_OF_THE_ELEMENTS_26818);;
        boolean isInInv = Inventory.contains(ItemID.RING_OF_THE_ELEMENTS_26818);

        return isInEquipment || isInInv;
    }

    public boolean checkForSmallPouch() {
        return Inventory.contains(ItemID.SMALL_POUCH);
    }

    public boolean checkForMediumPouch() {
        Collection<Integer> pouchIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MEDIUM_POUCH));
        return Inventory.contains(i -> pouchIds.contains(i.getId()));
    }

    public boolean checkForLargePouch() {
        Collection<Integer> pouchIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.LARGE_POUCH));
        return Inventory.contains(i -> pouchIds.contains(i.getId()));
    }

    public boolean checkForGiantPouch() {
        Collection<Integer> pouchIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.GIANT_POUCH));
        return Inventory.contains(i -> pouchIds.contains(i.getId()));
    }

    public boolean checkForColossalPouch() {
        Collection<Integer> pouchIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.COLOSSAL_POUCH));
        return Inventory.contains(i -> pouchIds.contains(i.getId()));
    }

    public void checkBindingNecklacesInBank() {
        if(Bank.isOpen()) {
            this.bindingNecklacesInBank = Bank.getCount(true, ItemID.BINDING_NECKLACE);
        }
    }

    public void checkChargesOnRote() {
        this.chargesOnRingOfElement = Vars.getBit(13707);
    }

    public void essenceInBank() {
        if(Bank.isOpen()) {
            if(config.useDaeyalt()) {
                this.essenceInBank = Bank.getCount(true, ItemID.DAEYALT_ESSENCE);
            } else {
                this.essenceInBank = Bank.getCount(true, ItemID.PURE_ESSENCE);
            }
        }
    }

    public void checkTotalEssencesInInv() {
        this.totalEssencesInInv = 0;
        if (this.isUsingSmallPouch) {
            this.totalEssencesInInv += EssPouch.SMALL.getAmount();
        }
        if(this.isUsingMediumPouch) {
            this.totalEssencesInInv += EssPouch.MEDIUM.getAmount();
        }
        if(this.isUsingLargePouch) {
            this.totalEssencesInInv += EssPouch.LARGE.getAmount();
        }
        if(this.isUsingGiantPouch) {
            this.totalEssencesInInv += EssPouch.GIANT.getAmount();
        }
        if(this.isUsingColossalPouch) {
            this.totalEssencesInInv += EssPouch.COLOSSAL.getAmount();
        }

        this.totalEssencesInInv += Inventory.getCount(false, ItemID.PURE_ESSENCE);
    }

    public void checkGloriesInBank() {
        if(Bank.isOpen()) {
            this.gloriesInBank = Bank.getCount(true, ItemID.AMULET_OF_GLORY6);
        }
    }

    public void checkDuelingRingsInBank() {
        if(Bank.isOpen()) {
            this.gloriesInBank = Bank.getCount(true, ItemID.RING_OF_DUELING8);
        }
    }

    public void checkIfHatIsCatalytic() {
        if(Equipment.contains(ItemID.HAT_OF_THE_EYE, ItemID.HAT_OF_THE_EYE_BLUE, ItemID.HAT_OF_THE_EYE_GREEN,
                ItemID.HAT_OF_THE_EYE_RED)) {
            this.isHatOfTheEyeCatalytic = Vars.getBit(13709) == 15;
        }
    }

    public int maxEssenceCapacity() {
        int capacity = 0;
        if (this.isUsingSmallPouch() ) {
            capacity += EssPouch.SMALL.maxAmount();
        }

        if (this.isUsingMediumPouch() ) {
            capacity += EssPouch.MEDIUM.maxAmount();
        }

        if (this.isUsingLargePouch()) {
            capacity += EssPouch.LARGE.maxAmount();
        }

        if (this.isUsingGiantPouch()) {
            capacity += EssPouch.GIANT.maxAmount();
        }

        if (this.isUsingColossalPouch()) {
            capacity += EssPouch.COLOSSAL.maxAmount();
        }

        capacity += Inventory.getCount(false, ItemID.PURE_ESSENCE);
        capacity += Inventory.getFreeSlots();

        return capacity;
    }

    public void setComboRuneRequirementIds() {
        if (this.config.runes().name().equals("AIR")) {
            if (this.config.airCombo() != AirRunes.AIR_RUNE) {
                runeNeededForComboRunesId = this.config.airCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanNeededForComboRunes = this.config.airCombo().getOppositeRuneId();
                }
            }
        }

        if (this.config.runes().name().equals("EARTH")) {
            if (this.config.earthCombo() != EarthRunes.EARTH_RUNE) {
                runeNeededForComboRunesId = this.config.earthCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanNeededForComboRunes = this.config.earthCombo().getOppositeRuneId();
                }
            }
        }

        if (this.config.runes().name().equals("FIRE")) {
            if (this.config.fireCombo() != FireRunes.FIRE_RUNE) {
                runeNeededForComboRunesId = this.config.fireCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanNeededForComboRunes = this.config.fireCombo().getOppositeRuneId();
                }
            }
        }

        if (this.config.runes().name().equals("WATER")) {
            if (this.config.waterCombo() != WaterRunes.WATER_RUNES) {
                runeNeededForComboRunesId = this.config.waterCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanNeededForComboRunes = this.config.waterCombo().getOppositeRuneId();
                }
            }
        }
    }

    public void checkStaminaDoses() {
        this.staminaDoses = Bank.getCount(true, ItemID.STAMINA_POTION4) * 4;
        this.staminaDoses += Bank.getCount(true, ItemID.STAMINA_POTION3) * 3;
        this.staminaDoses += Bank.getCount(true, ItemID.STAMINA_POTION2) * 2;
        this.staminaDoses += Bank.getCount(true, ItemID.STAMINA_POTION1);
    }
}