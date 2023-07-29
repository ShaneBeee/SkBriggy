package com.shanebeestudios.briggy.skript.structures;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.util.SimpleEvent;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandCreateEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandRunEvent;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;

@Name("Brig Command")
@Description("Register a new Brigadier command.")
@Since("INSERT VERSION")
public class StructBrigCommand extends Structure {

    static {
        EntryValidator entryValidator = EntryValidator.builder()
                .addEntry("permission", null, true)
                .addSection("arguments", true)
                .addSection("trigger", false)
                .build();
        Skript.registerStructure(StructBrigCommand.class, entryValidator, "brig[gy] command /<.+>");
    }

    private String command;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, EntryContainer entryContainer) {
        this.command = parseResult.regexes.get(0).group();
        return true;
    }

    @Override
    public boolean load() {
        // Create Command
        Script currentScript = getParser().getCurrentScript();
        getParser().setCurrentEvent("brig command", BrigCommandCreateEvent.class);
        EntryContainer entryContainer = getEntryContainer();
        BrigCommand brigCommand = new BrigCommand(this.command);

        SectionNode argNode = entryContainer.get("arguments", SectionNode.class, false);
        Trigger argTrigger = new Trigger(currentScript, "briggy command /" + this.command, new SimpleEvent(), ScriptLoader.loadItems(argNode));
        Trigger.walk(argTrigger, new BrigCommandCreateEvent(brigCommand));

        String permission = entryContainer.getOptional("permission", String.class, false);
        brigCommand.setPermission(permission);

        // Run Command
        getParser().setCurrentEvent("brig command", BrigCommandRunEvent.class);
        SectionNode triggerNode = entryContainer.get("trigger", SectionNode.class, false);
        Trigger trigger = new Trigger(currentScript, "briggy command /" + this.command, new SimpleEvent(), ScriptLoader.loadItems(triggerNode));
        trigger.setLineNumber(triggerNode.getLine());

        brigCommand.addExecution(trigger);
        brigCommand.build();

        getParser().deleteCurrentEvent();
        return true;
    }

    @Override
    public void unload() {
        CommandAPI.unregister(this.command, true);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "briggy command";
    }

}
