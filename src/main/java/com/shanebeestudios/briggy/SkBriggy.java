package com.shanebeestudios.briggy;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import com.shanebeestudios.briggy.api.skript.Registration;
import com.shanebeestudios.briggy.api.util.Utils;
import com.shanebeestudios.briggy.skript.ElementRegistration;
import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.nbt.NBTContainer;
import com.shanebeestudios.skbee.api.nbt.utils.MinecraftVersion;
import com.shanebeestudios.skbee.config.Config;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkBriggy extends JavaPlugin {

    private static SkBriggy INSTANCE;
    private static boolean commandApiCanLoad;
    private Registration registration;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onLoad() {
        try {
            CommandAPIPaperConfig config = new CommandAPIPaperConfig(this).silentLogs(true);
            if (Bukkit.getPluginManager().getPlugin("SkBee") != null && MinecraftVersion.getVersion() != MinecraftVersion.UNKNOWN) {
                config.initializeNBTAPI(NBTContainer.class, NBTContainer::new);
            }
            CommandAPI.onLoad(config.verboseOutput(false));
            commandApiCanLoad = true;
        } catch (RuntimeException ignore) {
            commandApiCanLoad = false;
        }
    }

    public static boolean HAS_SKBEE_COMPONENT;
    public static boolean HAS_SKBEE_NBT;

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    @Override
    public void onEnable() {
        INSTANCE = this;
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!commandApiCanLoad) {
            Utils.log("&eIt appears the CommandAPI is not available on your server version.");
            Utils.log("&eThis is not a bug.");
            Utils.log("&eThis addon will be updated when CommandAPI supports your server version.");
            Utils.log("&ePlugin will disable!");
            pluginManager.disablePlugin(this);
            return;
        }
        long start = System.currentTimeMillis();
        Utils.log("Starting up SkBriggy!!!");

        // Skript version check
        if (Skript.getVersion().isSmallerThan(new Version(2, 13, 999))) {
            Utils.log("&cOutdated Skript Version: &e" + Skript.getVersion() + " &cplugin will disable.");
            Utils.log("&eSkript 2.14+ is required for SkBriggy to run.");
            pluginManager.disablePlugin(this);
            return;
        }

        // Hook into SkBee (text components and NBT)
        Plugin skBeePlugin = pluginManager.getPlugin("SkBee");
        if (skBeePlugin != null && skBeePlugin.isEnabled() && skBeePlugin instanceof SkBee skBee) {
            Config skBeeConfig = skBee.getPluginConfig();

            if (new Version(skBee.getPluginMeta().getVersion()).isLargerThan(new Version(3, 16, 999))) {
                // In SkBee 3.17.0+ text components are always enabled
                HAS_SKBEE_COMPONENT = true;
            } else {
                HAS_SKBEE_COMPONENT = skBeeConfig.ELEMENTS_TEXT_COMPONENT;
            }

            if (HAS_SKBEE_COMPONENT) {
                Utils.log("&5SkBee Text Components &asuccessfully hooked");
            }
            if (skBeeConfig.ELEMENTS_NBT && NBTApi.isEnabled()) {
                HAS_SKBEE_NBT = true;
                Utils.log("&5SkBee NBT Compounds &asuccessfully hooked");
            }
        }

        // Register Skript addon
        if (Skript.isAcceptRegistrations()) {
            this.registration = new Registration();
            new SkBriggyAddonModule(this.registration);
            ElementRegistration.register(this.registration);
        } else {
            Utils.log("&cSkript isn't accepting registrations?!?!?");
        }

        // Beta check + notice
        String version = getDescription().getVersion();
        if (version.contains("-")) {
            Utils.log("&eThis is a BETA build, things may not work as expected, please report any bugs on GitHub");
            Utils.log("&ehttps://github.com/ShaneBeee/SkBriggy/issues");
        }

        registerMetrics();

        CommandAPI.onEnable();
        long finish = System.currentTimeMillis() - start;
        Utils.log("Finished loading in &b" + finish + "ms");
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 24320);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        INSTANCE = null;
    }

    public Registration getRegistration() {
        return this.registration;
    }

    public static SkBriggy getInstance() {
        return INSTANCE;
    }

}
