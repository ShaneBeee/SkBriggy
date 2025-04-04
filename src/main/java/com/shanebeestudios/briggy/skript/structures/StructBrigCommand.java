package com.shanebeestudios.briggy.skript.structures;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.test.runner.TestMode;
import ch.njol.skript.util.Utils;
import ch.njol.util.StringUtils;
import com.shanebeestudios.briggy.SkBriggy;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandArgumentsEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandTriggerEvent;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Name("Brig Command")
@Description({"Register a new Brigadier command.",
    "See wiki for more details on registering: [**SkBriggy Wiki**](https://github.com/ShaneBeee/SkBriggy/wiki/Registering-New-Command)",
    "",
    "Command Format:",
    "`brig command /namespace:commandName <brigArgType> [<brigArgType>] <argName:brigArgType> [<argName:brigArgType>]:`",
    "`namespace` = The namespace of a command. (Optional, defaults to \"minecraft:\")",
    "`commandName` = Represents the command itself, ex: '/mycommand'.",
    "`brigArgType` = Represents a brig argument type.",
    "- While some may match Skript types, this doesn't actually support Skript types.",
    "`argName` = The name of the arg, which will be used to create a local variable for the arg.",
    "- In some cases this will show when typing out a command in game.",
    "- If this isn't set a local variable will be created using the type (see examples).",
    "",
    "Just like Skript commands, wrapping your arg in `[]` makes it optional. Do note at this time there is no support for defaults.",
    "",
    "Entries and Sections:",
    "`permission:` = Just like Skript, the permission the player will require for this command.",
    "`description:` = Just like Skript, this is a string that will be used in the help command.",
    "`arguments:` = Section for registering arguments. See `Register Argument` effect.",
    "`trigger:` = Section, just like Skript, for executing your code in the command.",
    "",
    "Event Values:",
    "`[event-]sender` = The sender (console/player/entity) who ran the command or the sender used in Minecraft's `/execute as <sender>` command.",
    "`[event-]world` = World of the sender, or default world if sender is console, or world used in Minecraft's `/execute in <world>` command."})
@Examples({"brig command /move <player> <location>:",
    "\ttrigger:",
    "\t\tteleport {_player} to {_location}",
    "",
    "brig command /move <p1:player> <p2:player>:",
    "\ttrigger:",
    "\t\tteleport {_p1} to {_p2}",
    "",
    "brig command /i <item> [<amount:int>]:",
    "\ttrigger:",
    "\t\tset {_amount} to 1 if {_amount} isn't set",
    "\t\tgive {_amount} of {_item} to player",
    "",
    "brig command /coolserver:party:",
    "\tpermission: coolserver.party",
    "\ttrigger:",
    "\t\tteleport all players to spawn of world \"world_party\""})
@Since("1.0.0")
public class StructBrigCommand extends Structure {

    private static final SkBriggy PLUGIN = SkBriggy.getInstance();
    private static final Pattern ALIASES_PATTERN = Pattern.compile("\\s*,\\s*");
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\[?<.*?>]?");

    static {
        EntryValidator entryValidator = EntryValidator.builder()
            .addEntry("permission", null, true)
            .addEntry("description", "SkBriggy Command", true)
            .addEntryData(new VariableStringEntryData("usage", null, true))
            .addEntryData(new KeyValueEntryData<List<String>>("aliases", new ArrayList<>(), true) {
                @Override
                protected List<String> getValue(String value) {
                    value = value.replace("/", "");
                    List<String> aliases = new ArrayList<>(Arrays.asList(ALIASES_PATTERN.split(value)));
                    if (aliases.getFirst().isEmpty()) return null;
                    return aliases;
                }
            })
            .addSection("arguments", true)
            .addSection("trigger", false)
            .build();
        Skript.registerStructure(StructBrigCommand.class, entryValidator, "brig[(gy|adier)] command /<.+>");
    }

