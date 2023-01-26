package com.shanebeestudios.briggy.api;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BiomeArgument;
import dev.jorel.commandapi.arguments.BlockStateArgument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.TextArgument;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum BrigArgument {

    INT("int[eger]", IntegerArgument.class),
    FLOAT("float", FloatArgument.class),
    DOUBLE("double", DoubleArgument.class),
    BOOLEAN("boolean", BooleanArgument.class),
    LOCATION("location", LocationArgument.class),
    BLOCK("block[[ ]state]", BlockStateArgument.class),
    ITEM("item[stack]", ItemStackArgument.class),
    BIOME("biome", BiomeArgument.class),
    ENTITY("[single ]entity", EntitySelectorArgument.OneEntity.class),
    ENTITY_M("multiple entity", EntitySelectorArgument.ManyEntities.class),
    PLAYER("[single ]player", EntitySelectorArgument.OnePlayer.class),
    PLAYER_M("multiple player", EntitySelectorArgument.ManyPlayers.class),
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
