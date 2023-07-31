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
        Skript.registerEffect(EffRegisterArg.class,
                "register [:optional] string arg[ument] %string% [using %-objects%]",
                "register [:optional] %brigarg% arg[ument] %string%");
    }

    private Expression<String> argument;
    private Expression<?> suggestions;
    private Expression<BrigArgument> brigArg;
    private boolean optional;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandCreateEvent.class)) {
            Skript.error("A brig arg can only be registered in a brig command!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.argument = (Expression<String>) exprs[matchedPattern];
        if (matchedPattern == 0) {
            this.suggestions = exprs[1];
        }
        this.brigArg = matchedPattern == 1 ? (Expression<BrigArgument>) exprs[0] : null;
        this.optional = parseResult.hasTag("optional");
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        BrigCommandCreateEvent brigCommandEvent = (BrigCommandCreateEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        String arg = this.argument.getSingle(event);

        Argument<?> argument;
        if (this.brigArg == null) {
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
            BrigArgument brigArg = this.brigArg.getSingle(event);
            if (brigArg == null) return;
            argument = brigArg.getArgument(arg);
        }

        argument.setOptional(this.optional);
        brigCommand.addArgument(arg, argument);

    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String type = this.brigArg == null ? "string" : this.brigArg.toString(e, d);
        String suggestions = this.suggestions == null ? "" : (" using " + this.suggestions.toString(e, d));
        return "register " + type + " arg " + this.argument.toString(e, d) + suggestions;
    }

}
