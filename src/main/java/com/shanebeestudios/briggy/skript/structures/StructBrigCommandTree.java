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
import com.shanebeestudios.briggy.api.event.BrigTreeCreateEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeSubCommandEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeTriggerEvent;
import com.shanebeestudios.skbee.api.util.Util;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.executors.ExecutorType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;
import org.skriptlang.skript.lang.entry.util.LiteralEntryData;
import org.skriptlang.skript.lang.script.Script;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Name("CommandTree - Command")
@Description({"Command trees are similar to regular commands with the difference being the arguments are in a tree.",
    "By having a tree, each argument can have sub args as well as their own triggers.",
    "See [**SkBriggy Wiki**](https://github.com/ShaneBeee/SkBriggy/wiki/Command-Tree) for more detailed info.",
    "",
    "Command names can include namespaces, ex: `brig command tree /mycommands:somecommand`.",
    "Defaults to `minecraft` when excluded.",
    "",
    "**Entries/Sections**:",
    "`executor_type` = What types of execturs can run this command (Optional, defaults to `all`).",
    "`permission` = Just like Skript, the permission the player will require for this command.",
    "`description` = Just like Skript, this is a string that will be used in the help command.",
    "`usages` = This is the usage which is shown in the specific `/help <command>` page. Separate multiple usages by comma.",
    "`aliases` = Aliases for this command.",
    "`override` = Whether to completely wipe out other commands with the same name, such as vanilla Minecraft commands (Defaults to false).",
    "`register arg` = Register another subcommand within this one. Supports multiple.",
    "`trigger` = Like any other command, this is what will execute when the command is run."})
@Examples({"# Example with optional arg that can be bypassed",
    "brig command tree /legamemode:",
    "\tliteral arg \"gamemode\" using \"adventure\", \"creative\", \"spectator\", \"survival\":",
    "\t\t# When optional, the trigger will still run but the arg is ignored",
    "\t\toptional players arg \"players\":",
    "\t\t\ttrigger:",
    "\t\t\t\t# if the player arg is not used, we will default to the command sender",
    "\t\t\t\tset {_players::*} to {_players::*} ? player",
    "\t\t\t\tset {_gamemode} to {_gamemode} parsed as gamemode",
    "\t\t\t\tset gamemode of {_players::*} to {_gamemode}",
    "",
    "# Example similar to above but using 2 different triggers",
    "brig command tree /spawn:",
    "\tworld arg \"world\":",
    "\t\ttrigger:",
    "\t\t\tteleport player to spawn of {_world}",
    "\t# if the argument isn't entered, this will execute",
    "\ttrigger:",
    "\t\tteleport player to spawn of world of player",
    "",
    "# Example showing off suggestions with tooltips",
    "brig command tree /lewarp:",
    "\tstring arg \"warp\":",
    "\t\tsuggestions:",
    "\t\t\tloop {warps::*}:",
    "\t\t\t\tset {_s} to \"&7x: &b%x coord of loop-value% &7y: &b%y coord of loop-value% &7z: &b%z coord of loop-value% &7world: &a%world of loop-value%\"",
    "\t\t\t\tapply suggestion loop-index with tooltip {_s}",
    "\t\ttrigger:",
    "\t\t\tif {warps::%{_warp}%} is set:",
    "\t\t\t\tteleport player to {warps::%{_warp}%}",
    "\t\t\telse:",
    "\t\t\t\tsend \"No warp available for %{_warp}%\"",
    "",
    "brig command tree /leban:",
    "\tdescription: &bThis allows you to ban players",
    "\tusages: /leban &7<&bplayers&7> &7<&btimespan&7>",
    "\tplayers arg \"players\":",
    "\t\tint arg \"time\":",
    "\t\t\tstring arg \"span\" using \"minutes\", \"hours\", \"days\":",
    "\t\t\t\t# When optional, the trigger will still run but the arg is ignored",
    "\t\t\t\toptional greedy string arg \"reason\":",
    "\t\t\t\t\ttrigger:",
    "\t\t\t\t\t\tset {_timespan} to \"%{_time}% %{_span}%\" parsed as timespan",
    "\t\t\t\t\t\tset {_reason} to {_reason} ? \"Unknown Reason\"",
    "\t\t\t\t\t\tban {_players::*} due to \"&c\" + {_reason} for {_timespan}",
    "\t\t\t\t\t\tkick {_players::*} due to \"&c\" + {_reason}"})
@Since("1.4.0")
public class StructBrigCommandTree extends Structure {

    private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

