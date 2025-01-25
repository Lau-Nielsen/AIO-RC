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
import net.storm.sdk.items.loadouts.LoadoutHelper;
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

    @Inject
    private AIORCConfig config;

    @Inject
    private SharedContext context;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private AIORCOverlay overlay;

    @Getter
    @Setter
    private RunningState runningState = RunningState.AWAITING_START;

    @Getter
    @Setter
    private StateMachine stateMachine;

    private AtomicInteger ticks = new AtomicInteger(0);

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked buttonClicked) {
        if (buttonClicked.getKey().equals("startPlugin")) {
            if (RunningState.RUNNING.equals(this.runningState)) {
                stateMachine.getContext().pause();
                setRunningState(RunningState.STOPPED);
            } else {
                setRunningState(RunningState.RUNNING);
                stateMachine.getContext().start();
                if(stateMachine != null && stateMachine.getCurrentStateName() == States.ForceAwaitErrors) {
                    stateMachine.setState(new Setup(), false);
                }
            }
        }

        if(buttonClicked.getKey().equals("pausePlugin")) {
            setRunningState(RunningState.PAUSED);
        }

        if(buttonClicked.getKey().equals("importLoadout")) {
            GameThread.invokeAndWait(() -> conManager.setConfiguration("AIO RC", "loadout", LoadoutHelper.fromCurrentSetup().build()));
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (stateMachine != null && stateMachine.getContext() != null) {
            stateMachine.getContext().setConfig(conManager.getConfig(AIORCConfig.class));

            if(event.getKey().equals("runes") || event.getKey().equals("loadout") ||
                    event.getKey().equals("airCombo") || event.getKey().equals("earthCombo") || event.getKey().equals("fireCombo") ||
                    event.getKey().equals("waterCombo")) {
                stateMachine.setState(new BankSetupAndStock(), true);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.ticks.incrementAndGet();

        if (isRunning() && this.stateMachine == null) {
            setStateMachine(new StateMachine(context, eventBus));
            this.stateMachine.setState(new BankSetupAndStock(), true);
            this.stateMachine.getContext().checkCurrentRuneBeingCrafted();
            System.out.println(this.stateMachine.getContext().getCurrentlyCrafting());
            System.out.println("Initializing Example Looped Plugin");
        }

        if(stateMachine.getCurrentStateName() == States.ForceAwaitErrors) {
            setRunningState(RunningState.STOPPED);
        }


        if (runningState == RunningState.RUNNING) {
            System.out.println("Running handleState for: " + this.stateMachine.getCurrentStateName());
            this.stateMachine.handleState(this.stateMachine.getCurrentStateName());
        }

    }

    @Override
    public void startUp() throws Exception
    {
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


    @Inject
    public EventBus eventBus;

    @Provides
    AIORCConfig provideConfig(ConfigManager configManager) {
        this.conManager = configManager;
        return configManager.getConfig(AIORCConfig.class);
    }
}
