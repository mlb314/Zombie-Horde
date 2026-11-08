package com.example.zombiehorde;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * Multiplies the natural spawn weight of {@code minecraft:zombie} in every biome that has it.
 * <p>
 * Natural spawning picks a monster from the biome's weighted list, so a higher weight means zombies
 * are picked more often relative to everything else.
 */
public record ZombieSpawnWeightModifier() implements BiomeModifier {
    public static final ZombieSpawnWeightModifier INSTANCE = new ZombieSpawnWeightModifier();
    public static final MapCodec<ZombieSpawnWeightModifier> CODEC = MapCodec.unit(INSTANCE);

    private static final int MAX_WEIGHT = 1_000_000;

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // MODIFY runs after other mods have added their spawns, so we scale whatever zombie entry exists by then.
        if (phase != Phase.MODIFY) {
            return;
        }

        int percent = readPercent();
        if (percent <= 0) {
            return;
        }
        double multiplier = 1.0D + percent / 100.0D;

        List<MobSpawnSettings.SpawnerData> monsters = builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER);
        for (int i = 0; i < monsters.size(); i++) {
            MobSpawnSettings.SpawnerData data = monsters.get(i);
            if (data.type != EntityType.ZOMBIE) {
                continue;
            }
            long scaled = Math.round(data.getWeight().asInt() * multiplier);
            int newWeight = (int) Math.max(1L, Math.min(MAX_WEIGHT, scaled));
            monsters.set(i, new MobSpawnSettings.SpawnerData(data.type, newWeight, data.minCount, data.maxCount));
        }
    }

    private static int readPercent() {
        try {
            return Config.SPAWN_RATE_INCREASE_PERCENT.get();
        } catch (IllegalStateException configNotLoaded) {
            // e.g. a data generation run, where the common config is never loaded.
            return Config.DEFAULT_SPAWN_RATE_INCREASE_PERCENT;
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
