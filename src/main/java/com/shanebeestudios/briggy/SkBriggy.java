package com.shanebeestudios.briggy;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.shanebeestudios.briggy.api.util.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SkBriggy extends JavaPlugin {

//    @Override
//    public void onLoad() {
//        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
//    }
    // temporarily removing shading (using plugin instead for testing)

    @Override
    public void onEnable() {
        Utils.log("Starting up SkBriggy!!!");

        if (Skript.isAcceptRegistrations()) {
            Utils.log("Skript IS accepting registrations!!!");
            SkriptAddon skriptAddon = Skript.registerAddon(this);
            try {
                skriptAddon.loadClasses("com.shanebeestudios.briggy.skript");
                skriptAddon.setLanguageFileDirectory("lang");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Utils.log("&cSkript isn't accepting registrations?!?!?");
        }
        //CommandAPI.onEnable();
    }

    @Override
    public void onDisable() {
        //CommandAPI.onDisable();
    }

}
