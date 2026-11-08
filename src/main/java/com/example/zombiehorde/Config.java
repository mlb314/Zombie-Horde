package com.example.zombiehorde;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common config, written to {@code config/zombiehorde-common.toml}.
 */
public final class Config {
    public static final int DEFAULT_SPAWN_RATE_INCREASE_PERCENT = 300;
    public static final int DEFAULT_MIN_EXTRA_ZOMBIES = 3;
    public static final int DEFAULT_MAX_EXTRA_ZOMBIES = 5;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue SPAWN_RATE_INCREASE_PERCENT = BUILDER
            .comment(
                    "How much to increase the natural spawn weight of zombies, in percent.",
                    "0 = vanilla, 100 = twice the weight, 300 = four times the weight.",
                    "Only affects minecraft:zombie (not husks, drowned or zombie villagers).",
                    "Applied when the world / server loads, so restart the world or server after changing it.")
            .defineInRange("spawnRateIncreasePercent", DEFAULT_SPAWN_RATE_INCREASE_PERCENT, 0, 10_000);

    public static final ModConfigSpec.IntValue MIN_EXTRA_ZOMBIES = BUILDER
            .comment(
                    "Minimum number of additional zombies spawned alongside each naturally spawned zombie.",
                    "Extras are spawned with a random count between min and max (inclusive). Set both to 0 to disable.")
            .defineInRange("minExtraZombies", DEFAULT_MIN_EXTRA_ZOMBIES, 0, 64);

    public static final ModConfigSpec.IntValue MAX_EXTRA_ZOMBIES = BUILDER
            .comment("Maximum number of additional zombies spawned alongside each naturally spawned zombie.")
            .defineInRange("maxExtraZombies", DEFAULT_MAX_EXTRA_ZOMBIES, 0, 64);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
