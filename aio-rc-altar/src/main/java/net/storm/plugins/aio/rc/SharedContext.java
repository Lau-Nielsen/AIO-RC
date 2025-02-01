package net.storm.plugins.aio.rc;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemVariationMapping;
import net.storm.plugins.aio.rc.enums.*;
import net.storm.plugins.commons.utils.TrackingUtils;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;
import net.storm.plugins.commons.enums.RunningState;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@Singleton
public class SharedContext {
    private Integer chargesOnRingOfElement = 0; // done
    private AtomicInteger tripsCompleted = new AtomicInteger(0); // done
    private Integer staminaDosesInBank = 0; // done
    private Integer essenceInBank = 0; // done
    private Integer talismansInBank; // done
    private Integer bindingNecklacesInBank = 0; // done
    private Integer gloriesInBank = 0; // done
    private Integer duelingRingsInBank = 0; // done
    private Integer essencesTraded = 0;
    private Integer runesCrafted = 0; // done
    private Integer expGained = 0; // done
    private double  estimatedGpEarned = 0;
    private Integer totalEssencesInInv = 0; // done
    private Integer oppositeRuneIDForComboRune; // done
    private Integer talismanIDNeededForComboRune; // done
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
    private Runes currentlyCrafting; // done
    private List<String> tradeOrder = new ArrayList<>(); // done
    private String currentState; // done;
    private RunningState currentRunningState = RunningState.AWAITING_START; // done;

    @Getter
    TrackingUtils trackingUtils = new TrackingUtils();

    @Getter
    private AIORCConfig config;

    public SharedContext (AIORCConfig config){ this.config = config;}

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

    public void checkEssenceInBank() {
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
            this.duelingRingsInBank = Bank.getCount(true, ItemID.RING_OF_DUELING8);
        }
    }

    public void checkIfHatIsCatalytic() {
        if(Equipment.contains(ItemID.HAT_OF_THE_EYE, ItemID.HAT_OF_THE_EYE_BLUE, ItemID.HAT_OF_THE_EYE_GREEN,
                ItemID.HAT_OF_THE_EYE_RED)) {
            int hatOfTheEyeAtonementID = 13709;
            this.isHatOfTheEyeCatalytic = Vars.getBit(hatOfTheEyeAtonementID) == 15;
        }

        System.out.println(this.isHatOfTheEyeCatalytic);
    }

    public boolean arePouchesFull() {
        EssPouch small = EssPouch.SMALL;
        EssPouch medium = EssPouch.MEDIUM;
        EssPouch large = EssPouch.LARGE;
        EssPouch giant = EssPouch.GIANT;
        EssPouch colossal = EssPouch.COLOSSAL;

        if (this.isUsingSmallPouch() && small.maxAmount() != small.getAmount()) {
            return false;
        }

        if (this.isUsingMediumPouch() && medium.maxAmount() != medium.getAmount()) {
            return false;
        }

        if (this.isUsingLargePouch() && large.maxAmount() != large.getAmount()) {
            return false;
        }

        if (this.isUsingGiantPouch() && giant.maxAmount() != giant.getAmount()) {
            return false;
        }

        if (this.isUsingColossalPouch() && colossal.maxAmount() != colossal.getAmount()) {
            return false;
        }

        return true;
    }

    public boolean hasBrokenPouch() {
        return Inventory.contains(EssPouch.COLOSSAL.getBrokenPouchId(), EssPouch.GIANT.getBrokenPouchId(),
                EssPouch.LARGE.getBrokenPouchId(), EssPouch.MEDIUM.getBrokenPouchId());
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
        if (this.config.altar().name().equals("AIR")) {
            if (this.config.airCombo() != AirRunes.AIR_RUNE) {
                oppositeRuneIDForComboRune = this.config.airCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanIDNeededForComboRune = this.config.airCombo().getOppositeTalismanId();
                }
            }
        }

        if (this.config.altar().name().equals("EARTH")) {
            if (this.config.earthCombo() != EarthRunes.EARTH_RUNE) {
                oppositeRuneIDForComboRune = this.config.earthCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanIDNeededForComboRune = this.config.earthCombo().getOppositeTalismanId();
                }
            }
        }

        if (this.config.altar().name().equals("FIRE")) {
            if (this.config.fireCombo() != FireRunes.FIRE_RUNE) {
                this.oppositeRuneIDForComboRune = this.config.fireCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanIDNeededForComboRune = this.config.fireCombo().getOppositeTalismanId();
                }
            }
        }

        if (this.config.altar().name().equals("WATER")) {
            if (this.config.waterCombo() != WaterRunes.WATER_RUNES) {
                this.oppositeRuneIDForComboRune = this.config.waterCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    this.talismanIDNeededForComboRune = this.config.waterCombo().getOppositeTalismanId();
                }
            }
        }
    }

    public void checkRequiredTalismansInBank() {
        this.talismansInBank = Bank.getCount(true, this.talismanIDNeededForComboRune);
    }

    public int talismansToWithdraw() {
        if (this.talismansInBank != null && Inventory.getFreeSlots() != 0) {
            int maxEssence = maxEssenceCapacity();
            return (int) Math.ceil((double) maxEssence / Inventory.getFreeSlots());
        }

        return 0;
    }

    public void checkCurrentRuneBeingCrafted() {
        if (this.config.altar().name().equals("AIR")) {
            this.currentlyCrafting = this.config.airCombo().getRune();
        } else if (this.config.altar().name().equals("EARTH")) {
            this.currentlyCrafting = this.config.earthCombo().getRune();
        } else if (this.config.altar().name().equals("FIRE")) {
            this.currentlyCrafting = this.config.fireCombo().getRune();
        } else if (this.config.altar().name().equals("WATER")) {
            this.currentlyCrafting = this.config.waterCombo().getRune();
        } else {
            this.currentlyCrafting = this.config.altar().getRune();
        }
    }

    public boolean checkForBrokenPouch() {
        return Inventory.contains(EssPouch.GIANT.getBrokenPouchId(), EssPouch.LARGE.getBrokenPouchId(),
                EssPouch.MEDIUM.getBrokenPouchId(), EssPouch.COLOSSAL.getBrokenPouchId());
    }

    public void checkStaminaDoses() {
        this.staminaDosesInBank = Bank.getCount(true, ItemID.STAMINA_POTION4) * 4;
        this.staminaDosesInBank += Bank.getCount(true, ItemID.STAMINA_POTION3) * 3;
        this.staminaDosesInBank += Bank.getCount(true, ItemID.STAMINA_POTION2) * 2;
        this.staminaDosesInBank += Bank.getCount(true, ItemID.STAMINA_POTION1);
    }
}