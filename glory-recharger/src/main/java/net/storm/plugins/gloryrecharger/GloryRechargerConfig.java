package net.storm.plugins.gloryrecharger;


import net.storm.api.plugins.SoxExclude;
import net.storm.api.plugins.config.*;
import net.storm.plugins.gloryrecharger.enums.BankTransportation;
import net.storm.plugins.gloryrecharger.enums.Banks;
import net.storm.plugins.gloryrecharger.enums.FountainTransportation;
import net.storm.plugins.gloryrecharger.enums.GETransportation;

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

    @ConfigSection(
            name = "Restock",
            description = "Settings to restock.",
            position = 30
    )
    String restockConfig = "restockConfig";

    @ConfigSection(
            name = "Overlay",
            description = "Overlay settings.",
            position = 40
    )
    String overlayConfig = "overlayConfig";

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
            keyName = "geTransportation",
            name = "GE Transport",
            position = 0,
            section = restockConfig,
            description = "How do you want to get to the GE?"
    )
    default GETransportation geTransportation() {
        return GETransportation.RING_OF_WEALTH;
    }

    @ConfigItem(
            keyName = "restockGlories",
            name = "Restock Glories",
            position = 1,
            description = "Sell glories and restock at the GE.",
            section = restockConfig
    )
    default boolean restockGlories()
    {
        return false;
    }

    @Range(
            max = 10000
    )
    @ConfigItem(
            keyName = "gloryLimit",
            name = "Glories to buy",
            position = 2,
            description = "Amount of glories to restock.",
            section = restockConfig,
            hidden = true,
            unhide = "restockGlories"
    )
    default int gloryLimit()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "restockRunes",
            name = "Restock Runes",
            position = 3,
            description = "Buy runes for Annakarl tp.",
            section = restockConfig
    )
    default boolean restockRunes()
    {
        return false;
    }

    @Range(
            max = 25000
    )
    @ConfigItem(
            keyName = "runesLimit",
            name = "Runes to buy",
            position = 4,
            description = "Amount runes to restock.",
            section = restockConfig,
            hidden = true,
            unhide = "restockRunes"
    )
    default int runesLimit()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "restockTabs",
            name = "Restock TP tabs",
            position = 5,
            description = "Buy Annakarl TP tabs",
            section = restockConfig
    )
    default boolean restockTabs()
    {
        return false;
    }

    @Range(
            max = 10000
    )
    @ConfigItem(
            keyName = "tabsLimit",
            name = "Tabs to buy",
            position = 6,
            description = "Amount Annakarl tps to buy.",
            section = restockConfig,
            hidden = true,
            unhide = "restockTabs"
    )
    default int tabsLimit()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "restockStaminas",
            name = "Restock Staminas",
            position = 7,
            description = "Buy stamina pots (1) at the ge",
            section = restockConfig
    )
    default boolean restockStaminas()
    {
        return false;
    }

    @Range(
            max = 2000
    )
    @ConfigItem(
            keyName = "staminaLimit",
            name = "Staminas to buy",
            position = 8,
            description = "Amount staminas to buy.",
            section = restockConfig,
            hidden = true,
            unhide = "restockStaminas"
    )
    default int staminaLimit()
    {
        return 0;
    }

    @Units(
            Units.PERCENT
    )
    @Range(
            max = 200
    )
    @ConfigItem(
            keyName = "priceBuyMultiplier",
            name = "Buy price",
            position = 9,
            description = "What price items be bought at 100 being guide price. </br> 105 being guide price +5%",
            section = restockConfig
    )
    default int priceBuyMultiplier()
    {
        return 115;
    }

    @Units(
            Units.PERCENT
    )
    @Range(
            max = 200
    )
    @ConfigItem(
            keyName = "sellMultiplier",
            name = "Sell price",
            position = 10,
            description = "What price items be sold at 100 being guide price. </br> 105 being guide price +5%",
            section = restockConfig
    )
    default int sellMultiplier()
    {
        return 95;
    }

    @ConfigItem(
            keyName = "showOverlay",
            name = "Show overlay",
            position = 0,
            description = "Should the overlay be shown",
            section = overlayConfig
    )
    default boolean showOverlay()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showStock",
            name = "Show stock",
            position = 1,
            description = "Should the overlay show the stock you have left of the items it's using?",
            section = overlayConfig
    )
    default boolean showStock()
    {
        return true;
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
