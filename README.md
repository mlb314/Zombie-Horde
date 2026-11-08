# Zombie Horde

A small NeoForge mod for Minecraft 1.21.1 that does two things:

1. **More zombies.** Raises the natural spawn weight of `minecraft:zombie` by a configurable percentage (default `300`, i.e. zombies are picked 4x as often as before when the game chooses a monster to spawn).
2. **Zombie packs.** Every *naturally* spawned zombie arrives with between `min` and `max` extra zombies (default `3`-`5`) placed within a few blocks of it.

Extras are spawned as `REINFORCEMENT` and only `NATURAL` spawns trigger extras, so extras can never spawn more extras, and vanilla "zombie calls for help" reinforcements don't trigger them either. Spawner blocks, structures, world generation, sieges and spawn eggs are also unaffected.

## Config

`config/zombiehorde-common.toml` (also editable in-game under Mods > Zombie Horde > Config):

| Option | Default | Notes |
| --- | --- | --- |
| `spawnRateIncreasePercent` | `300` | `0` = vanilla. Applied when the world/server loads, so restart after changing. |
| `minExtraZombies` | `3` | Set min and max to `0` to disable extras. Takes effect immediately. |
| `maxExtraZombies` | `5` | |

### Things worth knowing

- Vanilla already spawns zombies in groups of 4, and the extras are added **per zombie**, so one vanilla pack with the default config becomes roughly 16-24 zombies. Lower the min/max if that's too much.
- The spawn rate option changes how often zombies are *chosen* among the monsters a biome can spawn. It doesn't raise the overall monster mob cap, so zombies take a bigger share of the cap rather than adding to it.
- Only `minecraft:zombie` is affected, not husks, drowned or zombie villagers.

## Building

Requires JDK 21.

```
./gradlew build
```

The jar ends up in `build/libs/`. Use `./gradlew runClient` / `./gradlew runServer` to test in a dev environment.

Before publishing, change `mod_group_id` / `mod_authors` in `gradle.properties` (and move the `com.example.zombiehorde` package to match), plus the name in `LICENSE`.
