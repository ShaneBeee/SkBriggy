package com.shanebeestudios.briggy.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandCreateEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffRegisterPermission extends Effect {

    static {
        Skript.registerEffect(EffRegisterPermission.class, "register permission %string%");
    }

    private Expression<String> permission;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.permission = (Expression<String>) exprs[0];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        BrigCommandCreateEvent brigCommandEvent = (BrigCommandCreateEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        String permission = this.permission.getSingle(event);
        brigCommand.setPermission(permission);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        return "register permission " + this.permission.toString(e, d);
    }

}
