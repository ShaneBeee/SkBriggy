package com.shanebeestudios.briggy.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandCreateEvent;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Register Argument")
@Since("INSERT VERSION")
public class EffRegisterArg extends Effect {

    static {
        List<String> patterns = new ArrayList<>();
        patterns.add("register [:optional] string arg[ument] %string% [using %-objects%]");
        BrigArgument.getNames().forEach((s) -> {
            String pattern = "register [:optional] " + s + " arg[ument] %string%";
            patterns.add(pattern);
        });
        Skript.registerEffect(EffRegisterArg.class, patterns.toArray(new String[0]));
    }

    private Expression<String> brigArg;
    private Expression<?> suggestions;
    private int pattern;
    private boolean optional;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandCreateEvent.class)) {
            Skript.error("A brig arg can only be registered in a brig command!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.brigArg = (Expression<String>) exprs[0];
        if (matchedPattern == 0) {
            this.suggestions = exprs[1];
        }
        this.pattern = matchedPattern;
        this.optional = parseResult.hasTag("optional");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        BrigCommandCreateEvent brigCommandEvent = (BrigCommandCreateEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        String arg = this.brigArg.getSingle(event);

        Argument<?> argument;
        if (this.pattern == 0) {
            argument = new StringArgument(arg);
            List<String> stringSuggestions = new ArrayList<>();
            if (this.suggestions != null) {
                for (Object object : this.suggestions.getArray(event)) {
                    if (object instanceof String string) stringSuggestions.add(string);
                    else stringSuggestions.add(Classes.toString(object));
                }
                if (stringSuggestions.size() > 0)
                    argument.includeSuggestions(ArgumentSuggestions.strings(stringSuggestions));
            }
        } else {
            argument = BrigArgument.getArgument(this.pattern - 1, arg);
        }

        argument.setOptional(this.optional);
        brigCommand.addArgument(arg, argument);

    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "register string arg " + this.brigArg.toString(e, d);
    }

}
