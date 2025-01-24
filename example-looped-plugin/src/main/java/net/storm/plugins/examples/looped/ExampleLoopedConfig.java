package net.storm.plugins.examples.looped;


import net.storm.api.items.loadouts.Loadout;
import net.storm.api.plugins.SoxExclude;
import net.storm.api.plugins.config.*;
import net.storm.plugins.examples.looped.enums.*;

@ConfigGroup(ExampleLoopedConfig.GROUP)
@SoxExclude // Exclude from obfuscation
public interface ExampleLoopedConfig extends Config {
    String GROUP = "example-looped-plugin";

    @ConfigSection(
            name = "Banking Section",
            description = "Banking config",
            position = 10
    )
    String bankingConfig = "bankingConfig";

    @ConfigSection(
            name = "Runner Section",
            description = "Runner config:  <br> Ignore this section if you're runecrafting.",
            position = 2
    )
    String runnerConfig = "runnerConfig";

    @ConfigSection(
            name = "Runecrafting Section",
            description = "runecrafting config:  <br> Ignore this section if you're running essence.",
            position = 1
    )
    String runecraftingSection = "runecraftingSection";

    @ConfigSection(
            name = "Loadout Section",
            description = "Loadout config",
            position = 5
    )
    String loadoutSection = "loadoutConfig";

    @ConfigSection(
            name = "Runner Trading Section",
            description = "Trading config: <br> Ignore this section if you're solo runecrafting.",
            position = 20,
            section = runecraftingSection
    )
    String tradingConfig = "tradingConfig";

    @ConfigSection(
            name = "Ring of the Elements",
            description = "Ring of the Elements config.  <br> Ignore this section if you don't have ring of the elements in your loadout.",
            position = 30
    )
    String roteConfig = "roteConfig";

    @ConfigSection(
            name = "Abyss",
            description = "Abyss config <br> If multiple pass options are selected, it will take the closest one.",
            position = 40
    )
    String abyssConfig = "abyssConfig";

    @ConfigItem(
            keyName = "loadout",
            name ="Loadout",
            description = "",
            section = loadoutSection
    )
    default Loadout loadout() {
        return Loadout.builder().build();
    }

    @ConfigItem(
            keyName = "importLoadout",
            name = "Import current loadout",
            description = "Import your current setup to the loadout",
            section = loadoutSection,
            position = 2
    )
    default Button importLoadout() {
        return null;
    };

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

    @ConfigItem(
            keyName = "altar",
            name = "Altar",
            position = 1,
            description = "Name of the altar to run to"
    )
    default Altar runes() {
        return Altar.AIR;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "minActionsPerTick",
            name = "Min Actions per tick",
            position = 2,
            description = "How many actions per tick should i do at minimum while banking.",
            section = bankingConfig
    )
    default int minActionsPerTick()
    {
        return 3;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            keyName = "maxActionsPerTick",
            name = "Max Actions per tick",
            position = 3,
            description = "How many actions per tick should i at maximum do while banking.",
            section = bankingConfig
    )
    default int maxActionsPerTick()
    {
        return 5;
    }

