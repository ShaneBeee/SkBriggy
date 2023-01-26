package com.shanebeestudios.briggy.skript.elements.section;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandCreateEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandRunEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import java.util.List;

public class SecExecute extends Section {

    static {
        Skript.registerSection(SecExecute.class, "to execute");
        EventValues.registerEventValue(BrigCommandRunEvent.class, CommandSender.class, new Getter<>() {
            @Override
            public CommandSender get(BrigCommandRunEvent event) {
                return event.getSender();
            }
        }, 0);
    }

    private Trigger trigger;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(BrigCommandCreateEvent.class)) {
            Skript.error("'to execute' can only be using in a brig command!", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        if (sectionNode != null) {
            trigger = loadCode(sectionNode, "brig-run", BrigCommandRunEvent.class);
        }
        return true;
    }

    public void execute(CommandSender sender, Object[] args) {
        for (Object arg : args) {
            sender.sendMessage("Arg: " + arg);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected TriggerItem walk(Event event) {
        BrigCommandCreateEvent brigCommandEvent = (BrigCommandCreateEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        brigCommand.addExecution(this.trigger);
        brigCommand.build();;
        return null;
    }

    @Override
    public String toString(Event e, boolean d) {
        return "null";
    }

}
