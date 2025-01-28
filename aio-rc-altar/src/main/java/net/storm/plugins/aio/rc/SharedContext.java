package net.storm.plugins.aio.rc;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemVariationMapping;
import net.storm.plugins.aio.rc.enums.*;
import net.storm.sdk.game.Skills;
import net.storm.sdk.game.Vars;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.Inventory;

import java.text.DecimalFormat;
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
    private Integer staminaDoses = 0; // done
    private Integer essenceInBank = 0; // done
    private Integer bindingNecklacesInBank = 0; // done
    private Integer essencesTraded = 0;
    private Integer runesCrafted = 0; // done
    private Integer expGained = 0; // done
    private double  estimatedGpEarned = 0;
    private Integer totalEssencesInInv = 0; // done
    private Integer duelingRingsInBank = 0; // done
    private Integer gloriesInBank = 0; // done
    private Integer runeNeededForComboRunesId; // done
    private Integer talismanNeededForComboRunes; // done
    private Integer talismansRemaining; // done
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

    private long startTime;
    private long totalElapsedTime = 0;
    private boolean isTimeTracking = false;


    @Getter
    private AIORCConfig config;

    public SharedContext (AIORCConfig config){ this.config = config;}

    public void start() {
        if (!isTimeTracking) {
            this.startTime = System.currentTimeMillis();
            this.isTimeTracking = true;
        }
    }

    public void pause() {
        if (isTimeTracking) {
            this.totalElapsedTime += System.currentTimeMillis() - startTime;
            this.isTimeTracking = false;
        }
    }

    public long getElapsedTimeSeconds() {
        if (isTimeTracking) {
            return (totalElapsedTime + (System.currentTimeMillis() - startTime)) / 1000;
        } else {
            return totalElapsedTime / 1000;
        }
    }

    public String formatTime() {
        long totalTime = this.getElapsedTimeSeconds();

        long hours = totalTime / 3600;
        long minutes = (totalTime % 3600) / 60;
        long seconds = (totalTime % 60);

        // Format as HH:MM:SS.mmm with leading zeros
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
            this.gloriesInBank = Bank.getCount(true, ItemID.RING_OF_DUELING8);
        }
    }

    public void checkIfHatIsCatalytic() {
        if(Equipment.contains(ItemID.HAT_OF_THE_EYE, ItemID.HAT_OF_THE_EYE_BLUE, ItemID.HAT_OF_THE_EYE_GREEN,
                ItemID.HAT_OF_THE_EYE_RED)) {
            this.isHatOfTheEyeCatalytic = Vars.getBit(13709) == 15;
        }
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
        return Inventory.contains(EssPouch.COLOSSAL.getBrokenItemID(), EssPouch.GIANT.getBrokenItemID(),
                EssPouch.LARGE.getBrokenItemID(), EssPouch.MEDIUM.getBrokenItemID());
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
                    talismanNeededForComboRunes = this.config.airCombo().getOppositeTalismanId();
                }
            }
        }

        if (this.config.runes().name().equals("EARTH")) {
            if (this.config.earthCombo() != EarthRunes.EARTH_RUNE) {
                runeNeededForComboRunesId = this.config.earthCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanNeededForComboRunes = this.config.earthCombo().getOppositeTalismanId();
                }
            }
        }

        if (this.config.runes().name().equals("FIRE")) {
            if (this.config.fireCombo() != FireRunes.FIRE_RUNE) {
                this.runeNeededForComboRunesId = this.config.fireCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    talismanNeededForComboRunes = this.config.fireCombo().getOppositeTalismanId();
                }
            }
        }

        if (this.config.runes().name().equals("WATER")) {
            if (this.config.waterCombo() != WaterRunes.WATER_RUNES) {
                this.runeNeededForComboRunesId = this.config.waterCombo().getOppositeRuneId();
                if(!this.config.bringBindingNecklace()) {
                    this.talismanNeededForComboRunes = this.config.waterCombo().getOppositeTalismanId();
                }
            }
        }
    }

    public void checkRequiredTalismansInBank() {
        this.talismansRemaining = Bank.getCount(true, this.talismanNeededForComboRunes);
    }

    public int talismansToWithdraw() {
        if (this.talismansRemaining != null && Inventory.getFreeSlots() != 0) {
            int maxEssence = maxEssenceCapacity();
            return (int) Math.ceil((double) maxEssence / Inventory.getFreeSlots());
        }

        return 0;
    }

    public void checkCurrentRuneBeingCrafted() {
        if (this.config.runes().name().equals("AIR")) {
            this.currentlyCrafting = this.config.airCombo().getRune();
        } else if (this.config.runes().name().equals("EARTH")) {
            this.currentlyCrafting = this.config.earthCombo().getRune();
        } else if (this.config.runes().name().equals("FIRE")) {
            this.currentlyCrafting = this.config.fireCombo().getRune();
        } else if (this.config.runes().name().equals("WATER")) {
            this.currentlyCrafting = this.config.waterCombo().getRune();
        } else {
            this.currentlyCrafting = this.config.runes().getRune();
        }
    }

    public String calculateRatePerHour(long amount) {
        double elapsedTimeHours = (double) getElapsedTimeSeconds() / 3600;

        if (elapsedTimeHours == 0) {
            return "0k";
        }

        double rate = (amount / elapsedTimeHours) / 1000;

        DecimalFormat df = new DecimalFormat("#.00K");

        return df.format(rate);
    }

    public boolean checkForBrokenPouch() {
        return Inventory.contains(EssPouch.GIANT.getBrokenItemID(), EssPouch.LARGE.getBrokenItemID(),
                EssPouch.MEDIUM.getBrokenItemID(), EssPouch.COLOSSAL.getBrokenItemID());
    }

    public void checkStaminaDoses() {
        this.staminaDoses = Bank.getCount(true, ItemID.STAMINA_POTION4) * 4;
        this.staminaDoses += Bank.getCount(true, ItemID.STAMINA_POTION3) * 3;
        this.staminaDoses += Bank.getCount(true, ItemID.STAMINA_POTION2) * 2;
        this.staminaDoses += Bank.getCount(true, ItemID.STAMINA_POTION1);
    }
}