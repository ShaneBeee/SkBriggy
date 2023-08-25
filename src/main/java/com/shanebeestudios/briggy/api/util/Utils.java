package com.shanebeestudios.briggy.api.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginLoadOrder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utils {

    @SuppressWarnings("deprecation")
    public static void log(String message) {
        String m = ChatColor.translateAlternateColorCodes('&', "&7[&bSk&3Briggy&7] &7" + message);
        Bukkit.getConsoleSender().sendMessage(m);
    }

    public static void reloadCommands() {
        Server server = Bukkit.getServer();
        Class<? extends @NotNull Server> craftServerClass = server.getClass();
        try {
            Method enablePlugins = craftServerClass.getDeclaredMethod("enablePlugins", PluginLoadOrder.class);
            enablePlugins.setAccessible(true);
            enablePlugins.invoke(server, PluginLoadOrder.POSTWORLD);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
