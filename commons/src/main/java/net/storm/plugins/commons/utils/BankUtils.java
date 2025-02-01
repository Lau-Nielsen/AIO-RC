package net.storm.plugins.commons.utils;

import net.storm.api.domain.items.IItem;
import net.storm.sdk.items.Bank;

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

    public void deposit(int itemID, int amount) {
        if (!Bank.isOpen()) return;

        if (Bank.Inventory.contains(itemID)) {
            Bank.deposit(itemID, amount);
        }
    }

    public void deposit(Predicate<IItem> filter, int amount) {
        if (!Bank.isOpen()) return;

        if (Bank.Inventory.contains(filter)) {
            Bank.deposit(filter, amount);
        }
    }

    public void withdraw(int itemID, int amount) {
        if (!Bank.isOpen()) return;

        if (Bank.contains(itemID)) {
            Bank.withdraw(itemID, amount);
        }
    }

    public void withdraw(Predicate<IItem> filter, int amount) {
        if (!Bank.isOpen()) return;

        if (Bank.contains(filter)) {
            Bank.withdraw(filter, amount);
        }
    }

    public void withdrawAll(int itemID) {
        if (!Bank.isOpen()) return;

        if (Bank.contains(itemID)) {
            Bank.withdrawAll(itemID);
        }
    }

    public void withdrawAll(Predicate<IItem> filter) {
        if (!Bank.isOpen()) return;

        if (Bank.contains(filter)) {
            Bank.withdrawAll(filter);
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
