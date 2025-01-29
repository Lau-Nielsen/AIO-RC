package net.storm.plugins.daeyalt;


import net.storm.api.plugins.SoxExclude;
import net.storm.api.plugins.config.*;

@ConfigGroup(DaeyaltMinerConfig.GROUP)
@SoxExclude // Exclude from obfuscation
public interface DaeyaltMinerConfig extends Config {
    String GROUP = "Daeyalt Miner";

    @ConfigItem(
            keyName = "tickManip",
            name = "1.5T",
            position = 1,
            description = "Do you want to 1.5T?."
    )
    default boolean tickManip()
    {
        return false;
    }

    @ConfigItem(
            keyName = "stopTickManip",
            name = "Stop 1.5T someone's near",
            position = 1,
            description = "Should it stop tick manipulating if someone else is near."
    )
    default boolean stopTickManip()
    {
        return false;
    }

    @ConfigItem(
            keyName = "skitzoHop",
            name = "Hop if someone else is here",
            position = 1,
            description = "Should it hop if someone else is here."
    )
    default boolean skitzoHop()
    {
        return false;
    }

    @ConfigItem(
            keyName = "dropGems",
            name = "Drop gems",
            position = 1,
            description = "Should it drop gems?"
    )
    default boolean dropGems()
    {
        return false;
    }

    @ConfigItem(
            keyName = "startPlugin",
            name = "Start / Stop",
            description = "Press button to start/stop plugin.",
            position = 99
    )
    default Button startButton() {
        return null;
    };

    @ConfigItem(
            keyName = "pausePlugin",
            name = "Pause",
            description = "Press button to pause the plugin.",
            position = 100
    )
    default Button pauseButton() {
        return null;
    };
}
