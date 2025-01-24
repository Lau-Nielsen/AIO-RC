package net.storm.plugins.examples.looped.states;

import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemVariationMapping;
import net.storm.api.items.loadouts.LoadoutItem;
import net.storm.plugins.examples.looped.ExampleLoopedConfig;
import net.storm.plugins.examples.looped.SharedContext;
import net.storm.plugins.examples.looped.StateMachine;
import net.storm.plugins.examples.looped.StateMachineInterface;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Equipment;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class BankSetupAndStock implements StateMachineInterface {
    private AtomicInteger ticks = new AtomicInteger(0);
    Map<Integer, Integer> equipment = new HashMap<>();
    Map<Integer, Integer> inventoryEquipment = new HashMap<>();
    Map<Integer, Integer> withdrawnEquipment = new HashMap<>();
    private boolean checkedForLoadoutIds = false;

    @Subscribe
    private void onGameTick(GameTick tick) {
        ticks.incrementAndGet();
    }

    // takes variants and checks your bank for variants findFirst ASSUMES some things
    private void setEquipmentIds(ExampleLoopedConfig config) {
        Collection<LoadoutItem> items = config.loadout().getItems();
        List<LoadoutItem> equipmentLoadout = items.stream()
                .filter(item -> item.getType() == LoadoutItem.Type.EQUIPMENT)
                .collect(Collectors.toList());

        for (LoadoutItem loadoutItem : equipmentLoadout) {
            Collection<Integer> variationIds = ItemVariationMapping.getVariations(ItemVariationMapping.map(loadoutItem.getId()));

            variationIds.stream()
                    .filter(i -> (Bank.contains(i) || Bank.Inventory.contains(i)))
                    .findFirst().ifPresent(id -> equipment.put(id, loadoutItem.getQuantity()));
        }

        equipment.forEach((id, quantity) -> System.out.println("EQUIPMENT: " + id + " " + quantity));
    }

    // Inventory ids shouldn't differ
    private void setInventoryIds(ExampleLoopedConfig config) {
        Collection<LoadoutItem> items = config.loadout().getItems();

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

    private void withDrawLoadoutFromBank(ExampleLoopedConfig config) {
        AtomicInteger actionCount = new AtomicInteger(0);

        Random random = new Random();
        int maxActions = random.nextInt(config.maxActionsPerTick() - config.minActionsPerTick() + 1 ) + config.minActionsPerTick();

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
    public void handleState(StateMachine stateMachine, States state) {
        SharedContext context = stateMachine.getContext();
        ExampleLoopedConfig config = context.getConfig();

        if(!Bank.isOpen() && !checkedForLoadoutIds) {
            Bank.open(config.bank().getBankLocation());
        }

        if(Bank.isOpen()) {
            if(Equipment.getAll().isEmpty() && Bank.Inventory.getAll().isEmpty()){
                if (!checkedForLoadoutIds) {
                    setEquipmentIds(config);
                    setInventoryIds(config);
                    this.checkedForLoadoutIds = true;
                }
            } else if (!checkedForLoadoutIds) {
                Bank.depositInventory();
                Bank.depositEquipment();
            }

            if(!equipment.isEmpty() || !withdrawnEquipment.isEmpty() || !inventoryEquipment.isEmpty()) {
                withDrawLoadoutFromBank(config);
            }

            context.checkStaminaDoses();
            context.essenceInBank();
            context.checkBindingNecklacesInBank();

            context.setUsingGlories(context.checkForGlories());
            if(context.isUsingGlories()) {
                context.checkGloriesInBank();
            }

            context.setUsingDuelingRings(context.checkForDuelingRing());
            if(context.isUsingDuelingRings()) {
                context.checkDuelingRingsInBank();
            }

            context.setUsingColossalPouch(context.checkForColossalPouch());
            context.setUsingGiantPouch(context.checkForGiantPouch());
            context.setUsingLargePouch(context.checkForLargePouch());
            context.setUsingMediumPouch(context.checkForMediumPouch());
            context.setUsingSmallPouch(context.checkForSmallPouch());
            context.setUsingEternalGlory(context.checkForEternalGlory());
            context.setUsingRingOfElements(context.checkForRingOfElements());
            context.checkIfHatIsCatalytic();

            if(context.isUsingRingOfElements()) {
                context.checkChargesOnRote();
            }

            if(checkedForLoadoutIds && equipment.isEmpty() &&
                    withdrawnEquipment.isEmpty() && inventoryEquipment.isEmpty()) {

                stateMachine.setState(new Setup(), false);
            }
        }
    }

    @Override
    public States getStateName() {
        return States.BankSetupAndStock;
    }
}
