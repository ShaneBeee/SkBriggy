package com.shanebeestudios.briggy.api;

import ch.njol.skript.patterns.PatternCompiler;
import ch.njol.skript.patterns.SkriptPattern;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;
import com.shanebeestudios.briggy.api.util.Utils;
import dev.jorel.commandapi.arguments.AngleArgument;
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
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.LootTableArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
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
import dev.jorel.commandapi.arguments.WorldArgument;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BrigArgument {

    // STATIC STUFF

    private static final Map<String, BrigArgument> MAP_BY_NAME = new HashMap<>();
    private static final Number[] INT_MIN_MAX = new Number[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
    private static final Number[] LONG_MIN_MAX = new Number[]{Long.MIN_VALUE, Long.MAX_VALUE};
    private static final Number[] FLOAT_MIN_MAX = new Number[]{Float.MIN_VALUE, Float.MAX_VALUE};
    private static final Number[] DOUBLE_MIN_MAX = new Number[]{Double.MIN_VALUE, Double.MAX_VALUE};

    static {
        // Numbers
        register("double", DoubleArgument.class);
        register("float", FloatArgument.class);
        register("integer", "int[eger]", IntegerArgument.class);
        register("integer range", "int[eger][ ]range", IntegerRangeArgument.class);
        register("long", LongArgument.class);

        // Minecraft
        register("angle", AngleArgument.class);
        register("biome", BiomeArgument.class);
        register("biomekey", "biome[ ]key", BiomeArgument.NamespacedKey.class);
        register("block state", "block[[ ](state|data)]", BlockStateArgument.class);
        register("blockpredicate", "block[ ]predicate", CustomArg.BLOCK_PREDICATE);
        register("blockpos", "block[ ]pos", CustomArg.BLOCK_POS);
        register("command", CommandArgument.class);
        register("component", CustomArg.COMPONENT);
        register("dimension", WorldArgument.class);
        register("enchantment", "enchant[ment]", EnchantmentArgument.class);
        register("entitytype", "entity[ ]type", EntityTypeArgument.class);
        register("itemstack", "item[[ ]stack]", ItemStackArgument.class);
        register("itempredicate", "item[[ ]stack][ ]predicate", CustomArg.ITEM_STACK_PREDICATE);
        register("loottable", "loot[ ]table", LootTableArgument.class);
        register("message", CustomArg.MESSAGE);
        register("nbt", CustomArg.NBT);
        register("objective", ObjectiveArgument.class);
        register("particle", ParticleArgument.class);
        register("potioneffect", "potion[ ]effect[[ ]type]", PotionEffectArgument.class);
        register("recipe", RecipeArgument.class);
        register("rotation", CustomArg.ROTATION);
        register("sound", SoundArgument.class);
        register("team", TeamArgument.class);
        register("time", TimeArgument.class);
        register("world", CustomArg.WORLD);

        // Entity
        register("entity", EntitySelectorArgument.OneEntity.class);
        register("entities", EntitySelectorArgument.ManyEntities.class);
        register("player", EntitySelectorArgument.OnePlayer.class);
        register("players", EntitySelectorArgument.ManyPlayers.class);
        register("offlineplayer", "offline[ ]player", OfflinePlayerArgument.class);

        // Bukkit
        register("location", "loc[ation]", LocationArgument.class);
        register("location 2d", "loc[ation][ ]2d", CustomArg.LOCATION2D);
        register("namespaced key", "(namespaced[ ]key|mc[ ]key)", NamespacedKeyArgument.class);

        // Skript
        register("entitydata", "entity[ ]data", CustomArg.ENTITY_DATA);
        register("itemtype", "item[ ]type", CustomArg.ITEM_TYPE);
        register("skriptcolor", "skript[ ]color", CustomArg.SKRIPT_COLOR);
        register("timespan", "time[ ]span", CustomArg.TIME_SPAN);
        if (Utils.classInfoExistsFor(Timespan.TimePeriod.class)) { // SkBee is adding this
            register("timespanperiod", "time[ ]span[ ]period", CustomArg.TIME_PERIOD);
        }

        // Other
        register("boolean", BooleanArgument.class);
        register("greedystring", "greedy[ ]string", GreedyStringArgument.class);
        register("literal", "lit[eral]", MultiLiteralArgument.class);
        register("text", TextArgument.class);
        register("string", StringArgument.class);
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
            if (brigArgument.skriptPattern.match(string) != null)
                return brigArgument;
        }
        return null;
    }

    /**
     * Supplier used for Skript's ClassInfo
     *
     * @return Supplier of all types
     */
    public static Supplier<Iterator<BrigArgument>> getSupplier() {
        return () -> MAP_BY_NAME.values().stream().sorted(Comparator.comparing(BrigArgument::getName)).iterator();
    }

    // CLASS STUFF

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

    public Number[] getMinMax() {
        if (this.argClass == IntegerArgument.class) return INT_MIN_MAX;
        else if (this.argClass == LongArgument.class) return LONG_MIN_MAX;
        else if (this.argClass == FloatArgument.class) return FLOAT_MIN_MAX;
        else if (this.argClass == DoubleArgument.class) return DOUBLE_MIN_MAX;
        return null;
    }

    public Argument<?> getArgument(String name) {
        if (this.customArg != null) return this.customArg.get(name);
        if (this.argClass == MultiLiteralArgument.class) {
            return new MultiLiteralArgument(name, name);
        }
        try {
            return this.argClass.getDeclaredConstructor(String.class).newInstance(name);
        } catch (InstantiationException | NoSuchMethodException |
                 InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Argument<?> getIntArgument(String name, Number min, Number max) {
        if (this.customArg != null) return this.customArg.get(name);
        if (this.argClass == IntegerArgument.class) {
            return new IntegerArgument(name, min.intValue(), max.intValue());
        } else if (this.argClass == LongArgument.class) {
            return new LongArgument(name, min.longValue(), max.longValue());
        } else if (this.argClass == FloatArgument.class) {
            return new FloatArgument(name, min.floatValue(), max.floatValue());
        } else if (this.argClass == DoubleArgument.class) {
            return new DoubleArgument(name, min.doubleValue(), max.doubleValue());
        }
        return getArgument(name);
    }

    public Argument<?> getMultiLit(String name, List<String> literals) {
        if (argClass != MultiLiteralArgument.class) return null;
        return new MultiLiteralArgument(name, literals.toArray(new String[0]));
    }

    public Class<? extends Argument<?>> getArgClass() {
        return argClass;
    }

    public String getName() {
        return this.name;
    }

}
