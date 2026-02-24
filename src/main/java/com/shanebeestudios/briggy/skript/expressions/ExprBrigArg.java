package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.event.BrigCommandSuggestEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandTriggerEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeTriggerEvent;
import com.shanebeestudios.briggy.api.skript.Registration;
import com.shanebeestudios.briggy.api.util.ObjectConverter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExprBrigArg extends SimpleExpression<Object> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprBrigArg.class, Object.class,
                "brig-arg-%*number%")
            .name("Brig Command Arg")
            .description("Represents the arguments in a Brig Command. These works the same way as Skript's `arg` and `arg-1`.",
                "Since command args create local variables at runtime, these are virtually useless.",
                "These can be used in both the `register argument` and `trigger` sections.")
            .examples("brig command /i <item> <int>:",
                "\ttrigger:",
                "\t\tgive brig-arg-2 of brig-arg-1 to player")
            .since("1.0.0")
            .register();
    }

    private Literal<Number> argNum;

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandTriggerEvent.class, BrigCommandSuggestEvent.class, BrigTreeTriggerEvent.class)) {
            Skript.error("'brig-arg' can only be used in a brig command 'trigger' and 'register arg' sections.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.argNum = (Literal<Number>) exprs[0];
        return true;
    }

    @Override
    protected Object[] get(Event event) {
        Object[] args;
        switch (event) {
            case BrigCommandTriggerEvent triggerEvent -> args = triggerEvent.getArgs();
            case BrigCommandSuggestEvent suggestEvent -> args = suggestEvent.getBrigArgs();
            case BrigTreeTriggerEvent treeEvent -> args = treeEvent.getArgs();
            case null, default -> {
                return null;
            }
        }

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
