package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.event.BrigCommandSuggestEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandTriggerEvent;
import com.shanebeestudios.briggy.api.util.ObjectConverter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Name("Brig Command Arg")
@Description({"Represents the arguments in a Brig Command. These works the same way as Skript's `arg` and `arg-1`.",
        "Since command args create local variables at runtime, these are virtually useless.",
        "These can be used in both the `register argument` and `trigger` sections."})
@Examples({"brig command /i <item> <int>:",
        "\ttrigger:",
        "\t\tgive brig-arg-2 of brig-arg-1 to player"})
@Since("1.0.0")
public class ExprBrigArg extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBrigArg.class, Object.class, ExpressionType.SIMPLE,
                "brig-arg-%*number%");
    }

    private Literal<Number> argNum;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandTriggerEvent.class, BrigCommandSuggestEvent.class)) {
            Skript.error("'brig-arg' can only be used in a brig command 'trigger' and 'register arg' sections.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.argNum = (Literal<Number>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event) {
        Object[] args;
        if (event instanceof BrigCommandTriggerEvent triggerEvent) args = triggerEvent.getArgs();
        else if (event instanceof BrigCommandSuggestEvent suggestEvent) args = suggestEvent.getArgs();
        else return null;

        List<Object> objects = new ArrayList<>();
        int i = this.argNum.getSingle().intValue();
        if (args.length >= i) {
            Object arg = args[i - 1];
            if (arg instanceof List<?> list) {
                objects.addAll(list);
            } else {
                objects.add(arg);
            }
        }
        return ObjectConverter.convert(objects);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "brig-arg-" + this.argNum.toString(e, d);
    }

}
