package com.shanebeestudios.briggy.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.event.BrigCommandSuggestEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeSubCommandEvent;
import com.shanebeestudios.briggy.api.event.BrigTreeTriggerEvent;
import com.shanebeestudios.briggy.api.util.ObjectConverter;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@Name("CommandTree - SubCommand")
@Description({"Register a sub command in a command tree.",
    "A sub command is just an argument that can have its own sub commands and triggers.",
    "See [**SkBriggy Wiki**](https://github.com/ShaneBeee/SkBriggy/wiki/Command-Tree) for more detailed info.",
    "**Notes**:",
    "- A `greedy string` arg always has to be last, you cannot register another subcommand within it.",
    "- Optionals are a little funny, you cannot have a required subcommand within an optional subcommand.",
    "- Min/Max can only be used on number subcommands.",
    "- The name/id you choose for your subcommand will automatically be made into a local variable.",
    "- List arg types (ie: players/entities) will create list variables. DO NOT repeat names. See examples.",
    "",
    "**Entries/Sections**:",
    "`permission` = Each subcommand can have its own permission.",
    "`suggestions` = You can apply suggestions (with tooltips) to a subcommand. See `apply suggestion` effect, and examples.",
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
public class SecSubCommand extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        //noinspection DataFlowIssue
        VALIDATOR
            .addEntry("permission", null, true)
            .addSection("suggestions", true)
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
            }).build();

        String base = "[:optional] %*brigarg% arg[ument] [(named|with (name|id))] %*string%";
        Skript.registerSection(SecSubCommand.class,
            base,
            base + " (with suggestions|using) %objects%",
            base + " with min %number% [and] [with] max %number%");
    }

    // Section Pattern
    private int pattern;
    private boolean optional;
    private Literal<BrigArgument> brigArg;
    private Literal<String> commandName;
    private Expression<?> suggestions;
    private Expression<Number> min;
    private Expression<Number> max;

    // Entries
    private String permission;

    // Sections
    private final List<Section> sections = new ArrayList<>();
    private Trigger suggestionsTrigger;
    private Trigger trigger;

    @SuppressWarnings({"NullableProblems", "unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.build().validate(sectionNode);

        if (container == null) {
            return false;
        }
        this.pattern = matchedPattern;

        for (Node node : container.getUnhandledNodes()) {
            if (node instanceof SectionNode sectionNode1) {
                if (node.getKey() == null) continue;

                Section parse = Section.parse(node.getKey(), "Invalid section: " + node.getKey(), sectionNode1, null);
                if (parse == null) return false;
                this.sections.add(parse);
            }
        }
        this.permission = container.getOptional("permission", String.class, false);

        SectionNode suggestions = container.getOptional("suggestions", SectionNode.class, false);
        this.suggestionsTrigger = suggestions != null ? loadCode(suggestions, "suggestions", BrigCommandSuggestEvent.class) : null;

        SectionNode trigger = container.getOptional("trigger", SectionNode.class, false);
        this.trigger = trigger != null ? loadCode(trigger, "trigger", BrigTreeTriggerEvent.class) : null;

        this.optional = parseResult.hasTag("optional");

        // Manage subcommand optional status
        if (this.optional && !this.sections.isEmpty()) {
            boolean subIsRequired = false;
            for (Section section : this.sections) {
                if (section instanceof SecSubCommand sub) {
                    if (!sub.optional) subIsRequired = true;
                }
            }
            if (subIsRequired) {
                Skript.error("An optional subcommand cannot contain a required subcommand.");
                return false;
            }
        }
        this.brigArg = (Literal<BrigArgument>) exprs[0];

        // Manage greedy strings
        if (this.brigArg.getSingle().getName().equalsIgnoreCase("greedystring") && !this.sections.isEmpty()) {
            Skript.error("A greedy string cannot have other subcommands after it.");
            return false;
        }

        this.commandName = (Literal<String>) exprs[1];

        if (matchedPattern == 1) {
            this.suggestions = LiteralUtils.defendExpression(exprs[2]);
            //this.suggestions = exprs[2];
        } else if (matchedPattern == 2) {
            this.min = (Expression<Number>) exprs[2];
            this.max = (Expression<Number>) exprs[3];
            if (this.brigArg.getSingle().getMinMax() == null) {
                Skript.error("Min/Max can only be used on number subcommands.");
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        if (!(event instanceof BrigTreeSubCommandEvent subCommandEvent)) return null;

        BrigArgument brigArg = this.brigArg.getSingle();
        String commandName = this.commandName.getSingle();
        Argument<?> command = null;

        // Create the command
        if (this.pattern > 1) {
            // Apply min/max numbers
            Number[] minMax = brigArg.getMinMax();
            if (minMax != null) {
                Number min = (this.min != null && this.min.getSingle(event) != null) ? this.min.getSingle(event) : minMax[0];
                Number max = (this.max != null && this.max.getSingle(event) != null) ? this.max.getSingle(event) : minMax[1];
                command = brigArg.getIntArgument(commandName, min, max);
            }
        } else {
            // Apply suggestions
            List<String> literals = new ArrayList<>();
            if (this.pattern == 1 && this.suggestions != null) {
                for (Object object : this.suggestions.getArray(event)) {
                    if (object instanceof String string) literals.add(string);
                    else literals.add(Classes.toString(object));
                }
            }

            if (brigArg.getArgClass() == MultiLiteralArgument.class) {
                // Literal arg requires at least one string
                if (literals.isEmpty()) literals.add(commandName);
                command = brigArg.getMultiLit(commandName, literals);
            } else {
                command = brigArg.getArgument(commandName);
                if (!literals.isEmpty()) {
                    command.includeSuggestions(ArgumentSuggestions.strings(literals));
                }
            }
        }
        if (command == null) return super.walk(event, false);

        command.setOptional(this.optional);

        // Register permission
        if (this.permission != null) {
            command.withPermission(this.permission);
        }

        // Register suggestions
        if (this.suggestionsTrigger != null) {
            // Execute trigger when suggestions are reguested (allows for dynmaic suggestions)
            command.includeSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> {
                BrigCommandSuggestEvent suggestEvent = new BrigCommandSuggestEvent();
                this.suggestionsTrigger.execute(suggestEvent);
                return suggestEvent.getTooltips().toArray(new IStringTooltip[0]);
            }));
        }

        // Register subcommands
        boolean hasSubCommand = false;
        for (Section section : this.sections) {
            hasSubCommand = true;
            Section.walk(section, new BrigTreeSubCommandEvent(command));
        }

        // Register trigger
        if (this.trigger != null) {
            command.executes(executerInfo -> {
                BrigTreeTriggerEvent brigTreeTriggerEvent = new BrigTreeTriggerEvent(executerInfo);
                // Create local variable from arg
                executerInfo.args().argsMap().forEach((argName, argObject) -> {
                    if (argObject instanceof ArrayList<?> arrayList) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            Object convert = ObjectConverter.convert(arrayList.get(i));
                            Variables.setVariable(argName + "::" + i, convert, brigTreeTriggerEvent, true);
                        }
                    } else {
                        Object convert = ObjectConverter.convert(argObject);
                        Variables.setVariable(argName, convert, brigTreeTriggerEvent, true);
                    }
                });

                this.trigger.execute(brigTreeTriggerEvent);
            });
        } else if (!hasSubCommand) {
            Skript.error("SubCommand must have at least a subcommand or a trigger.");
            return null;
        }


        // Apply trigger/subcommands to parent command
        CommandTree commandTree = subCommandEvent.getCommandTree();
        Argument<?> argument = subCommandEvent.getArgument();

        if (commandTree != null) commandTree.then(command);
        else if (argument != null) argument.then(command);

        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String opt = this.optional ? "optional " : "";
        String suggestions = this.pattern == 1 ? (" with suggestions " + this.suggestions.toString(e, d)) : "";
        String minmax = this.pattern == 2 ? (" with min " + this.min.toString(e, d) + " and max " + this.max.toString(e, d)) : "";
        return opt + this.brigArg.toString(e, d) + " arg " + this.commandName.toString(e, d) + suggestions + minmax;
    }

}
