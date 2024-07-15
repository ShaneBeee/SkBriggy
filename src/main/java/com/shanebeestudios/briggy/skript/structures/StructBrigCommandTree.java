package com.shanebeestudios.briggy.skript.structures;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Utils;
import com.shanebeestudios.briggy.SkBriggy;
import com.shanebeestudios.briggy.api.event.BrigTreeCreateEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeSubCommandEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeTriggerEvent;
import com.shanebeestudios.skbee.api.util.Util;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Name("CommandTree - Command")
@Description({"Command trees are similar to regular commands with the difference being the arguments are in a tree.",
    "By having a tree, each argument can have sub args as well as their own triggers.",
    "",
    "**Entries/Sections**:",
    "`permission` = Just like Skript, the permission the player will require for this command.",
    "`description` = Just like Skript, this is a string that will be used in the help command.",
    "`usages` = This is the usage which is shown in the specific `/help <command>` page. Separate multiple usages by comma.",
    "`aliases` = Aliases for this command.",
    "`suggestions` = You can apply suggestions (with tooltips) to a subcommand. See `apply suggestion` effect, and examples.",
    "`register arg` = Register another subcommand within this one. Supports multiple.",
    "`trigger` = Like any other command, this is what will execute when the command is run."})
@Examples("")
@Since("INSERT VERSION")
public class StructBrigCommandTree extends Structure {

    private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

    static {
        @SuppressWarnings("DataFlowIssue")
        EntryValidator entryValidator = EntryValidator.builder()
            .addEntry("permission", null, true)
            .addEntry("description", "SkBriggy Command", true)
            .addEntryData(new KeyValueEntryData<List<String>>("usages", new ArrayList<>(), true) {
                @SuppressWarnings("NullableProblems")
                @Override
                protected List<String> getValue(String value) {
                    value = Util.getColString(value);
                    List<String> usages = new ArrayList<>();
                    Arrays.asList(COMMA_PATTERN.split(value)).forEach(usage -> usages.add("§r" + usage));
                    return usages;
                }
            })
            .addEntryData(new KeyValueEntryData<List<String>>("aliases", new ArrayList<>(), true) {
                @SuppressWarnings("NullableProblems")
                @Override
                protected List<String> getValue(String value) {
                    value = value.replace("/", "");
                    List<String> aliases = new ArrayList<>(Arrays.asList(COMMA_PATTERN.split(value)));
                    if (aliases.get(0).isEmpty()) return null;
                    return aliases;
                }
            })
            .addSection("register arg section", true) // Dummy for docs
            .addSection("trigger", true)
            .unexpectedNodeTester(node -> {
                if (node instanceof SectionNode sectionNode) {
                    String key = sectionNode.getKey();
                    // TODO proper parsing
                    if (key != null && key.contains("arg") && key.contains("\"")) {
                        return false;
                    }
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
        commandTree.withShortDescription(description);

        // Regiseter command usage
        List<String> usages = (List<String>) entryContainer.get("usages", true);
        commandTree.withUsage(usages.toArray(new String[0]));

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
