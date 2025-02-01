package net.storm.plugins.aio.rc.states;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemVariationMapping;
import net.storm.api.items.loadouts.LoadoutItem;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;
import net.storm.sdk.items.loadouts.LoadoutFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BankSetupAndStock implements StateMachineInterface {
    private final SharedContext context;

    public BankSetupAndStock(final SharedContext context) {
        this.context = context;
    }

    Map<Integer, Integer> equipment = new HashMap<>();
    Map<Integer, Integer> inventoryEquipment = new HashMap<>();
    Map<Integer, Integer> withdrawnEquipment = new HashMap<>();
    private boolean checkedForLoadoutIds = false;

    private boolean isEquipmentCorrect() {
        return context.getConfig().loadout().equipmentItemsIds().equals(LoadoutFactory.fromCurrentEquipment().build().equipmentItemsIds());
    };

    // takes variants and checks your bank for variants findFirst ASSUMES some things
    private void setEquipmentIds() {
        Collection<LoadoutItem> items = context.getConfig().loadout().getItems();
        List<LoadoutItem> equipmentLoadout = items.stream()
                .filter(item -> item.getType() == LoadoutItem.Type.EQUIPMENT)
                .collect(Collectors.toList());

        for (LoadoutItem loadoutItem : equipmentLoadout) {
            Collection<Integer> variationIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(loadoutItem.getId()));

            variationIds.stream()
                    .filter(i -> (Bank.contains(i) || Bank.Inventory.contains(i)))
                    .findFirst().ifPresent(id -> equipment.put(id, loadoutItem.getQuantity()));
        }

        if(isEquipmentCorrect()) {
            equipment.clear();
        }
    }

    // Inventory ids shouldn't differ
    private void setInventoryIds() {
        Collection<LoadoutItem> items = context.getConfig().loadout().getItems();

        List<LoadoutItem> inventoryLoadout = items.stream()
                .filter(item -> item.getType() == LoadoutItem.Type.INVENTORY)
                .collect(Collectors.toList());

        inventoryLoadout.forEach(item -> {
            inventoryEquipment.merge(
                    item.getId(),
                    item.getQuantity(),
                    Integer::sum
            );
        });

    }

    private void withdrawLoadoutFromBank() {
        AtomicInteger actionCount = new AtomicInteger(0);

        Random random = new Random();
        int maxActions = random.nextInt(context.getConfig().maxActionsPerTick() - context.getConfig().minActionsPerTick() + 1 ) + context.getConfig().minActionsPerTick();

        // Withdraw equipment ids and if it's in the inventory add it to the withdrawnEquipment
        if(!equipment.isEmpty()) {
            List<Integer> toRemove = new ArrayList<>();
            equipment.forEach((id, quantity) -> {
                if (actionCount.get() < maxActions) {
                    if (id != null && !Equipment.contains(id) && !Bank.Inventory.contains(id)) {
                        Bank.withdraw(id, quantity);
                        actionCount.incrementAndGet();
                    }

                    if (Bank.Inventory.contains(id)) {
                        withdrawnEquipment.put(id, quantity);
                        toRemove.add(id);
                    }
                }
            });

            for (Integer id : toRemove) {
                equipment.remove(id);
            }
        }

        // Equip the withdrawn equipment
        if (!withdrawnEquipment.isEmpty() && equipment.isEmpty() && maxActions > actionCount.get()) {
            List<Integer> toRemove = new ArrayList<>();

            withdrawnEquipment.forEach((id, quantity) -> {
                if (id != null && !Equipment.contains(id) &&
                        Bank.Inventory.contains(id) && maxActions > actionCount.get()) {
                    var item = Bank.Inventory.getFirst(id);
                    item.interact("Wear", "Wield", "Equip");
                    actionCount.incrementAndGet();
                    toRemove.add(id);
                }
            });

            for (Integer id : toRemove) {
                withdrawnEquipment.remove(id);
            }
        }

        if (!inventoryEquipment.isEmpty()) {
            inventoryEquipment.remove(context.getConfig().useDaeyalt() ? ItemID.DAEYALT_ESSENCE : ItemID.PURE_ESSENCE);
        }

        // Withdraw the inventory
        if (inventoryEquipment != null && !inventoryEquipment.isEmpty() &&
                equipment.isEmpty() && withdrawnEquipment.isEmpty() &&
                maxActions > actionCount.get()) {
            List<Integer> toRemove = new ArrayList<>();

            inventoryEquipment.forEach((id, quantity) -> {
                if (!Bank.Inventory.contains(id) && maxActions > actionCount.get()) {
                    Bank.withdraw(id, quantity);
                    actionCount.incrementAndGet();
                }

                if(Bank.Inventory.contains(id)) {
                    toRemove.add(id);
                }
            });

            for (Integer id : toRemove) {
                inventoryEquipment.remove(id);
            }
        }

        actionCount.set(0);
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        if(!Bank.isOpen() && !checkedForLoadoutIds) {
            Bank.open(context.getConfig().bank().getBankLocation());
        }

        if(Bank.isOpen()) {
            if (Bank.isNotedWithdrawMode()) {
                Bank.setWithdrawMode(false);
            }

            if(Bank.Inventory.getAll().isEmpty()){
                if (!checkedForLoadoutIds) {
                    setEquipmentIds();
                    setInventoryIds();
                    this.checkedForLoadoutIds = true;
                }
            } else if (!checkedForLoadoutIds) {
                Bank.depositInventory();
                if (!isEquipmentCorrect()) {
                    Bank.depositEquipment();
                }
            }

            if(!equipment.isEmpty() || !withdrawnEquipment.isEmpty() || !inventoryEquipment.isEmpty()) {
                withdrawLoadoutFromBank();
            }


            // Set stock
            context.checkStaminaDoses();
            context.checkEssenceInBank();
            context.checkBindingNecklacesInBank();
            context.setUsingGlories(context.checkForGlories());
            if(context.isUsingGlories()) {
                context.checkGloriesInBank();
            }
            context.setUsingDuelingRings(context.checkForDuelingRing());
            if(context.isUsingDuelingRings()) {
                context.checkDuelingRingsInBank();
            }

            // Check for equipment flags
            context.setUsingColossalPouch(context.checkForColossalPouch());
            context.setUsingGiantPouch(context.checkForGiantPouch());
            context.setUsingLargePouch(context.checkForLargePouch());
            context.setUsingMediumPouch(context.checkForMediumPouch());
            context.setUsingSmallPouch(context.checkForSmallPouch());
            context.setUsingEternalGlory(context.checkForEternalGlory());

            // Combo rune requirement checks
            context.setComboRuneRequirementIds();
            if(context.getTalismanIDNeededForComboRune() != null && !context.getConfig().bringBindingNecklace()) {
                context.checkRequiredTalismansInBank();
            }
            context.checkIfHatIsCatalytic();

            context.setUsingRingOfElements(context.checkForRingOfElements());
            if(context.isUsingRingOfElements()) {
                context.checkChargesOnRote();
            }

            if(checkedForLoadoutIds && equipment.isEmpty() &&
                    withdrawnEquipment.isEmpty() && inventoryEquipment.isEmpty()) {
                stateMachine.setState(new Setup(context), false);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.BankSetupAndStock;
    }
}