    private EntryContainer entryContainer;
    private String command;
    private String argString;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult, EntryContainer entryContainer) {
        this.entryContainer = entryContainer;
        String[] split = parseResult.regexes.getFirst().group().split(" ", 2);
        this.command = split[0];
        this.argString = split.length > 1 ? split[1] : null;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean load() {
        // Create Command
        Script currentScript = getParser().getCurrentScript();
        getParser().setCurrentEvent("BrigCommandArguments", BrigCommandArgumentsEvent.class);
        BrigCommand brigCommand = new BrigCommand(this.command);

        // Register command arguments
        if (!parseArgs(brigCommand)) {
            return false;
        }

        // Register section arguments
        SectionNode argNode = this.entryContainer.getOptional("arguments", SectionNode.class, false);
        if (argNode != null) {
            Trigger argTrigger = new Trigger(currentScript, "briggy command /" + this.command, new SimpleEvent(), ScriptLoader.loadItems(argNode));
            Trigger.walk(argTrigger, new BrigCommandArgumentsEvent(brigCommand));
        }

        // Register command permission
        String permission = this.entryContainer.getOptional("permission", String.class, false);
        brigCommand.setPermission(permission);

        // Register command description
        String description = this.entryContainer.getOptional("description", String.class, true);
        assert description != null;
        description = Utils.replaceEnglishChatStyles(description);
        brigCommand.setDescription(description);

        // Regiseter command usage
        VariableString usage = this.entryContainer.getOptional("usage", VariableString.class, false);
        if (usage != null && usage.isSimple()) {
            String string = usage.toString(null);
            brigCommand.setUsage(string);
        }

        // Register command aliases
        List<String> aliases = (List<String>) this.entryContainer.get("aliases", true);
        brigCommand.setAliases(aliases);

        // Register command trigger
        getParser().setCurrentEvent("BrigCommandTrigger", BrigCommandTriggerEvent.class);
        SectionNode triggerNode = this.entryContainer.get("trigger", SectionNode.class, false);
        Trigger trigger = new Trigger(currentScript, "briggy command /" + this.command, new SimpleEvent(), ScriptLoader.loadItems(triggerNode));
        trigger.setLineNumber(triggerNode.getLine());

        // Build command
        brigCommand.addExecution(trigger);

        if (TestMode.ENABLED) { // Tests can't have delays
            brigCommand.build();
        } else {
            // Run later to prevent reload issues
            Bukkit.getScheduler().runTaskLater(PLUGIN, brigCommand::build, 0);
        }

        getParser().deleteCurrentEvent();
        return true;
    }

    private boolean parseArgs(BrigCommand brigCommand) {
        if (this.argString == null) return true;

        int argLength = this.argString.length();
        // Verify correct placement of optional brackets around arguments
        int opt1 = StringUtils.count(this.argString, '[', 0, argLength);
        int opt2 = StringUtils.count(this.argString, ']', 0, argLength);
        if (opt1 != opt2) {
            Skript.error("Invalid placement of [] around arguments.");
            return false;
        }

        // Verify correct placement of <> around arguments
        int arg1 = StringUtils.count(this.argString, '<', 0, argLength);
        int arg2 = StringUtils.count(this.argString, '>', 0, argLength);
        if (arg1 != arg2) {
            Skript.error("Invalid placement of <> around arguments.");
            return false;
        }

        List<String> args = new ArrayList<>();
        Matcher matcher = ARGUMENT_PATTERN.matcher(this.argString);
        while (matcher.find()) {
            args.add(matcher.group());
        }

        for (String arg : args) {
            boolean optional = false;
            if (arg.startsWith("[") && arg.endsWith("]")) {
                optional = true;
                arg = arg.replace("[", "").replace("]", "");
            }
            arg = arg.replace("<", "").replace(">", "");

            String name;
            String type;
            if (arg.contains(":")) {
                String[] split = arg.split(":");
                name = split[0];
                type = split[1];
            } else {
                name = arg;
                type = arg;
            }
            BrigArgument brigArgument = BrigArgument.parse(type);
            if (brigArgument == null) {
                Skript.error("Invalid brig argument type '" + type + "'");
                return false;
            }
            if (brigArgument.getArgClass() == MultiLiteralArgument.class) {
                Skript.error("<" + arg + "> arguments cannot be used in a command. " +
                    "You can use them in the arguments section instead.");
                return false;
            }

            Map<String, Argument<?>> registeredArgs = brigCommand.getArgumentMap();
            // If the arg name already exists, let's append a number to it
            if (registeredArgs.containsKey(name)) {
                // Start at 2 so we get things like
                // int, int2, int3, int4
                for (int i = 2; i < 50; i++) {
                    String testname = name + i;
                    if (!registeredArgs.containsKey(testname)) {
                        name = testname;
                        break;
                    }
                }
            }

            Argument<?> argument = brigArgument.getArgument(name);
            argument.setOptional(optional);

            // GreedyString args have to be last
            List<Argument<?>> brigArgs = brigCommand.getArguments();
            if (!brigArgs.isEmpty() && brigArgs.getLast() instanceof GreedyStringArgument) {
                Skript.error("You cannot place another arg after a <greedystring> arg.");
                return false;
            }

            brigCommand.addArgument(name, argument);

        }
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
