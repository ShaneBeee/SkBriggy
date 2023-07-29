package com.shanebeestudios.briggy.skript.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.briggy.api.event.BrigCommandRunEvent;
import dev.jorel.commandapi.wrappers.IntegerRange;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class Types {

    static {
        // Event Values
        EventValues.registerEventValue(BrigCommandRunEvent.class, CommandSender.class, new Getter<>() {
            @Override
            public @Nullable CommandSender get(BrigCommandRunEvent event) {
                return event.getSender();
            }
        }, 0);

        // Classes
        Classes.registerClass(new ClassInfo<>(IntegerRange.class, "int range")
                .user("int[eger] ?ranges?")
                .name("Integer Range"));
    }
}