    static {
        @SuppressWarnings("RedundantIfStatement")
        EntryValidator entryValidator = EntryValidator.builder()
            .addEntry("permission", null, true)
            .addEntry("description", "SkBriggy Command", true)
            .addEntryData(new LiteralEntryData<>("override", false, true, Boolean.class))
            .addEntryData(new KeyValueEntryData<List<ExecutorType>>("executor_type", new ArrayList<>(), true) {
                @Override
                protected @NotNull List<ExecutorType> getValue(String value) {
                    List<ExecutorType> executorTypes = new ArrayList<>();
                    for (String s : COMMA_PATTERN.split(value)) {
                        try {
                            ExecutorType executorType = ExecutorType.valueOf(s.toUpperCase());
                            executorTypes.add(executorType);
                        }  catch (IllegalArgumentException e) {
                            Skript.error("Invalid executor_type: " + s);
                        }
                    }
                    return executorTypes;
                }
            })
            .addEntryData(new KeyValueEntryData<List<String>>("usages", new ArrayList<>(), true) {
                @Override
                protected List<String> getValue(String value) {
                    value = Util.getColString(value);
                    List<String> usages = new ArrayList<>();
                    Arrays.asList(COMMA_PATTERN.split(value)).forEach(usage -> usages.add("§r" + usage));
                    return usages;
                }
            })
            .addEntryData(new KeyValueEntryData<List<String>>("aliases", new ArrayList<>(), true) {
                @Override
                protected List<String> getValue(String value) {
                    value = value.replace("/", "");
                    List<String> aliases = new ArrayList<>(Arrays.asList(COMMA_PATTERN.split(value)));
                    if (aliases.getFirst().isEmpty()) return null;
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

    private EntryContainer entryContainer;
    private String namespace = "minecraft";
    private String command;
    private boolean override = false;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, EntryContainer entryContainer) {
        this.entryContainer = entryContainer;
        String command = parseResult.regexes.getFirst().group();
        if (command.contains(":")) {
            String[] split = command.split(":");
            this.namespace = split[0];
            this.command = split[1];
        } else {
            this.command = command;
        }
        if (this.command.contains(" ")) {
            Skript.error("Commands cannot contain spaces: '" + this.command + "'");
            return false;
        }
        return true;
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public boolean load() {
        Script currentScript = getParser().getCurrentScript();
        getParser().setCurrentEvent("BrigTreeCreate", BrigTreeCreateEvent.class);

        CommandTree commandTree = new CommandTree(this.command);

        // Register executor types
        List<ExecutorType> executorTypes = (List<ExecutorType>) this.entryContainer.get("executor_type", true);
        if (executorTypes.isEmpty()) executorTypes.add(ExecutorType.ALL);

        // Register command permission
        String permission = this.entryContainer.getOptional("permission", String.class, false);
        if (permission != null) commandTree.withPermission(permission);

        // Register command description
        String description = this.entryContainer.getOptional("description", String.class, true);
        assert description != null;
        description = Utils.replaceEnglishChatStyles(description);
        commandTree.withShortDescription(description);

        // Override vanilla command
        Boolean override = this.entryContainer.getOptional("override", Boolean.class, false);
        if (override != null) this.override = override;

        // Register command usage
        List<String> usages = (List<String>) this.entryContainer.get("usages", true);
        if (!usages.isEmpty()) commandTree.withUsage(usages.toArray(new String[0]));

        // Register command aliases
        List<String> aliases = (List<String>) this.entryContainer.get("aliases", true);
        commandTree.withAliases(aliases.toArray(new String[0]));

        // Register sub commands
        boolean hasSubCommand = false;
        getParser().setCurrentEvent("BrigTreeSubCommand", BrigTreeSubCommandEvent.class);
        for (Node node : this.entryContainer.getUnhandledNodes()) {
            if (node instanceof SectionNode sectionNode) {
                Section parse = Section.parse(node.getKey(), "Invalid section: " + node.getKey(), sectionNode, null);
                if (parse == null) return false;

                hasSubCommand = true;
                Section.walk(parse, new BrigTreeSubCommandEvent(commandTree));
            }
        }

        // Register command trigger
        SectionNode triggerNode = this.entryContainer.getOptional("trigger", SectionNode.class, false);
        if (triggerNode != null) {
            getParser().setCurrentEvent("BrigTreeTrigger", BrigTreeTriggerEvent.class);
            Trigger triggerTrigger = new Trigger(currentScript, "briggy command /" + this.command, new SimpleEvent(), ScriptLoader.loadItems(triggerNode));
            commandTree.executes(executionInfo -> {
                triggerTrigger.execute(new BrigTreeTriggerEvent(executionInfo));
            }, executorTypes.toArray(new ExecutorType[0]));
        } else if (!hasSubCommand) {
            Skript.error("Command tree must have at least a subcommand or a trigger.");
            return false;
        }
        if (this.override) {
            CommandAPI.unregister(this.command, true);
        }
        commandTree.register(this.namespace);
        return true;
    }

    @Override
    public void unload() {
        CommandAPI.unregister(this.command, this.override);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "brig command tree /" + this.namespace + ":" + this.command;
    }

}
