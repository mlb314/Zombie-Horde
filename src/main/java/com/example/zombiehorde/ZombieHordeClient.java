package com.example.zombiehorde;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Not loaded on dedicated servers.
@Mod(value = ZombieHorde.MODID, dist = Dist.CLIENT)
public class ZombieHordeClient {
    public ZombieHordeClient(ModContainer container) {
        // Adds a config button for this mod under Mods > Zombie Horde.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
