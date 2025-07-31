package com.shanebeestudios.briggy.api;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.Timespan;
import com.shanebeestudios.briggy.SkBriggy;
import com.shanebeestudios.briggy.api.wrapper.BlockPredicate;
import com.shanebeestudios.briggy.api.wrapper.ItemStackPredicate;
import com.shanebeestudios.skbee.api.nbt.NBTApi;
import com.shanebeestudios.skbee.api.wrapper.ComponentWrapper;
import dev.jorel.commandapi.arguments.AdventureChatArgument;
import dev.jorel.commandapi.arguments.AdventureChatComponentArgument;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BlockPredicateArgument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.ItemStackPredicateArgument;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.NBTCompoundArgument;
import dev.jorel.commandapi.arguments.RotationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.Location2D;
import dev.jorel.commandapi.wrappers.Rotation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public abstract class CustomArg {

    private static final List<String> MATERIAL_NAMES = Arrays.stream(Material.values()).filter(material -> !material.isLegacy()).map(mat -> mat.getKey().getKey()).toList();
    private static final World MAIN_WORLD = Bukkit.getWorlds().getFirst();
    private static final List<String> DEFAULT_TIMESPANS = List.of("10s", "5m", "1h", "3d");

    static final CustomArg MESSAGE = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new AdventureChatArgument(name), info -> {
                Component component = info.currentInput();
                if (SkBriggy.HAS_SKBEE_COMPONENT)
                    return ComponentWrapper.fromComponent(component);
                return LegacyComponentSerializer.legacySection().serialize(component);
            });
        }
    };

    static final CustomArg BLOCK_POS = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new LocationArgument(name, LocationType.BLOCK_POSITION);
        }
    };

    static final CustomArg BLOCK_PREDICATE = new CustomArg() {
        @SuppressWarnings("unchecked")
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new BlockPredicateArgument(name), info -> new BlockPredicate(info.currentInput()));
        }
    };

    static final CustomArg COMPONENT = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new AdventureChatComponentArgument(name), info -> {
                Component component = info.currentInput();
                if (SkBriggy.HAS_SKBEE_COMPONENT)
                    return ComponentWrapper.fromComponent(component);
                return LegacyComponentSerializer.legacySection().serialize(component);
            });
        }
    };

    static final CustomArg ENTITY_DATA = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new EntityTypeArgument(name), info ->
                EntityUtils.toSkriptEntityData(info.currentInput()));
        }
    };

    static final CustomArg ITEM_STACK_PREDICATE = new CustomArg() {
        @SuppressWarnings({"unused", "unchecked"})
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new ItemStackPredicateArgument(name), info -> new ItemStackPredicate(info.currentInput()));
        }
    };

    static final CustomArg ITEM_TYPE = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                Material material = Material.getMaterial(info.input().toUpperCase(Locale.ROOT));
                if (material == null) {
                    throw CustomArgumentException.fromString("Unknown item type '" + info.input() + "'");
                }
                return new ItemType(material);
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

    static final CustomArg NBT = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new NBTCompoundArgument<>(name), info -> {
                String nbtString = info.input();
                if (SkBriggy.HAS_SKBEE_NBT)
                    return NBTApi.validateNBT(nbtString);
                return nbtString;
            });
        }
    };

    static final CustomArg ROTATION = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new RotationArgument(name), info -> {
                Rotation rotation = info.currentInput();
                return new Location(MAIN_WORLD, 0, 0, 0, rotation.getNormalizedYaw(), rotation.getNormalizedPitch());
            });
        }
    };

    static final CustomArg SKRIPT_COLOR = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                SkriptColor skriptColor = SkriptColor.fromName(info.input().replace("_", " "));
                if (skriptColor == null) {
                    throw CustomArgumentException.fromString("Unknown skript color '" + info.input() + "'");
                }
                return skriptColor;
            }).replaceSuggestions(ArgumentSuggestions.strings(
                Arrays.stream(SkriptColor.values()).map(skriptColor -> skriptColor.getName().replace(" ", "_")).toArray(String[]::new)));
        }
    };

    static final CustomArg TIME_SPAN = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                Timespan parse = Timespan.parse(info.input(), ParseContext.COMMAND);
                if (parse == null) {
                    throw CustomArgumentException.fromString("Unknown timespan '" + info.input() + "'");
                }
                return parse;
            }).replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                CompletableFuture.supplyAsync(() -> {
                    String arg = info.currentArg();
                    if (arg.matches("\\d+")) {
                        return List.of(arg + "s", arg + "m", arg + "h", arg + "d", arg + "w", arg + "mo", arg + "y");
                    } else {
                        return DEFAULT_TIMESPANS;
                    }
                })));
        }
    };

    static final CustomArg TIME_PERIOD = new CustomArg() {
        @Override
        Argument<?> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                try {
                    return Timespan.TimePeriod.valueOf(info.input().toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    throw CustomArgumentException.fromString("Unknown time period '" + info.input() + "'");
                }
            }).replaceSuggestions(ArgumentSuggestions.strings(
                Arrays.stream(Timespan.TimePeriod.values()).map(timePeriod -> timePeriod.name().toLowerCase(Locale.ROOT)).toArray(String[]::new)
            ));
        }
    };

    static final CustomArg WORLD = new CustomArg() {
        @Override
        Argument<World> get(String name) {
            return new CustomArgument<>(new StringArgument(name), info -> {
                World world = Bukkit.getWorld(info.input());
                if (world == null) {
                    throw CustomArgumentException.fromString("Unknown world '" + info.input() + "'");
                }
                return world;
            }).includeSuggestions(ArgumentSuggestions.stringCollectionAsync(commandSenderSuggestionInfo ->
                CompletableFuture.supplyAsync(() -> Bukkit.getWorlds().stream().map(World::getName).toList())));

        }
    };

    abstract Argument<?> get(String name);

}
