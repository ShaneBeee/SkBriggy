package com.shanebeestudios.briggy.skript.types;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.event.BrigCommandEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandSuggestEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeTriggerEvent;
import com.github.shanebeee.skr.Registration;
import dev.jorel.commandapi.executors.ExecutorType;
import dev.jorel.commandapi.wrappers.IntegerRange;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Types {

    public static void register(Registration reg) {
        // Event Values
        reg.newEventValue(BrigCommandEvent.class, CommandSender.class)
            .converter(BrigCommandEvent::getSender)
            .register();
        reg.newEventValue(BrigCommandEvent.class, World.class)
            .converter(BrigCommandEvent::getWorld)
            .register();
        reg.newEventValue(BrigTreeTriggerEvent.class, CommandSender.class)
            .converter(BrigTreeTriggerEvent::getSender)
            .register();
        reg.newEventValue(BrigCommandSuggestEvent.class, CommandSender.class)
            .converter(BrigCommandSuggestEvent::getCommandSender)
            .register();

        // Classes
        reg.newType(IntegerRange.class, "intrange")
            .user("int[eger] ?ranges?")
            .name("Integer Range")
            .description("Represents a range between 2 integers.",
                "Use the IntegerRange expression to get the high/low value.")
            .since("1.0.0")
            .register();

        reg.newType(BrigArgument.class, "brigarg")
            .user("brig ?args?")
            .name("Brig Argument Type")
            .description("Represents a type of argument for a Brig Command.",
                "\nAll the types here represent object types in Minecraft, with built in conversions to return",
                "Bukkit/Skript types to be able to fully use in Skript.",
                "\nSee Wiki for more details <link>https://github.com/ShaneBeee/SkBriggy/wiki/Brig-Argument-Type</link>")
            .usage(BrigArgument.getPatterns())
            .since("1.0.0")
            .after("classinfo")
            .parser(new Parser<>() {
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
            })
            .supplier(BrigArgument.getSupplier())
            .register();

        reg.newEnumType(ExecutorType.class, "executortype")
            .user("executor ?types?")
            .name("Executor Type")
            .description("Represents the different types that can run a command.")
            .since("1.5.7")
            .register();

        reg.newType(ParticleData.class, "particledata")
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
            .register();

        if (Classes.getExactClassInfo(Predicate.class) == null) {
            reg.newType(Predicate.class, "predicate")
                .user("predicates?")
                .name("Predicate")
                .description("Represents a predicate which can be used for filtering.")
                .since("1.3.0")
                .register();
        }
    }

}
