package net.storm.plugins.gloryrecharger;


import net.storm.api.plugins.SoxExclude;
import net.storm.api.plugins.config.*;
import net.storm.plugins.gloryrecharger.enums.BankTransportation;
import net.storm.plugins.gloryrecharger.enums.Banks;
import net.storm.plugins.gloryrecharger.enums.FountainTransportation;

@ConfigGroup(GloryRechargerConfig.GROUP)
@SoxExclude // Exclude from obfuscation
public interface GloryRechargerConfig extends Config {
    String GROUP = "Glory Recharger";

    @ConfigSection(
            name = "Banking",
            description = "Banking config",
            position = 10
    )
    String bankingConfig = "bankingConfig";

    @ConfigSection(
            name = "Transporting",
            description = "How you get from a to b.",
            position = 20
    )
    String transportConfig = "transportConfig";

    @ConfigItem(
            keyName = "bank",
            name = "Bank location",
            position = 0,
            section = bankingConfig,
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
            section = bankingConfig,
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
            section = bankingConfig,
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
            section = bankingConfig,
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
            section = bankingConfig,
            position = 1,
            description = "Use the pool at Ferox after banking."
    )
    default boolean useFeroxPool()
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
            keyName = "fountainTransport",
            name = "Path to Fountain",
            position = 0,
            section = transportConfig,
            description = "How do you want to get to the fountain of rune?"
    )
    default FountainTransportation fountainTransport() {
        return FountainTransportation.OBELISK;
    }

    @ConfigItem(
            keyName = "bankTransport",
            name = "Path to Bank",
            position = 0,
            section = transportConfig,
            description = "How do you want to get to the bank?"
    )
    default BankTransportation bankTransportation() {
        return BankTransportation.OBELISK;
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
