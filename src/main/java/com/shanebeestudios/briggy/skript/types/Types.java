package com.shanebeestudios.briggy.skript.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.event.BrigCommandTriggerEvent;
import dev.jorel.commandapi.wrappers.IntegerRange;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class Types {

    static {
        // Event Values
        EventValues.registerEventValue(BrigCommandTriggerEvent.class, CommandSender.class, new Getter<>() {
            @Override
            public @Nullable CommandSender get(BrigCommandTriggerEvent event) {
                return event.getSender();
            }
        }, 0);

        // Classes
        Classes.registerClass(new ClassInfo<>(IntegerRange.class, "intrange")
                .user("int[eger] ?ranges?")
                .name("Integer Range")
                .since("INSERT VERSION"));

        Classes.registerClass(new ClassInfo<>(BrigArgument.class, "brigarg")
                .user("brig? args?")
                .name("Brig Argument Type")
                .description("Represents a type of argument for a Brig Command.",
                        "Most argument types will accept vanilla values and return a Bukkit value (usablable by Skript).",
                        "\nNote, special types:",
                        "\nString is a special type only currently available in the registration effect which",
                        "represents a single string with no spaces. ex: `blah_blah`. Also has the ability to add suggestions.",
                        "\n`text` represents a string of text in quotes. ex: `\"oh hello there\"`",
                        "\n`int range` represents 2 numbers with 2 dots between. ex: `1..10`.",
                        "This will return as an IntRange object, use the expression to get the high/low numbers.",
                        "\n`player/entity` represents a single player/entity.",
                        "\n`players/entities` represents a list of players/entities.",
                        "\n`blockstate` represents a BlockState in Minecraft but will return as a BlockData.",
                        "\n`time` represents time in a world. ex: `1d` is 1 ingame day (24000 ticks).",
                        "\n`sound` represents a sound string key.",
                        "\n`location 2d` represents a location with a y coord (defaults to y=0).",
                        "\n\nSee Wiki for more details <link>https://github.com/ShaneBeee/SkBriggy/wiki/Brig-Argument-Type</link>")
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