    @ConfigItem(
            keyName = "bankCraftedRunes",
            name = "Bank crafted runes?",
            description = "Should crafted runes be banked?",
            position = 4,
            section = bankingConfig
    )
    default boolean bankCraftedRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "bringBindingNecklace",
            name = "Bring binding necklace",
            description = "Do you want to use binding necklaces? <br> Or should this runner bring binding necklaces?",
            position = 51,
            section = bankingConfig
    )
    default boolean bringBindingNecklace() {
        return false;
    }

    @ConfigItem(
            keyName = "bindingNecklaceFrequency",
            name = "Every # runs: ",
            position = 52,
            description = "When to bring binding necklaces. 4 = every 4 trips to the bank.",
            hidden = true,
            unhide = "bringBindingNecklace",
            section = bankingConfig
    )
    default int bindingNecklaceFrequency()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "useStamina",
            name = "Using staminas?",
            position = 53,
            description = "Do want to use stamina potions?",
            section = bankingConfig
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
            unhide = "useStamina",
            section = bankingConfig
    )
    default int staminaThreshold()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "useAbyss",
            name = "Use the abyss?",
            description = "Use abyss to get to the altar?",
            position = 0,
            hidden = true,
            unhide = "altar",
            unhideValue = "BLOOD || DEATH || CHAOS || BODY || COSMIC || NATURE || LAW || AIR || MIND || FIRE || WATER || EARTH",
            section = abyssConfig
    )
    default boolean useAbyss() {
        return false;
    }

    @ConfigItem(
            keyName = "repairOnDarkMage",
            name = "Dark mage repair?",
            description = "Repair pouches with dark mage?",
            position = 1,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean repairOnDarkMage() {
        return false;
    }

    @ConfigItem(
            keyName = "abyssTendrils",
            name = "Tendrils",
            description = "Pass through tendrils?",
            position = 2,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean abyssTendrils() {
        return false;
    }

    @ConfigItem(
            keyName = "abyssRock",
            name = "Rock",
            description = "Pass through rocks?",
            position = 3,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean abyssRock() {
        return false;
    }

    @ConfigItem(
            keyName = "abyssEyes",
            name = "Eyes",
            description = "Pass through eyes?",
            position = 4,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean abyssEyes() {
        return false;
    }

    @ConfigItem(
            keyName = "abyssGap",
            name = "Gap",
            description = "Pass through gaps?",
            position = 5,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean abyssGap() {
        return false;
    }

    @ConfigItem(
            keyName = "abyssBoil",
            name = "Boil",
            description = "Pass through boils?",
            position = 6,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean abyssBoil() {
        return false;
    }

    @ConfigItem(
            keyName = "abyssPassage",
            name = "Passage",
            description = "Pass through passage?",
            position = 7,
            hidden = true,
            unhide = "useAbyss",
            section = abyssConfig
    )
    default boolean abyssPassage() {
        return false;
    }

    @Range(
            max = 5000
    )
    @ConfigItem(
            keyName = "roteChargesToAdd",
            name = "Charges to add",
            position = 1,
            description = "How many charges should be recharged when a recharge is needed. <br>" +
                    "The plugin will stop if you have insufficient runes. <br>" +
                    "Charges to add + Charge below at should not exceed 5000.",
            section = roteConfig
    )
    default int roteChargesToAdd()
    {
        return 100;
    }

    @Range(
            min = 1,
            max = 5000
    )
    @ConfigItem(
            keyName = "roteChargeAt",
            name = "Charge below: ",
            position = 2,
            description = "How many charges should be left before recharging.",
            section = roteConfig
    )
    default int roteChargeAt()
    {
        return 100;
    }

    @ConfigItem(
            keyName = "bank",
            name = "Bank location",
            position = 1,
            description = "Where do you want to bank for essence?",
            section = bankingConfig
    )
    default Banks bank() {
        return Banks.EDGEVILLE_BANK;
    }

    @ConfigItem(
            keyName = "usePoolAtFerox",
            name = "Use Ferox Pool?",
            position = 5,
            description = "Should the pool at ferox be used to heal, regain prayer/energy?",
            unhide =  "bank",
            unhideValue = "FEROX_ENCLAVE_BANK",
            hidden = true,
            section = bankingConfig
    )
    default boolean usePoolAtFerox() {
        return false;
    }

    @ConfigItem(
            keyName = "poolBeforeBank",
            name = "Pool before bank?",
            position = 6,
            description = "Should the pool at ferox be used before banking?",
            unhide =  "usePoolAtFerox",
            hidden = true,
            section = bankingConfig
    )
    default boolean poolBeforeBank() {
        return false;
    }

    @ConfigItem(
            keyName = "isRunner",
            name = "Running essence?",
            position = 0,
            description = "Are you running essence?",
            section = runnerConfig
    )
    default boolean isRunner() {
        return false;
    }

    @ConfigItem(
            keyName = "runecrafterName",
            name = "Name of the runecrafter",
            position = 1,
            description = "The username of the person runecrafting.",
            section = runnerConfig,
            hidden = true,
            unhide = "isRunner"
    )
    default String runecrafterName() {
        return "Bob";
    }

    @ConfigItem(
            keyName = "airCombo",
            name = "Rune: ",
            position = 3,
            description = "Which combo rune to craft?",
            hidden = true,
            unhide = "altar",
            section = runecraftingSection,
            unhideValue = "AIR"
    )
    default AirRunes airCombo() {
        return AirRunes.AIR_RUNE;
    }

    @ConfigItem(
            keyName = "fireCombo",
            name = "Rune: ",
            position = 3,
            description = "Which combo rune to craft?",
            hidden = true,
            unhide = "altar",
            section = runecraftingSection,
            unhideValue = "FIRE"
    )
    default FireRunes fireCombo() {
        return FireRunes.FIRE_RUNE;
    }

    @ConfigItem(
            keyName = "waterCombo",
            name = "Rune: ",
            position = 3,
            description = "Which combo rune to craft?",
            hidden = true,
            unhide = "altar",
            section = runecraftingSection,
            unhideValue = "WATER"
    )
    default WaterRunes waterCombo() {
        return WaterRunes.WATER_RUNES;
    }

    @ConfigItem(
            keyName = "earthCombo",
            name = "Rune: ",
            position = 3,
            description = "Which combo rune to craft?",
            hidden = true,
            unhide = "altar",
            unhideValue = "EARTH",
            section = runecraftingSection
    )
    default EarthRunes earthCombo() {
        return EarthRunes.EARTH_RUNE;
    }

    @ConfigItem(
            keyName = "useImbue",
            name = "Cast Imbue?",
            position = 4,
            description = "Cast Imbue or use Talismans? <br> If false it'll use talismans.",
            section = runecraftingSection
    )
    default boolean useImbue() {
        return false;
    }

    @ConfigItem(
            keyName = "useDaeyalt",
            name = "Use Daeyalt essence?",
            position = 5,
            description = "Use Daeyalt essence instead of pure essence.",
            section = runecraftingSection
    )
    default boolean useDaeyalt() {
        return false;
    }

    @ConfigItem(
            keyName = "isUsingRunners",
            name = "Using runners?",
            position = 6,
            description = "Do you have people running essence?",
            section = runecraftingSection
    )
    default boolean isUsingRunners() {
        return false;
    }

    @ConfigItem(
            keyName = "runnerNames",
            name = "Name of the runner(s)",
            position = 7,
            description = "The username(s) of your runner(s) comma separated. <br> It will always trade the first username if he is present. <br> otherwise it will take the next",
            section = runecraftingSection,
            hidden = true,
            unhide = "isUsingRunners"
    )
    default String runnerNames() {
        return "Bob,Bob2";
    }

    @Range(
            max = 28
    )
    @ConfigItem(
            keyName = "maxTradeVolume",
            name = "Max items",
            position = 1,
            description = "Maximum amount of items per trade.",
            section = tradingConfig
    )
    default int maxTradeVolume()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "incompleteTrades",
            name = "Do incomplete trades?",
            position = 2,
            description = "Trade even if you don't have the specified number of items available?",
            section = tradingConfig
    )
    default boolean incompleteTrades() {
        return false;
    }

    @Units(
            Units.TICKS
    )
    @Range(
            max = 20
    )
    @ConfigItem(
            keyName = "resendTradeEvery",
            name = "Trade every",
            position = 3,
            description = "Resend trade request every # ticks.",
            section = tradingConfig
    )
    default int resendTradeEvery()
    {
        return 3;
    }
}
