package com.shanebeestudios.briggy.skript.elements.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.event.BrigCommandRunEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExprBrigArg extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprBrigArg.class, Object.class, ExpressionType.SIMPLE,
                "brig-arg-%number%");
    }

    private Literal<Number> argNum;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandRunEvent.class)) {
            Skript.error("'brig-arg' can only be used in a 'to execute' section", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.argNum = (Literal<Number>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected Object[] get(Event event) {
        BrigCommandRunEvent brigCommandRunEvent = (BrigCommandRunEvent) event;
        Object[] args = brigCommandRunEvent.getArgs();
        int i = this.argNum.getSingle().intValue();
        if (args.length >= i) {
            Object arg = args[i - 1];
            if (arg instanceof List<?> list) {
                return list.toArray(new Object[0]);
            }
            return new Object[]{arg};
        }
        return null;
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
        return "null";
    }

}
