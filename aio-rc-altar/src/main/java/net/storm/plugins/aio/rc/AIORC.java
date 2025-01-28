package net.storm.plugins.aio.rc;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.storm.api.events.ConfigButtonClicked;
import net.storm.api.events.ConfigChanged;
import net.storm.api.plugins.PluginDescriptor;
import net.storm.api.plugins.config.ConfigManager;
import net.storm.plugins.aio.rc.enums.RunningState;
import net.storm.plugins.aio.rc.enums.States;
import net.storm.plugins.aio.rc.states.BankSetupAndStock;
import net.storm.plugins.aio.rc.states.Setup;
import net.storm.sdk.game.GameThread;
import net.storm.sdk.items.loadouts.LoadoutFactory;
import net.storm.sdk.plugins.LoopedPlugin;
import org.pf4j.Extension;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * A very basic example of a looped plugin.
 *
 * Important notes: look at the imports! The class names are similar to RuneLite's API, but they are not the same.
 * Always use the Storm SDK's classes when developing plugins.
 *
 * Ensure that your package names start with net.storm.plugins, or your plugin will not be compatible with the SDN.
 */
@PluginDescriptor(name = "AIO RC")
@Extension
public class AIORC extends LoopedPlugin {
    private ConfigManager conManager;
    private SharedContext context;
    private AIORCOverlay overlay;

    @Inject
    public EventBus eventBus;

    @Inject
    private AIORCConfig config;

    @Inject
    private OverlayManager overlayManager;


    @Getter
    @Setter
    private StateMachine stateMachine;

    private AtomicInteger ticks = new AtomicInteger(0);

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked buttonClicked) {
        if (buttonClicked.getKey().equals("startPlugin")) {
            if (RunningState.RUNNING.equals(context.getCurrentRunningState())) {
                context.pause();
                context.setCurrentRunningState(RunningState.STOPPED);
            } else {
                context.setCurrentRunningState(RunningState.RUNNING);
                context.start();
                if(stateMachine != null && stateMachine.getCurrentStateName() == States.ForceAwaitErrors) {
                    stateMachine.setState(new Setup(context), false);
                }
            }
        }

        if(buttonClicked.getKey().equals("pausePlugin")) {
            context.setCurrentRunningState(RunningState.PAUSED);
        }

        if(buttonClicked.getKey().equals("importLoadout")) {
            GameThread.invokeAndWait(() -> conManager.setConfiguration("AIO RC", "loadout", LoadoutFactory.fromCurrentEquipment().build()));
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (stateMachine != null && context != null) {
            context.setConfig(conManager.getConfig(AIORCConfig.class));
            context.checkCurrentRuneBeingCrafted();

            if(event.getKey().equals("runes") || event.getKey().equals("loadout") ||
                    event.getKey().equals("airCombo") || event.getKey().equals("earthCombo") || event.getKey().equals("fireCombo") ||
                    event.getKey().equals("waterCombo")) {
                stateMachine.setState(new BankSetupAndStock(context), true);
                context.setRuneNeededForComboRunesId(null);
                context.setTalismanNeededForComboRunes(null);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.ticks.incrementAndGet();

        if (isRunning() && this.stateMachine == null) {
            setStateMachine(new StateMachine(eventBus));
            this.stateMachine.setState(new BankSetupAndStock(context), true);
            context.checkCurrentRuneBeingCrafted();
            System.out.println("Initializing AIO RC Plugin");
        }

        if(stateMachine.getCurrentStateName() == States.ForceAwaitErrors) {
            context.setCurrentRunningState(RunningState.STOPPED);
        }


        if (context.getCurrentRunningState() == RunningState.RUNNING) {
            this.stateMachine.handleState();
            context.setCurrentState(stateMachine.getCurrentStateName().name());
        }

    }

    @Override
    public void startUp() throws Exception
    {
        this.context = new SharedContext(config);
        this.overlay = new AIORCOverlay(context);
        overlayManager.add(overlay);
    }

    @Override
    public void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }

    @Override
    protected int loop() {
        return 1000; // Sleep for 1000 milliseconds
    }

    @Provides
    AIORCConfig provideConfig(ConfigManager configManager) {
        this.conManager = configManager;
        return configManager.getConfig(AIORCConfig.class);
    }
}
