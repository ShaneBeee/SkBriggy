package com.shanebeestudios.briggy.skript.structures;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Utils;
import com.shanebeestudios.briggy.SkBriggy;
import com.shanebeestudios.briggy.api.event.BrigTreeCreateEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeSubCommandEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeTriggerEvent;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;
import org.skriptlang.skript.lang.entry.util.VariableStringEntryData;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Name("CommandTree - Command")
public class StructBrigCommandTree extends Structure {

    private static final Pattern ALIASES_PATTERN = Pattern.compile("\\s*,\\s*");

    static {
        @SuppressWarnings("DataFlowIssue")
        EntryValidator entryValidator = EntryValidator.builder()
            .addEntry("permission", null, true)
            .addEntry("description", "SkBriggy Command", true)
            .addEntryData(new VariableStringEntryData("usage", null, true))
            .addEntryData(new KeyValueEntryData<List<String>>("aliases", new ArrayList<>(), true) {
                @SuppressWarnings("NullableProblems")
                @Override
                protected List<String> getValue(String value) {
                    value = value.replace("/", "");
                    List<String> aliases = new ArrayList<>(Arrays.asList(ALIASES_PATTERN.split(value)));
                    if (aliases.get(0).isEmpty()) return null;
                    return aliases;
                }
            })
            .addSection("trigger", true)
            .unexpectedNodeTester(node -> {
                if (node instanceof SectionNode sectionNode) {
                    String key = sectionNode.getKey();
                    return key == null || !key.contains("register") || !key.contains("sub") || !key.contains("command");
                }
                return true;
            })
            .build();
        Skript.registerStructure(StructBrigCommandTree.class, entryValidator, "brig[(gy|adier)] command[ ]tree /<.+>");
    }

    private String command;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, EntryContainer entryContainer) {
        this.command = parseResult.regexes.get(0).group();
        return true;
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public boolean load() {
        Script currentScript = getParser().getCurrentScript();
        getParser().setCurrentEvent("BrigTreeCreate", BrigTreeCreateEvent.class);

        EntryContainer entryContainer = getEntryContainer();
        CommandTree commandTree = new CommandTree(this.command);

        // Register command permission
        String permission = entryContainer.getOptional("permission", String.class, false);
        commandTree.withPermission(permission);

        // Register command description
        String description = entryContainer.getOptional("description", String.class, true);
        assert description != null;
        description = Utils.replaceEnglishChatStyles(description);
        commandTree.withFullDescription(description);

        // Regiseter command usage
        VariableString usage = entryContainer.getOptional("usage", VariableString.class, false);
        if (usage != null && usage.isSimple()) {
            String string = usage.toString(null);
            commandTree.withUsage(string);
        }
        // Register command aliases
        List<String> aliases = (List<String>) entryContainer.get("aliases", true);
        commandTree.withAliases(aliases.toArray(new String[0]));

        // Register sub commands
        boolean hasSubCommand = false;
        getParser().setCurrentEvent("BrigTreeSubCommand", BrigTreeSubCommandEvent.class);
        for (Node node : entryContainer.getUnhandledNodes()) {
            if (node instanceof SectionNode sectionNode) {
                Section parse = Section.parse(node.getKey(), "Invalid section: " + node.getKey(), sectionNode, null);
                if (parse == null) return false;

                hasSubCommand = true;
                Section.walk(parse, new BrigTreeSubCommandEvent(commandTree));
            }
        }

        // Register command trigger
        SectionNode triggerNode = entryContainer.getOptional("trigger", SectionNode.class, false);
        if (triggerNode != null) {
            getParser().setCurrentEvent("BrigTreeTrigger", BrigTreeTriggerEvent.class);
            Trigger triggerTrigger = new Trigger(currentScript, "briggy command /" + this.command, new SimpleEvent(), ScriptLoader.loadItems(triggerNode));
            commandTree.executes(executionInfo -> {
                triggerTrigger.execute(new BrigTreeTriggerEvent(executionInfo));
            });
        } else if (!hasSubCommand) {
            Skript.error("Command tree must have at least a subcommand or a trigger.");
            return false;
        }
        Bukkit.getScheduler().runTaskLater(SkBriggy.getInstance(), () -> commandTree.register(), 1);
        return true;
    }

    @Override
    public void unload() {
        CommandAPI.unregister(this.command, true);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "brig command tree /" + this.command;
    }

}
