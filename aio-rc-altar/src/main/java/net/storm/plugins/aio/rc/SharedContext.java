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

        boolean isInEquipment = Equipment.contains(i -> ringIds.contains(i.getId()));
        boolean isInInv = Inventory.contains(i -> ringIds.contains(i.getId()));

        return isInEquipment || isInInv;
    }

    public boolean checkForGlories() {
        Collection<Integer> amuletIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.AMULET_OF_GLORY6));

        boolean isInEquipment = Equipment.contains(i -> amuletIds.contains(i.getId()));
        boolean isInInv = Inventory.contains(i -> amuletIds.contains(i.getId()));

        return isInEquipment || isInInv;
    }

    public boolean checkForEternalGlory() {
        boolean isInEquipment = Equipment.contains(ItemID.AMULET_OF_ETERNAL_GLORY);
        boolean isInInv = Inventory.contains(ItemID.AMULET_OF_ETERNAL_GLORY);

        return isInEquipment || isInInv;
    }

    public boolean checkForRingOfElements() {
        boolean isInEquipment = Equipment.contains(ItemID.RING_OF_THE_ELEMENTS_26818);
        boolean isInInv = Inventory.contains(ItemID.RING_OF_THE_ELEMENTS_26818);

        return isInEquipment || isInInv;
    }

    public boolean checkForSmallPouch() {
        return Inventory.contains(ItemID.SMALL_POUCH);
    }

    private boolean checkForPouch(int itemId) {
        Collection<Integer> pouchIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(itemId));
        return Inventory.contains(i -> pouchIds.contains(i.getId()));
    }

    public boolean checkForMediumPouch() {
        return checkForPouch(ItemID.MEDIUM_POUCH);
    }

    public boolean checkForLargePouch() {
        return checkForPouch(ItemID.LARGE_POUCH);
    }

    public boolean checkForGiantPouch() {
        return checkForPouch(ItemID.GIANT_POUCH);
    }

    public boolean checkForColossalPouch() {
        return checkForPouch(ItemID.COLOSSAL_POUCH);
    }

    public void checkBindingNecklacesInBank() {
        if(Bank.isOpen()) {
            this.bindingNecklacesInBank = Bank.getCount(true, ItemID.BINDING_NECKLACE);
        }
    }

    public void checkChargesOnRote() {
        int ROTE_CHARGES_VARBIT_ID = 13707;
        this.chargesOnRingOfElement = Vars.getBit(ROTE_CHARGES_VARBIT_ID);
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

        EssPouch[] pouches = {EssPouch.SMALL, EssPouch.MEDIUM, EssPouch.LARGE, EssPouch.GIANT, EssPouch.COLOSSAL};
        boolean[] isUsingPouch = {isUsingSmallPouch, isUsingMediumPouch, isUsingLargePouch, isUsingGiantPouch, isUsingColossalPouch};

        for (int i = 0; i < pouches.length; i++) {
            if (isUsingPouch[i]) {
                this.totalEssencesInInv += pouches[i].getAmount();
            }
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
            int HAT_OF_THE_EYE_VARBIT_ID  = 13709;
            this.isHatOfTheEyeCatalytic = Vars.getBit(HAT_OF_THE_EYE_VARBIT_ID) == 15;
        }
    }

    public boolean arePouchesFull() {
        return (!isUsingSmallPouch || EssPouch.SMALL.isFull())
                && (!isUsingMediumPouch || EssPouch.MEDIUM.isFull())
                && (!isUsingLargePouch || EssPouch.LARGE.isFull())
                && (!isUsingGiantPouch || EssPouch.GIANT.isFull())
                && (!isUsingColossalPouch || EssPouch.COLOSSAL.isFull());
    }

    public boolean hasBrokenPouch() {
        return Inventory.contains(EssPouch.COLOSSAL.getBrokenPouchId(), EssPouch.GIANT.getBrokenPouchId(),
                EssPouch.LARGE.getBrokenPouchId(), EssPouch.MEDIUM.getBrokenPouchId());
    }

    public int maxEssenceCapacity() {
        int capacity = 0;

        EssPouch[] pouches = {EssPouch.SMALL, EssPouch.MEDIUM, EssPouch.LARGE, EssPouch.GIANT, EssPouch.COLOSSAL};
        boolean[] isUsingPouch = {isUsingSmallPouch, isUsingMediumPouch, isUsingLargePouch, isUsingGiantPouch, isUsingColossalPouch};

        for (int i = 0; i < pouches.length; i++) {
            if (isUsingPouch[i]) {
                capacity += pouches[i].maxAmount();
            }
        }

        capacity += Inventory.getCount(false, ItemID.PURE_ESSENCE);
        capacity += Inventory.getFreeSlots();

        return capacity;
    }

    private void setComboRuneRequirementIds(int talismanID, int runeID) {
        this.oppositeRuneIDForComboRune = runeID;
        if (!this.config.bringBindingNecklace()) {
            this.talismanIDNeededForComboRune = talismanID;
        }
    }

    public void checkComboRuneRequirements() {
        switch (this.config.altar()) {
            case AIR:
                setComboRuneRequirementIds(this.config.airCombo().getOppositeTalismanId(), this.config.airCombo().getOppositeRuneId());
                break;
            case EARTH:
                setComboRuneRequirementIds( this.config.earthCombo().getOppositeTalismanId(), this.config.earthCombo().getOppositeRuneId());
                break;
            case FIRE:
                setComboRuneRequirementIds(this.config.fireCombo().getOppositeTalismanId(), this.config.fireCombo().getOppositeRuneId());
                break;
            case WATER:
                setComboRuneRequirementIds(this.config.waterCombo().getOppositeTalismanId(), this.config.waterCombo().getOppositeRuneId());
                break;
            default:
                this.oppositeRuneIDForComboRune = null;
                this.talismanIDNeededForComboRune = null;
                break;
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
        switch (this.config.altar()) {
            case AIR:
                this.currentlyCrafting = this.config.airCombo().getRune();
                break;
            case EARTH:
                this.currentlyCrafting = this.config.earthCombo().getRune();
                break;
            case FIRE:
                this.currentlyCrafting = this.config.fireCombo().getRune();
                break;
            case WATER:
                this.currentlyCrafting = this.config.waterCombo().getRune();
                break;
            default:
                this.currentlyCrafting = this.config.altar().getRune();
                break;
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