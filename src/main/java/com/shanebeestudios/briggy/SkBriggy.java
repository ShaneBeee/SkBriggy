package com.shanebeestudios.briggy;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SkBriggy extends JavaPlugin {

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable(this);

        SkriptAddon skriptAddon = Skript.registerAddon(this);
        try {
            skriptAddon.loadClasses("com.shanebeestudios.briggy.skript", "elements");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
