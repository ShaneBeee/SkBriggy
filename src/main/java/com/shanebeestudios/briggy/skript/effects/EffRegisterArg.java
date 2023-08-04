package com.shanebeestudios.briggy.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandArgumentsEvent;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Register Argument")
@Description({"Register an argument for a brig command.",
        "\nString arguments represent a single string with no spaces.",
        "\nText arguments represent a string of text in quotes.",
        "\nSee Brig Argument type for more info."})
@Examples({"brig command /leban:",
        "\targuments:",
        "\t\tregister player arg \"player\"",
        "\t\tregister int arg \"time\"",
        "\t\tregister string arg \"span\" using \"minutes\", \"hours\" and \"days\"",
        "\t\tregister text arg \"reason\"",
        "\ttrigger:",
        "\t\tset {_reason} to \"&c%{_reason}%\"",
        "\t\tset {_timespan} to \"%{_time}% %{_span}%\" parsed as time span",
        "\t\tban {_player} due to {_reason} for {_timespan}",
        "\t\tkick {_player} due to {_reason}",
        "\t\tsend \"You banned %{_player}% due to %{_reason}% for %{_timespan}%\""})
@Since("INSERT VERSION")
public class EffRegisterArg extends Effect {

    static {
        Skript.registerEffect(EffRegisterArg.class,
                "register [:optional] %brigarg% arg[ument] %string% [using %-objects%]");
    }

    private Expression<String> argument;
    private Expression<?> suggestions;
    private Expression<BrigArgument> brigArg;
    private boolean optional;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandArgumentsEvent.class)) {
            Skript.error("A brig arg can only be registered in a brig command 'arguments' section.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.brigArg = (Expression<BrigArgument>) exprs[0];
        this.argument = (Expression<String>) exprs[1];
        this.suggestions = exprs[2];
        this.optional = parseResult.hasTag("optional");
        if (this.suggestions != null && !LiteralUtils.canInitSafely(this.suggestions)) {
            return LiteralUtils.canInitSafely(LiteralUtils.defendExpression(this.suggestions));
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        BrigCommandArgumentsEvent brigCommandEvent = (BrigCommandArgumentsEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        String arg = this.argument.getSingle(event);

        Argument<?> argument = null;
        BrigArgument brigArg = this.brigArg.getSingle(event);

        if (brigArg != null) {
            Number[] minMax = brigArg.getMinMax();
            if (minMax != null && this.suggestions != null) {
                Expression<Object> objectExpression = LiteralUtils.defendExpression(this.suggestions);
                Object[] array = objectExpression.getArray(event);
                Number min = (array.length > 0 && array[0] instanceof Number number) ? number : minMax[0];
                Number max = (array.length > 1 && array[1] instanceof Number number) ? number : minMax[1];
                argument = brigArg.getIntArgument(arg, min, max);

                // reset so they're not used for suggestions
                this.suggestions = null;
            } else {
                argument = brigArg.getArgument(arg);
            }
        }
        if (argument == null) return;

        // GreedyString args have to be last
        List<Argument<?>> brigArgs = brigCommand.getArguments();
        if (brigArgs.size() > 0 && brigArgs.get(brigArgs.size() - 1) instanceof GreedyStringArgument) {
            Skript.error("You cannot register another argument after a 'greedystring' arg.");
            return;
        }

        if (this.suggestions != null) {
            argument.includeSuggestions(ArgumentSuggestions.strings(info -> {
                brigCommandEvent.setSender(info.sender());

                List<String> stringSuggestions = new ArrayList<>();
                for (Object object : this.suggestions.getArray(brigCommandEvent)) {
                    if (object instanceof String string) stringSuggestions.add(string);
                    else stringSuggestions.add(Classes.toString(object));
                }
                if (stringSuggestions.size() > 0) {
                    return stringSuggestions.toArray(new String[0]);
                } else {
                    // Fallback when empty
                    return new String[]{"<" + arg + ">"};
                }
            }));
        }

        argument.setOptional(this.optional);
        brigCommand.addArgument(arg, argument);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String type = this.brigArg.toString(e, d);
        String suggestions = this.suggestions.toString(e, d);
        return "register " + type + " arg " + this.argument.toString(e, d) + " using " + suggestions;
    }

}
