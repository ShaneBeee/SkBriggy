package com.shanebeestudios.briggy.api;

import ch.njol.skript.patterns.PatternCompiler;
import ch.njol.skript.patterns.SkriptPattern;
import ch.njol.util.StringUtils;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BiomeArgument;
import dev.jorel.commandapi.arguments.BlockStateArgument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.CommandArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LootTableArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.jorel.commandapi.arguments.ObjectiveArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.ParticleArgument;
import dev.jorel.commandapi.arguments.PotionEffectArgument;
import dev.jorel.commandapi.arguments.RecipeArgument;
import dev.jorel.commandapi.arguments.SoundArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TeamArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.arguments.TimeArgument;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrigArgument {

    private static final Map<String, BrigArgument> MAP_BY_NAME = new HashMap<>();

    static {
        // Numbers
        register("integer", "int[eger]", IntegerArgument.class);
        register("integer range", "int[eger][ ]range", IntegerRangeArgument.class);
        register("float", FloatArgument.class);
        register("double", DoubleArgument.class);

        // Minecraft
        register("biome", BiomeArgument.class);
        register("block state", "block[[ ](state|data)]", BlockStateArgument.class);
        register("enchantment", "enchant[ment]", EnchantmentArgument.class);
        register("itemstack", "item[stack]", ItemStackArgument.class);
        register("loottable", "loot[ ]table", LootTableArgument.class);
        register("objective", ObjectiveArgument.class);
        register("particle", ParticleArgument.class);
        register("potioneffect", "potion[ ]effect[[ ]type]", PotionEffectArgument.class);
        register("recipe", RecipeArgument.class);
        register("sound", SoundArgument.class);
        register("team", TeamArgument.class);
        register("time", TimeArgument.class);
        register("world", CustomArg.WORLD);
        register("entitytype", EntityTypeArgument.class);
        register("command", CommandArgument.class);
        register("chat", CustomArg.CHAT);
        register("nbt", CustomArg.NBT);

        // Entity
        register("entity", EntitySelectorArgument.OneEntity.class);
        register("entities", EntitySelectorArgument.ManyEntities.class);
        register("player", EntitySelectorArgument.OnePlayer.class);
        register("players", EntitySelectorArgument.ManyPlayers.class);
        register("offlineplayer", "offline[ ]player", OfflinePlayerArgument.class);

        // Bukkit
        register("location", "loc[ation]", LocationArgument.class);
        register("location 2d", "loc[ation][ ]2d", CustomArg.LOCATION2D);
        register("namespaced key", "(namespacedkey|mckey)", NamespacedKeyArgument.class);

        // Skript
        register("skriptcolor", "skript[ ]color", CustomArg.SKRIPT_COLOR);
        register("itemtype", "item[ ]type", CustomArg.ITEM_TYPE);

        // Other
        register("boolean", BooleanArgument.class);
        register("text", TextArgument.class);
        register("string", StringArgument.class);
        register("greedystring", "greedy[ ]string", GreedyStringArgument.class);
    }

    private static void register(String name, String pattern, Class<? extends Argument<?>> argClass) {
        BrigArgument brigArgument = new BrigArgument(name, pattern, argClass);
        MAP_BY_NAME.put(name, brigArgument);
    }

    private static void register(String name, Class<? extends Argument<?>> argClass) {
        register(name, name, argClass);
    }

    private static void register(String name, String pattern, CustomArg customArg) {
        BrigArgument brigArgument = new BrigArgument(name, pattern, customArg);
        MAP_BY_NAME.put(name, brigArgument);
    }

    private static void register(String name, CustomArg customArg) {
        register(name, name, customArg);
    }


    private final String name;
    private final String pattern;
    private final SkriptPattern skriptPattern;
    private Class<? extends Argument<?>> argClass;
    private CustomArg customArg = null;

    BrigArgument(String name, String pattern, Class<? extends Argument<?>> argClass) {
        this.name = name;
        this.pattern = pattern;
        this.argClass = argClass;
        this.skriptPattern = PatternCompiler.compile(pattern);
    }

    BrigArgument(String name, String pattern, CustomArg customArg) {
        this.name = name;
        this.pattern = pattern;
        this.customArg = customArg;
        this.skriptPattern = PatternCompiler.compile(pattern);
    }

    @Override
    public String toString() {
        return "BrigArg(name=" + this.name + ")";
    }

    public Argument<?> getArgument(String name) {
        if (this.customArg != null) return this.customArg.get(name);
        try {
            return this.argClass.getDeclaredConstructor(String.class).newInstance(name);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPatterns() {
        List<String> patterns = new ArrayList<>();
        for (BrigArgument value : MAP_BY_NAME.values()) {
            patterns.add(value.pattern);
        }
        Collections.sort(patterns);
        return StringUtils.join(patterns, ", ");
    }

    public static BrigArgument parse(String string) {
        for (BrigArgument brigArgument : MAP_BY_NAME.values()) {
            if (brigArgument.skriptPattern.match(string) != null) return brigArgument;
        }
        return null;
    }

}
