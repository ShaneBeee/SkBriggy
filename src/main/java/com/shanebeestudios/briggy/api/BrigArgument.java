package com.shanebeestudios.briggy.api;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BiomeArgument;
import dev.jorel.commandapi.arguments.BlockStateArgument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LootTableArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.jorel.commandapi.arguments.ObjectiveArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.ParticleArgument;
import dev.jorel.commandapi.arguments.PotionEffectArgument;
import dev.jorel.commandapi.arguments.RecipeArgument;
import dev.jorel.commandapi.arguments.SoundArgument;
import dev.jorel.commandapi.arguments.TeamArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.arguments.TimeArgument;
import dev.jorel.commandapi.arguments.WorldArgument;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum BrigArgument {

    // Numbers
    INT("int[eger]", IntegerArgument.class),
    INT_RANGE("int[eger] range", IntegerRangeArgument.class),
    FLOAT("float", FloatArgument.class),
    DOUBLE("double", DoubleArgument.class),

    // Minecraft
    BIOME("biome", BiomeArgument.class),
    BLOCK("block[[ ]state]", BlockStateArgument.class),
    ENCHANT("enchant[ment]", EnchantmentArgument.class),
    ITEM("item[stack]", ItemStackArgument.class),
    LOOT("loot[ ]table", LootTableArgument.class),
    OBJECTIVE("objective", ObjectiveArgument.class),
    PARTICLE("particle", ParticleArgument.class),
    POTION("potion effect[ type]", PotionEffectArgument.class),
    RECIPE("recipe", RecipeArgument.class),
    SOUND("sound", SoundArgument.class),
    TEAM("team", TeamArgument.class),
    TIME("time", TimeArgument.class),
    WORLD("world", WorldArgument.class),

    // Entity
    ENTITY("[single ]entity", EntitySelectorArgument.OneEntity.class),
    ENTITY_M("[multiple ]entities", EntitySelectorArgument.ManyEntities.class),
    PLAYER("[single ]player", EntitySelectorArgument.OnePlayer.class),
    PLAYER_M("[multiple ]players", EntitySelectorArgument.ManyPlayers.class),
    OFFLINE_PLAYER("offline player", OfflinePlayerArgument.class),

    // Bukkit
    LOCATION("location", LocationArgument.class),
    LOCATION2D("location 2d", Location2DArgument.class),
    NAMESPACEDKEY("namespaced key", NamespacedKeyArgument.class),

    // Other
    BOOLEAN("boolean", BooleanArgument.class),
    TEXT("text", TextArgument.class);

    private final String name;
    private final Class<? extends Argument<?>> argClass;

    BrigArgument(String name, Class<? extends Argument<?>> argClass) {
        this.name = name;
        this.argClass = argClass;
    }

    public static Argument<?> getArgument(int spot, String name) {
        BrigArgument value = values()[spot];
        try {
            return value.argClass.getDeclaredConstructor(String.class).newInstance(name);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (BrigArgument value : values()) {
            names.add(value.name);
        }
        return names;
    }

}
