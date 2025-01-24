package net.storm.plugins.examples.looped;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Setter;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.storm.api.events.ConfigButtonClicked;
import net.storm.api.events.ConfigChanged;
import net.storm.api.plugins.PluginDescriptor;
import net.storm.api.plugins.config.ConfigManager;
import net.storm.plugins.examples.looped.enums.RunningState;
import net.storm.plugins.examples.looped.enums.States;
import net.storm.plugins.examples.looped.states.*;
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
@PluginDescriptor(name = "Example Looped Plugin")
@Extension
public class ExampleLoopedPlugin extends LoopedPlugin {
    private ConfigManager conManager;

    @Inject
    private ExampleLoopedConfig config;

    @Setter
    private RunningState runningState = RunningState.AWAITING_START;

    @Setter
    private StateMachine stateMachine;

    private AtomicInteger ticks = new AtomicInteger(0);

    @Subscribe
    public void onConfigButtonClicked(ConfigButtonClicked buttonClicked) {
        if (buttonClicked.getKey().equals("startPlugin")) {
            if (RunningState.RUNNING.equals(this.runningState)) {
                setRunningState(RunningState.STOPPED);
            } else {
                setRunningState(RunningState.RUNNING);
            }
        }

        if(buttonClicked.getKey().equals("pausePlugin")) {
            setRunningState(RunningState.PAUSED);
        }

        if(buttonClicked.getKey().equals("importLoadout")) {
            GameThread.invokeAndWait(() -> conManager.setConfiguration("example-looped-plugin", "loadout", LoadoutHelper.fromCurrentSetup().build()));
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (stateMachine != null && stateMachine.getContext() != null) {
            stateMachine.getContext().setConfig(conManager.getConfig(ExampleLoopedConfig.class));
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        this.ticks.incrementAndGet();

        if (isRunning() && this.stateMachine == null) {
            setStateMachine(new StateMachine(new SharedContext(config), eventBus));
            this.stateMachine.setState(new BankSetupAndStock(), true);
            // this.stateMachine.setState(new CraftRunes(), true);
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
    protected int loop() {
        return 50; // Sleep for 1000 milliseconds
    }


    @Inject
    public EventBus eventBus;

    @Provides
    ExampleLoopedConfig provideConfig(ConfigManager configManager) {
        this.conManager = configManager;
        return configManager.getConfig(ExampleLoopedConfig.class);
    }
}
