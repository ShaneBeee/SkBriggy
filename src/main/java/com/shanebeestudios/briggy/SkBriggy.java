package com.shanebeestudios.briggy;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.shanebeestudios.briggy.api.util.Utils;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.config.Config;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public class SkBriggy extends JavaPlugin {

//    @Override
//    public void onLoad() {
//        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
//    }
    // temporarily removing shading (using plugin instead for testing)

    public static boolean HAS_SKBEE_COMPONENT;
    public static boolean HAS_SKBEE_NBT;

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        Utils.log("Starting up SkBriggy!!!");

        if (Bukkit.getPluginManager().getPlugin("SkBee") instanceof SkBee skbee) {
            Config skBeeConfig = skbee.getPluginConfig();
            if (skBeeConfig.ELEMENTS_TEXT_COMPONENT) {
                HAS_SKBEE_COMPONENT = true;
                Utils.log("&5SkBee Text Components &asuccessfully hooked");
            }
            if (skBeeConfig.ELEMENTS_NBT && NBTApi.isEnabled()) {
                HAS_SKBEE_NBT = true;
                Utils.log("&5SkBee NBT &asuccessfully hooked");

                // Hide the NBTAPI logger warnings // TODO remove after shading
                Logger.getLogger("NBTAPI").setFilter(record -> !record.getMessage().contains("[NBTAPI]"));
                Class<?> nbtClass = CommandAPI.getConfiguration().getNBTContainerClass();
                try {
                    // Initialize internal NBT API // TODO remove after shading
                    // Prevents it randomly popped up messages later
                    nbtClass.getDeclaredConstructor(String.class).newInstance("{test:1}");
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

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
