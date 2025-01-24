package net.storm.plugins.examples.looped.states;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.INPC;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.examples.looped.*;
import net.storm.plugins.examples.looped.enums.EssPouch;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.sdk.entities.NPCs;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.GameThread;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.utils.MessageUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SolveAbyss implements StateMachineInterface {
    private boolean clickedObject = false;
    private AtomicInteger ticks = new AtomicInteger(0);

    @Subscribe
    private void onGameTick(GameTick tick) {
        if(clickedObject) {
            ticks.incrementAndGet();
        }
    }

    private void findClosestObstacleAndPass(SharedContext context) {
        IPlayer localPlayer = Players.getLocal();
        WorldPoint myWorldPoint = localPlayer.getWorldArea().toWorldPoint();
        Map<String, ITileObject> objects = new HashMap<>();
        objects.put("Tendrils", TileObjects.getNearest( x -> x.hasAction("Chop") && x.getName().equals("Tendrils")));
        objects.put("Rock", TileObjects.getNearest(x -> x.hasAction("Mine") && x.getName().equals("Rock")));
        objects.put("Eyes", TileObjects.getNearest(x -> x.hasAction("Distract") && x.getName().equals("Eyes")));
        objects.put("Boil", TileObjects.getNearest(x -> x.hasAction("Boil") && x.getName().equals("Boil")));
        objects.put("Gap", TileObjects.getNearest(x -> x.hasAction("Squeeze-through") && x.getName().equals("Gap")));
        objects.put("Passage", TileObjects.getNearest( x -> x.hasAction("Pass-through") && x.getName().equals("Passage")));

        Set<String> allowedObjects = getStringSet(context);

        ITileObject closestObject = objects.entrySet().stream()
                .filter(entry -> allowedObjects.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(obj -> obj.distanceTo(myWorldPoint)))
                .orElse(null);

        if (closestObject != null) {
            GameThread.invokeAndWait(() -> closestObject.interact(0));
            this.clickedObject = true;
        } else {
            MessageUtils.addMessage("Couldn't find a passage... tping to bank");
            Bank.open(context.getConfig().bank().getBankLocation());
        }
    }

    private static Set<String> getStringSet(SharedContext context) {
        Set<String> allowedObjects = new HashSet<>();

        if (context.getConfig().abyssEyes()) {
            allowedObjects.add("Eyes");
        }

        if (context.getConfig().abyssBoil()) {
            allowedObjects.add("Boil");
        }

        if (context.getConfig().abyssGap()) {
            allowedObjects.add("Gap");
        }

        if (context.getConfig().abyssRock()) {
            allowedObjects.add("Rock");
        }

        if (context.getConfig().abyssPassage()) {
            allowedObjects.add("Passage");
        }

        if (context.getConfig().abyssTendrils()) {
            allowedObjects.add("Tendrils");
        }
        return allowedObjects;
    }

    @Override
    public void handleState(StateMachine stateMachine, States state) {
        WorldPoint myWorldPoint = Players.getLocal().getWorldArea().toWorldPoint();
        ITileObject abyssalRift = TileObjects.getNearest("Abyssal Rift");
        ExampleLoopedConfig config = stateMachine.getContext().getConfig();

        if(!clickedObject && abyssalRift != null && abyssalRift.distanceTo(myWorldPoint) > 14) {
            findClosestObstacleAndPass(stateMachine.getContext());
        }

        if (ticks.get() > 50) {
            MessageUtils.addMessage("Something went wrong, let's try again :)");
            ticks.set(0);
            clickedObject = false;
        }

        if (abyssalRift != null && abyssalRift.distanceTo(myWorldPoint) < 14) {
            ITileObject runeRift = TileObjects.getNearest(x -> x.getId() == config.runes().getAbyssRiftID());
            INPC darkMage = NPCs.getNearest(x -> x.hasAction("Repairs"));

            System.out.println(darkMage.getName());

            if(Inventory.contains(EssPouch.COLOSSAL.getBrokenItemID(), EssPouch.GIANT.getBrokenItemID(),
                    EssPouch.LARGE.getBrokenItemID(), EssPouch.MEDIUM.getBrokenItemID())
                    && !Players.getLocal().isInteracting() && config.repairOnDarkMage() && config.useAbyss()) {
                darkMage.interact("Repairs");
            } else if (runeRift != null && !Players.getLocal().isMoving()) {
                runeRift.interact("Exit-through");
            }
        }
    }

    @Override
    public States getStateName() {
        return States.SolveAbyss;
    }
}
