package net.storm.plugins.commons.utils;

import net.runelite.api.ItemID;
import net.storm.api.domain.items.IBankInventoryItem;
import net.storm.api.domain.items.IItem;
import net.storm.plugins.commons.enums.RunningState;
import net.storm.sdk.items.Bank;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.utils.MessageUtils;

import java.awt.*;
import java.util.function.Predicate;

public class BankUtils {

    public BankUtils() {}

    public void withdrawAndInteract(int itemID, String... actions) {
        if (!Bank.isOpen()) return;

        if (Bank.Inventory.contains(itemID)) {
            var item = Bank.Inventory.getFirst(itemID);
            if (item != null) item.interact(actions);
        } else if (Bank.contains(itemID)) {
            Bank.withdraw(itemID, 1);
        }
    }

    public void withdrawAndInteract(Predicate<IItem> filter, String... actions) {
        if (!Bank.isOpen()) return;

        if (Bank.Inventory.contains(filter)) {
            var item = Bank.Inventory.getFirst(filter);
            if (item != null) item.interact(actions);
        } else if (Bank.contains(filter)) {
            Bank.withdraw(filter, 1);
        }
    }

    public void withdrawAndConsume(int itemID) {
        withdrawAndInteract(itemID, "Eat", "Drink");
    }

    public void withdrawAndConsume(Predicate<IItem> filter) {
        withdrawAndInteract(filter, "Eat", "Drink");
    }

    public void withdrawAndEquip(int itemID) {
        withdrawAndInteract(itemID, "Wield", "Wear", "Equip");
    }

    public void withdrawAndEquip(Predicate<IItem> filter) {
        withdrawAndInteract(filter, "Wield", "Wear", "Equip");
    }

    public void depositAll(int itemID) {
        if (!Bank.isOpen()) return;

        if (Bank.Inventory.contains(itemID)) {
            Bank.depositAll(itemID);
        }
    }

    public void depositAll(Predicate<IItem> filter) {
        if (!Bank.isOpen()) return;

        if (Bank.Inventory.contains(filter)) {
            Bank.depositAll(filter);
        }
    }

    public void withdrawAndDrinkStamina() {
        withdrawAndConsume(i -> i.getName() != null && i.getName().contains("Stamina potion"));
    }

    public void depositStamina() {
        depositAll(i -> i.getName() != null && i.getName().contains("Stamina potion"));
    }

    public void setNotedWithdrawMode(boolean notedWithdrawMode) {
        if (Bank.isNotedWithdrawMode() != notedWithdrawMode) {
            Bank.setWithdrawMode(notedWithdrawMode);
        }
    }


}
