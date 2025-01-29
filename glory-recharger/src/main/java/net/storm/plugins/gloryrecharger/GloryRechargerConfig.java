package net.storm.plugins.gloryrecharger;


import net.storm.api.plugins.SoxExclude;
import net.storm.api.plugins.config.*;
import net.storm.plugins.gloryrecharger.enums.Banks;

@ConfigGroup(GloryRechargerConfig.GROUP)
@SoxExclude // Exclude from obfuscation
public interface GloryRechargerConfig extends Config {
    String GROUP = "Glory Recharger";

    @ConfigItem(
            keyName = "bank",
            name = "Bank location",
            position = 0,
            description = "Where do you want to bank?"
    )
    default Banks bank() {
        return Banks.EDGEVILLE_BANK;
    }

    @Range (
            min = 3,
            max = 28
    )
    @ConfigItem(
            keyName = "gloriesToBring",
            name = "Amount of Glories",
            position = 1,
            description = "The number of glories on each trip."
    )
    default int gloriesToBring()
    {
        return 1;
    }

    @ConfigItem(
            keyName = "useStamina",
            name = "Using staminas?",
            position = 53,
            description = "Do want to use stamina potions?"
    )
    default boolean useStamina() {
        return false;
    }

    @Range(
            max = 100
    )
    @ConfigItem(
            keyName = "staminaThreshold",
            name = "When to use stamina",
            position = 54,
            description = "Run energy threshold required to drink a stamina dose.",
            hidden = true,
            unhide = "useStamina"
    )
    default int staminaThreshold()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "useFeroxPool",
            name = "Use the pool at ferox",
            position = 1,
            description = "Use the pool at Ferox after banking."
    )
    default boolean useFeroxPool()
    {
        return false;
    }

    @ConfigItem(
            keyName = "suicide",
            name = "Suicide",
            position = 1,
            description = "This will suicide at rouges castle"
    )
    default boolean suicide()
    {
        return false;
    }

    @ConfigItem(
            keyName = "stopOnEternal",
            name = "Stop on Eternal Glory",
            position = 1,
            description = "This stop after getting an Eternal glory"
    )
    default boolean stopOnEternal()
    {
        return false;
    }

    @ConfigItem(
            keyName = "depleteInsteadOfBanking",
            name = "Deplete glories",
            position = 1,
            description = "This will use up all charges instead of banking the glories."
    )
    default boolean depleteInsteadOfBanking()
    {
        return false;
    }

    @ConfigItem(
            keyName = "useObelisk",
            name = "Use Obelisks",
            position = 1,
            description = "Use the obelisks for transportation."
    )
    default boolean useObelisk()
    {
        return false;
    }

    @ConfigItem(
            keyName = "hopOnAttackablePlayer",
            name = "Hop if someone attackable is near",
            position = 1,
            description = "Hop if someone who can attack you is nearby while in the wilderness."
    )
    default boolean hopOnAttackablePlayer()
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
