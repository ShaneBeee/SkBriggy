package com.shanebeestudios.briggy.api;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.SkriptColor;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.SoundArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.SuggestionProviders;
import dev.jorel.commandapi.wrappers.Location2D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class CustomArg {

    private static final List<String> MATERIAL_NAMES = Arrays.stream(Material.values()).map(mat -> mat.getKey().getKey()).toList();

    static final CustomArg ITEM_TYPE = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                Material material = Material.getMaterial(info.input().toUpperCase(Locale.ROOT));
                if (material != null) return new ItemType(material);
                return null;
            }).includeSuggestions(ArgumentSuggestions.strings(MATERIAL_NAMES.toArray(String[]::new)));
        }
    };

    static final CustomArg LOCATION2D = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new Location2DArgument(name), info -> {
                Location2D loc2d = info.currentInput();
                return new Location(loc2d.getWorld(), loc2d.getX(), 0, loc2d.getZ());
            });
        }
    };

    static final CustomArg SKRIPT_COLOR = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info ->
                    SkriptColor.fromName(info.input().replace("_", " ")))
                    .replaceSuggestions(ArgumentSuggestions.strings(
                            Arrays.stream(SkriptColor.values()).map(skriptColor -> skriptColor.getName().replace(" ", "_")).toArray(String[]::new)));
        }
    };

    static final CustomArg WORLD = new CustomArg() {
        @Override
        Argument<World> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info ->
                    Bukkit.getWorld(info.input()))
                    .replaceSuggestions(ArgumentSuggestions.strings(
                            Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new)));
        }
    };

    abstract Argument<?> get(String name);

}
