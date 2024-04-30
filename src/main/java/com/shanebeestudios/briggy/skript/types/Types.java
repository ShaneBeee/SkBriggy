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
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

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
                .since("1.0.0")
                .parser(getDefaultParser()));

        Classes.registerClass(new ClassInfo<>(BrigArgument.class, "brigarg")
                .user("brig ?args?")
                .name("Brig Argument Type")
                .description("Represents a type of argument for a Brig Command.",
                        "\nAll the types here represent object types in Minecraft, with built in conversions to return",
                        "Bukkit/Skript types to be able to fully use in Skript.",
                        "\nSee Wiki for more details <link>https://github.com/ShaneBeee/SkBriggy/wiki/Brig-Argument-Type</link>")
                .usage(BrigArgument.getPatterns())
                .since("1.0.0")
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

        Classes.registerClass(new ClassInfo<>(ParticleData.class, "particledata")
                .user("particle ?datas?")
                .name("Particle Data")
                .description("Represents a particle along with its provided data.",
                        "This is retrieved when using the `particle` argument.",
                        "You can get the particle/data from this type using the appropriate expressions.")
                .examples("brig command /leparticle <p:particle> <loc:location>:",
                        "\ttrigger:",
                        "\t\tset {_particle} to particle type of {_p}",
                        "\t\tset {_data} to data type of {_p}",
                        "\t\tif {_data} is set:",
                        "\t\t\tmake 1 of {_particle} using {_data} at {_loc} with extra 0",
                        "\t\telse:",
                        "\t\t\tmake 1 of {_particle} at {_loc} with extra 0")
                .since("1.1.0")
                .parser(getDefaultParser()));

        if (Classes.getExactClassInfo(Predicate.class) == null) {
            Classes.registerClass(new ClassInfo<>(Predicate.class, "predicate")
                    .user("predicates?")
                    .name("Predicate")
                    .description("Represents a predicate which can be used for filtering.")
                    .since("1.3.0")
                    .parser(getDefaultParser()));
        }
    }

    /**
     * Get a default instance of a Parser for ClassInfos
     *
     * @param <T> ClassType
     * @return New instance of default parser
     */
    public static <T> Parser<T> getDefaultParser() {
        return new Parser<>() {
            @SuppressWarnings("NullableProblems")
            @Override
            public boolean canParse(ParseContext context) {
                return false;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public String toString(T o, int flags) {
                return o.toString();
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public String toVariableNameString(T o) {
                return o.toString();
            }
        };
    }

}
