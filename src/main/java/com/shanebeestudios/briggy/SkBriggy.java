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

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        Utils.log("Starting up SkBriggy!!!");

        if (Skript.isAcceptRegistrations()) {
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

        // Beta check + notice
        String version = getDescription().getVersion();
        if (version.contains("-")) {
            Utils.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
            Utils.log("&ehttps://github.com/ShaneBeee/SkBriggy/issues");
        }

        //CommandAPI.onEnable();
        long finish = System.currentTimeMillis() - start;
        Utils.log("Finished loading in &b" + finish + "ms");
    }

    @Override
    public void onDisable() {
        //CommandAPI.onDisable();
    }

}
