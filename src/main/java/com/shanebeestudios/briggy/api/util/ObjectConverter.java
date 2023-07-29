package com.shanebeestudios.briggy.api.util;

import dev.jorel.commandapi.wrappers.Location2D;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class ObjectConverter {

    public static Object[] convert(List<Object> objects) {
        List<Object> toReturn = new ArrayList<>();
        for (Object object : objects) {
            toReturn.add(convert(object));
        }
        return toReturn.toArray(new Object[0]);
    }

    public static Object convert(Object object) {
        if (object instanceof Location2D location2D) {
            return new Location(location2D.getWorld(), location2D.getX(), 0, location2D.getZ());
        } else if (object instanceof Sound sound) {
            return sound.getKey().toString();
        }
        return object;
    }

}
