package net.storm.plugins.aio.rc.states;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.domain.actors.INPC;
import net.storm.api.domain.actors.IPlayer;
import net.storm.api.domain.tiles.ITileObject;
import net.storm.plugins.aio.rc.AIORCConfig;
import net.storm.plugins.aio.rc.SharedContext;
import net.storm.plugins.aio.rc.StateMachine;
import net.storm.plugins.aio.rc.StateMachineInterface;
import net.storm.plugins.aio.rc.enums.Altar;
import net.storm.plugins.aio.rc.enums.EssPouch;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.sdk.entities.NPCs;
import net.storm.sdk.entities.Players;
import net.storm.sdk.entities.TileObjects;
import net.storm.sdk.game.GameThread;
import net.storm.sdk.items.Bank;
import net.storm.sdk.items.Inventory;
import net.storm.sdk.movement.Movement;
import net.storm.sdk.utils.MessageUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class WalkToAltar implements StateMachineInterface {
    private final SharedContext context;
    private final AIORCConfig config;

    public WalkToAltar(final SharedContext context) {
        this.context = context;
        this.config = context.getConfig();
    }

    private AtomicInteger counter = new AtomicInteger(0);
    private boolean startCounting = false;

    @Subscribe
    private void onGameTick(GameTick tick) {
        if(startCounting) {
            counter.incrementAndGet();
        }
    }

    private void findClosestObstacleAndPass() {
        IPlayer localPlayer = Players.getLocal();
        WorldPoint myWorldPoint = localPlayer.getWorldArea().toWorldPoint();
        Map<String, ITileObject> objects = new HashMap<>();
        objects.put("Tendrils", TileObjects.getNearest( x -> x.hasAction("Chop") && x.getName().equals("Tendrils")));
        objects.put("Rock", TileObjects.getNearest(x -> x.hasAction("Mine") && x.getName().equals("Rock")));
        objects.put("Eyes", TileObjects.getNearest(x -> x.hasAction("Distract") && x.getName().equals("Eyes")));
        objects.put("Boil", TileObjects.getNearest(x -> x.hasAction("Boil") && x.getName().equals("Boil")));
        objects.put("Gap", TileObjects.getNearest(x -> x.hasAction("Squeeze-through") && x.getName().equals("Gap")));
        objects.put("Passage", TileObjects.getNearest( x -> x.hasAction("Pass-through") && x.getName().equals("Passage")));

        Set<String> allowedObjects = getStringSet();

        ITileObject closestObject = objects.entrySet().stream()
                .filter(entry -> allowedObjects.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(obj -> obj.distanceTo(myWorldPoint)))
                .orElse(null);

        if (closestObject != null) {
            GameThread.invokeAndWait(() -> closestObject.interact(0));
            if (localPlayer.isMoving()) {
                this.startCounting = true;
            }
        } else {
            MessageUtils.addMessage("Couldn't find a passage... tping to bank");
            Bank.open(context.getConfig().bank().getBankLocation());
        }
    }

    private Set<String> getStringSet() {
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

    public void enterRuins(Altar altar) {
        IPlayer localPlayer = Players.getLocal();
        if(localPlayer != null && altar.getRuinID() != null) {
            ITileObject ruins = TileObjects.getFirstSurrounding(localPlayer.getWorldArea().toWorldPoint(), 20, altar.getRuinID());
            if(ruins != null) {
                if(ruins.getId() == altar.getRuinID()) {
                    ruins.interact("Enter");
                    startCounting = true;
                }
            }
        }
    }

    @Override
    public void handleState(StateMachine stateMachine) {
        Altar altar = config.runes();
        boolean closeToAltar = TileObjects.getFirstSurrounding(Players.getLocal().getWorldArea().toWorldPoint(), 20, altar.getAltarID()) != null;
        IPlayer localPlayer = Players.getLocal();

        if (closeToAltar) {
            if(config.isRunner()) {
                stateMachine.setState(new RunnerTradePlayer(context), true);
            } else {
                stateMachine.setState(new CraftRunes(context), true);
            }
        }

        if(localPlayer.isAnimating() && localPlayer.getAnimation() == 829) {
            localPlayer.setAnimation(-1);
        }

        if (!config.useAbyss()) {
            if (!Movement.isWalking() && !closeToAltar && !startCounting) {
                altar.walkToAltar();
            }

            if (!closeToAltar && counter.get() % 5 == 0) {
                enterRuins(altar);
            }
        } else {
            WorldPoint myWorldPoint = Players.getLocal().getWorldArea().toWorldPoint();
            INPC zamorakMage = NPCs.getNearest(x -> x.hasAction("Teleport"));
            ITileObject abyssalRift = TileObjects.getNearest("Abyssal Rift");
            boolean isAbyssalRiftNull = abyssalRift == null;
            boolean isInteractingWithCuck = false;

            if (localPlayer.isInteracting()) {
                isInteractingWithCuck = localPlayer.getInteracting().getId() == 2581;
            }

            if(!Movement.isWalking() && !isInteractingWithCuck && zamorakMage == null && isAbyssalRiftNull) {
                Movement.walkTo(new WorldArea(3105,3559,5,5,0));
            }

            if(zamorakMage != null && !isInteractingWithCuck && zamorakMage.getAnimation() != 1818 && isAbyssalRiftNull) {
                zamorakMage.interact("Teleport");
            }

            if(!startCounting && abyssalRift != null && abyssalRift.distanceTo(myWorldPoint) > 14) {
                findClosestObstacleAndPass();
            }

            if (counter.get() > 50) {
                MessageUtils.addMessage("Something went wrong, let's try again :)");
                counter.set(0);
                startCounting = false;
            }

            if (abyssalRift != null && abyssalRift.distanceTo(myWorldPoint) < 14) {
                ITileObject runeRift = TileObjects.getNearest(x -> x.getId() == config.runes().getAbyssRiftID());
                INPC darkMage = NPCs.getNearest(x -> x.hasAction("Repairs"));

                if(context.hasBrokenPouch()
                        && !Players.getLocal().isInteracting() && config.repairOnDarkMage() && config.useAbyss()) {
                    darkMage.interact("Repairs");
                } else if (runeRift != null && !Players.getLocal().isMoving()) {
                    runeRift.interact("Exit-through");
                }
            }
        }
    }

    @Override
    public States getStateName() {
        return States.WalkToAltar;
    }
}
