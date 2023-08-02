package com.shanebeestudios.briggy.skript.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.event.BrigCommandEvent;
import dev.jorel.commandapi.wrappers.IntegerRange;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class Types {

    static {
        // Event Values
        EventValues.registerEventValue(BrigCommandEvent.class, CommandSender.class, new Getter<>() {
            @Override
            public @Nullable CommandSender get(BrigCommandEvent event) {
                return event.getSender();
            }
        }, 0);

        // Classes
        Classes.registerClass(new ClassInfo<>(IntegerRange.class, "intrange")
                .user("int[eger] ?ranges?")
                .name("Integer Range")
                .description("Represents a range between 2 integers.",
                        "Use the IntegerRange expression to get the high/low value.")
                .since("INSERT VERSION"));

        Classes.registerClass(new ClassInfo<>(BrigArgument.class, "brigarg")
                .user("brig ?args?")
                .name("Brig Argument Type")
                .description("Represents a type of argument for a Brig Command.",
                        "\nAll the types here represent object types in Minecraft, with built in conversions to return",
                        "Bukkit/Skript types to be able to fully use in Skript.",
                        "\nSee Wiki for more details <link>https://github.com/ShaneBeee/SkBriggy/wiki/Brig-Argument-Type</link>")
                .usage(BrigArgument.getPatterns())
                .since("INSERT VERSION")
                .parser(new Parser<>() {

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public @Nullable BrigArgument parse(String string, ParseContext context) {
                        return BrigArgument.parse(string);
                    }

                    @Override
                    public @NotNull String toString(BrigArgument brigArgument, int flags) {
                        return brigArgument.toString();
                    }

                    @Override
                    public @NotNull String toVariableNameString(BrigArgument brigArgument) {
                        return toString(brigArgument, 0);
                    }
                }));
    }

}
