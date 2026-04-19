package com.shanebeestudios.briggy.skript.testing;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprRunCommand extends SimpleExpression<Boolean> {

    private static final CommandSender CONSOLE = Bukkit.getConsoleSender();

    public static void register(Registration reg) {
        reg.newCombinedExpression(ExprRunCommand.class, Boolean.class,
                "run command %string%")
            .noDoc()
            .register();
    }

    private Expression<String> command;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.command = (Expression<String>) exprs[0];
        return true;
    }


    @Override
    protected Boolean @Nullable [] get(Event event) {
        String command = this.command.getSingle(event);
        assert command != null;
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        return new Boolean[]{Bukkit.dispatchCommand(CONSOLE, command)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "run command " + this.command.toString(event, debug);
    }

}
