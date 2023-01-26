package com.shanebeestudios.briggy.skript.elements.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandCreateEvent;
import com.shanebeestudios.briggy.api.util.Utils;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.event.Event;

public class EvtBrigCommand extends SelfRegisteringSkriptEvent {

    static {
        Skript.registerEvent("brig", EvtBrigCommand.class, BrigCommandCreateEvent.class,
                "brig command /<.+>");
    }

    private String command;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        this.command = parseResult.regexes.get(0).group();
        return true;
    }

    @Override
    public void register(Trigger trigger) {;
        BrigCommand brigCommand = new BrigCommand(this.command);
        BrigCommandCreateEvent brigCommandEvent = new BrigCommandCreateEvent(brigCommand);
        trigger.execute(brigCommandEvent);
    }

    @Override
    public void unregister(Trigger trigger) {
        CommandAPI.unregister(this.command, true);
        Utils.reloadCommands();
    }

    @Override
    public void unregisterAll() {
    }


    @Override
    public String toString(Event e, boolean debug) {
        return "null";
    }
}
