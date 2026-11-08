package com.example.zombiehorde;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

@Mod(ZombieHorde.MODID)
public class ZombieHorde {
    public static final String MODID = "zombiehorde";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Registers the biome modifier type that data/zombiehorde/neoforge/biome_modifier/zombie_spawn_weight.json refers to.
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

    static {
        BIOME_MODIFIER_SERIALIZERS.register("zombie_spawn_weight", () -> ZombieSpawnWeightModifier.CODEC);
    }

    public ZombieHorde(IEventBus modEventBus, ModContainer modContainer) {
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
