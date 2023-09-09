package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.shanebeestudios.briggy.api.event.BrigCommandArgumentsEvent;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Name("Argument Suggestions")
@Description({"Set the suggestions for an argument.",
        "Will accept any object, but strings are the best way to go.",
        "For args you can use a number (the position of the argument) or a string (name of the argument), see examples."})
@Examples({"brig command /spawn <string>:",
        "\targuments:",
        "\t\tset suggestions of arg 1 to all worlds",
        "\ttrigger:",
        "\t\tteleport player to spawn of world({_string})",
        "",
        "brig command /setnick <name:string>:",
        "\targuments:",
        "\t\tset suggestions of \"name\" argument to \"<put your nick here>\"",
        "\ttrigger:",
        "\t\tset display name of player to {_name}"})
@Since("1.0.0")
public class ExprArgSuggestions extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprArgSuggestions.class, Object.class, ExpressionType.COMBINED,
                "suggestions of arg[ument][s] %strings/numbers%",
                "suggestions of %strings/numbers% arg[ument][s]");
    }

    private Expression<?> arg;

    @SuppressWarnings({"NullableProblems"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandArgumentsEvent.class)) {
            Skript.error("Brig arg suggestions can only be set in a brig command 'arguments' section.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.arg = exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable Object[] get(Event event) {
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET) return CollectionUtils.array(Object[].class);
        return null;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        if (!(event instanceof BrigCommandArgumentsEvent brigEvent)) return;

        Map<String, Argument<?>> arguments = brigEvent.getBrigCommand().getArgumentMap();
        List<Argument<?>> argumentList = brigEvent.getBrigCommand().getArguments();

        for (Object object : this.arg.getArray(event)) {
            Argument<?> argument = null;
            if (object instanceof String stringArg) argument = arguments.get(stringArg);
            else if (object instanceof Number numberArg) {
                int i = numberArg.intValue();
                if (i <= argumentList.size()) {
                    argument = argumentList.get(i - 1);
                }
            }
            if (argument == null) continue;

            List<String> stringSuggestions = new ArrayList<>();
            for (Object value : delta) {
                if (value instanceof String string) stringSuggestions.add(string);
                else stringSuggestions.add(Classes.toString(value));
            }
            if (stringSuggestions.size() > 0)
                argument.includeSuggestions(ArgumentSuggestions.strings(stringSuggestions));
        }

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "suggestions of argument " + this.arg.toString(e, d);
    }

}
