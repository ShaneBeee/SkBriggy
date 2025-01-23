package com.shanebeestudios.briggy.api.util;

import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import dev.jorel.commandapi.wrappers.CommandResult;
import dev.jorel.commandapi.wrappers.ComplexRecipeImpl;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.Recipe;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ObjectConverter {

    private static final boolean HAS_TEAMS = Classes.getExactClassInfo(Team.class) != null;
    private static final boolean HAS_KEYS = Classes.getExactClassInfo(NamespacedKey.class) != null;

    public static Object[] convert(List<Object> objects) {
        List<Object> toReturn = new ArrayList<>();
        for (Object object : objects) {
            toReturn.add(convert(object));
        }
        return toReturn.toArray(new Object[0]);
    }

    @SuppressWarnings("removal") // Sound#getKey deprecated by Paper
    public static Object convert(Object object) {
        if (object instanceof Sound sound) {
            return sound.getKey().toString();
        } else if (object instanceof ComplexRecipeImpl complexRecipe) {
            Recipe recipe = complexRecipe.recipe();
            if (recipe instanceof Keyed keyed) {
                NamespacedKey namespacedKey = keyed.getKey();
                if (HAS_KEYS) return namespacedKey;
                return namespacedKey.toString();
            }
            return recipe.toString();
        } else if (object instanceof Team team) {
            if (HAS_TEAMS) return team;
            return team.getName();
        } else if (object instanceof NamespacedKey key) {
            if (HAS_KEYS) return key;
            return key.toString();
        } else if (object instanceof CommandResult commandResult) {
            String command = commandResult.command().getName();
            String args = StringUtils.join(commandResult.args(), " ");
            return command + " " + args;
        }
        return object;
    }

}
