package com.shanebeestudios.briggy.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.Variables;
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

@Name("Brig - Register Argument")
@Since("INSERT VERSION")
public class EffRegisterArg extends Effect {

    static {
        String stringPattern;
        StringBuilder stringBuilder = new StringBuilder();
        int[] i = new int[1];
        BrigArgument.getNames().forEach((s) -> {
            stringBuilder.append(i[0]);
            stringBuilder.append(":");
            stringBuilder.append(s);
            stringBuilder.append("|");
            i[0]++;
        });

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringPattern = stringBuilder.toString();

        Skript.registerEffect(EffRegisterArg.class,
                "register string arg %string% [using %-objects%]",
                "register (" + stringPattern + ") arg %string% ");
    }

    private Expression<String> brigArg;
    private Expression<Object> suggestions;
    private int pattern;
    private boolean stringArg;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandCreateEvent.class)) {
            Skript.error("A brig arg can only be registered in a brig command!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.stringArg = matchedPattern == 0;
        this.brigArg = (Expression<String>) exprs[0];
        if (matchedPattern == 0) {
            this.suggestions = (Expression<Object>) exprs[1];
        }
        this.pattern = parseResult.mark;
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        BrigCommandCreateEvent brigCommandEvent = (BrigCommandCreateEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        String arg = this.brigArg.getSingle(event);

        Argument<?> argument;
        if (this.stringArg) {
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
            argument = BrigArgument.getArgument(this.pattern, arg);
        }

        brigCommand.addParam(arg, argument);

    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "register string arg " + this.brigArg.toString(e, d);
    }

}
