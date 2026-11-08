package com.example.zombiehorde;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

/**
 * Spawns a few extra zombies next to every naturally spawned zombie.
 * <p>
 * Recursion is impossible by construction: we only react to {@link MobSpawnType#NATURAL}, and the extras
 * are spawned as {@link MobSpawnType#REINFORCEMENT}. That also means vanilla "zombie calls for help"
 * reinforcements (same spawn type) never trigger extras either.
 */
@EventBusSubscriber(modid = ZombieHorde.MODID)
public final class ZombieSpawnHandler {
    /** Extras are placed within this many blocks (horizontally) of the original zombie. */
    private static final int SPREAD_RADIUS = 4;
    /** Random columns tried per extra zombie before giving up on it. */
    private static final int PLACEMENT_ATTEMPTS = 12;
    /** Vertical offsets tried in each column, nearest first. */
    private static final int[] Y_OFFSETS = {0, 1, -1, 2, -2};

    private ZombieSpawnHandler() {}

    // LOWEST so that other mods get to veto the spawn first; we then skip extras for vetoed spawns.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }
        if (event.getEntity().getType() != EntityType.ZOMBIE || event.isSpawnCancelled()) {
            return;
        }

        int min = Config.MIN_EXTRA_ZOMBIES.get();
        int max = Config.MAX_EXTRA_ZOMBIES.get();
        if (max < min) {
            int swap = min;
            min = max;
            max = swap;
        }
        if (max <= 0) {
            return;
        }

        ServerLevel level = event.getLevel().getLevel();
        RandomSource random = level.getRandom();
        BlockPos origin = BlockPos.containing(event.getX(), event.getY(), event.getZ());

        int extras = Mth.nextInt(random, min, max); // inclusive on both ends
        for (int i = 0; i < extras; i++) {
            spawnExtraZombie(level, origin, random);
        }
    }

    private static void spawnExtraZombie(ServerLevel level, BlockPos origin, RandomSource random) {
        Zombie zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) {
            return;
        }

        for (int attempt = 0; attempt < PLACEMENT_ATTEMPTS; attempt++) {
            int x = origin.getX() + random.nextInt(-SPREAD_RADIUS, SPREAD_RADIUS + 1);
            int z = origin.getZ() + random.nextInt(-SPREAD_RADIUS, SPREAD_RADIUS + 1);
            // The original zombie isn't in the world yet at this point, so keep its column free.
            if (x == origin.getX() && z == origin.getZ()) {
                continue;
            }

            for (int dy : Y_OFFSETS) {
                BlockPos feet = new BlockPos(x, origin.getY() + dy, z);
                if (!level.hasChunkAt(feet) || !level.getWorldBorder().isWithinBounds(feet)) {
                    break;
                }
                BlockPos below = feet.below();
                if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                    continue;
                }

                zombie.moveTo(x + 0.5D, feet.getY(), z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
                if (level.isUnobstructed(zombie)
                        && level.noCollision(zombie)
                        && !level.containsAnyLiquid(zombie.getBoundingBox())) {
                    // Fires FinalizeSpawnEvent with REINFORCEMENT, which our handler ignores.
                    EventHooks.finalizeMobSpawn(zombie, level, level.getCurrentDifficultyAt(feet), MobSpawnType.REINFORCEMENT, null);
                    level.addFreshEntityWithPassengers(zombie);
                    return;
                }
            }
        }
    }
}
