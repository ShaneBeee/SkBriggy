package com.shanebeestudios.briggy.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

@Name("CommandTree - SubCommand")
public class SecRegisterSubCommand extends Section {

    private static final EntryValidator.EntryValidatorBuilder VALIDATOR = EntryValidator.builder();

    static {
        //noinspection DataFlowIssue
        VALIDATOR
            .addEntry("permission", null, true)
            .addSection("suggestions", true)
            .addSection("trigger", true)
            .unexpectedNodeTester(node -> {
                if (node instanceof SectionNode sectionNode) {
                    String key = sectionNode.getKey();
                    return key == null || !key.contains("register") || !key.contains("sub") || !key.contains("command");
                }
                return true;
            }).build();
        Skript.registerSection(SecRegisterSubCommand.class, "register [:optional] %*brigarg% sub[ ]command [(named|with name)] %*string%");
    }

    private boolean optional;
    private String permission;
    private Literal<BrigArgument> brigArg;
    private Literal<String> commandName;
    private final List<Section> sections = new ArrayList<>();
    private Trigger suggestions;
    private Trigger trigger;

    @SuppressWarnings({"NullableProblems", "unchecked", "DataFlowIssue"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        EntryContainer container = VALIDATOR.build().validate(sectionNode);

        if (container == null) {
            return false;
        }

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
        this.suggestions = suggestions != null ? loadCode(suggestions, "suggestions", BrigCommandSuggestEvent.class) : null;

        SectionNode trigger = container.getOptional("trigger", SectionNode.class, false);
        this.trigger = trigger != null ? loadCode(trigger, "trigger", BrigTreeTriggerEvent.class) : null;

        this.optional = parseResult.hasTag("optional");
        this.brigArg = (Literal<BrigArgument>) exprs[0];
        this.commandName = (Literal<String>) exprs[1];
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        TriggerItem next = getNext();
        if (!(event instanceof BrigTreeSubCommandEvent subCommandEvent)) return null;

        BrigArgument brigArg = this.brigArg.getSingle();
        String commandName = this.commandName.getSingle();
        Argument<?> command = brigArg.getArgument(commandName);
        command.setOptional(this.optional);

        // Register permission
        if (this.permission != null) {
            command.withPermission(this.permission);
        }

        // Register suggestions
        if (this.suggestions != null) {
            BrigCommandSuggestEvent suggestEvent = new BrigCommandSuggestEvent();
            this.suggestions.execute(suggestEvent);
            command.includeSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> suggestEvent.getTooltips().toArray(new IStringTooltip[0])));
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

        return next;
    }

    @Override
    public @NotNull String toString(Event e, boolean d) {
        String opt = this.optional ? "optional " : "";
        return "register " + opt + this.brigArg.toString(e, d) + " subcommand " + this.commandName.toString(e, d);
    }

}
