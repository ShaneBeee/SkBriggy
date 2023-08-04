package com.shanebeestudios.briggy.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.BrigArgument;
import com.shanebeestudios.briggy.api.BrigCommand;
import com.shanebeestudios.briggy.api.event.BrigCommandArgumentsEvent;
import com.shanebeestudios.briggy.api.event.BrigCommandSuggestEvent;
import dev.jorel.commandapi.BukkitStringTooltip;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Name("Register Argument")
@Description({"Register an argument for a brig command.",
        "\nWithin this section you can apply suggestions with tooltips.",
        "The section itself will run when a player types a command, thus variables CAN be used and the [event-]player",
        "will be the player typing the command.",
        "\n`brigarg` represents the type of argument. See Brig Argument Type for more details.",
        "\n`string` represents the name of the argument. (Used for local variables and how it shows in game)",
        "\n`%objects`% represents suggestions for this argument. (If object is not a string, Skript will stringify it)",
        "\n`min/max` represents the min/max range of a number (long/int/float/double) argument."})
@Examples({"register string arg \"world\" using all worlds",
        "register string arg \"world\":",
        "\tapply suggestion all worlds",
        "register string arg \"homes\" using indexes of {homes::%uuid of player%::*}",
        "register string arg \"homes\" using:",
        "\tapply suggestions indexes of {homes::%uuid of player%::*}",
        "register string arg \"homes\" using:",
        "\tloop {homes::%uuid of player%::*}:",
        "\t\tapply suggestion loop-index with tooltip loop-value",
        "register string arg \"gamemode\":",
        "\tapply suggestion \"0\" with tooltip \"survival\"",
        "\tapply suggestion \"1\" with tooltip \"creative\"",
        "\tapply suggestion \"2\" with tooltip \"adventure\"",
        "\tapply suggestion \"3\" with tooltip \"spectator\""})
@Since("INSERT VERSION")
public class SecRegisterArg extends EffectSection {

    static {
        Skript.registerSection(SecRegisterArg.class,
                "register [:optional] %brigarg% arg[ument] [(named|with name)] %string%",
                "register [:optional] %brigarg% arg[ument] [(named|with name)] %string% (with suggestions|using) %objects%",
                "register [:optional] %brigarg% arg[ument] [(named|with name)] %string% with min %number% [and] [with] max %number%");
    }

    private int pattern;
    private boolean optional;
    private Expression<BrigArgument> brigArg;
    private Expression<String> argument;
    private Trigger trigger;
    private Expression<?> suggestions;
    private Expression<Number> min;
    private Expression<Number> max;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentEvent(BrigCommandArgumentsEvent.class)) {
            Skript.error("A brig arg can only be registered in a brig command 'arguments' section.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.pattern = matchedPattern;

        this.optional = parseResult.hasTag("optional");
        this.brigArg = (Expression<BrigArgument>) exprs[0];
        this.argument = (Expression<String>) exprs[1];
        if (matchedPattern == 1) {
            this.suggestions = exprs[2];
        } else if (matchedPattern == 2) {
            this.min = (Expression<Number>) exprs[2];
            this.max = (Expression<Number>) exprs[3];
        }
        if (sectionNode != null) {
            AtomicBoolean delayed = new AtomicBoolean(false);
            Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
            trigger = loadCode(sectionNode, "argument registration", afterLoading, BrigCommandSuggestEvent.class);
            if (delayed.get()) {
                Skript.error("Delays can't be within an Argument Registration section.");
                return false;
            }
        }
        if (matchedPattern == 1 && LiteralUtils.hasUnparsedLiteral(this.suggestions)) {
            return LiteralUtils.canInitSafely(this.suggestions);
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object localVars = Variables.copyLocalVariables(event);
        BrigCommandArgumentsEvent brigCommandEvent = (BrigCommandArgumentsEvent) event;
        BrigCommand brigCommand = brigCommandEvent.getBrigCommand();
        String arg = this.argument.getSingle(event);

        BrigArgument brigArg = this.brigArg.getSingle(event);
        Argument<?> argument = null;

        if (brigArg != null) {
            if (this.pattern > 1) {
                Number[] minMax = brigArg.getMinMax();
                if (minMax != null) {
                    Number min = (this.min != null && this.min.getSingle(event) != null) ? this.min.getSingle(event) : minMax[0];
                    Number max = (this.max != null && this.max.getSingle(event) != null) ? this.max.getSingle(event) : minMax[1];
                    argument = brigArg.getIntArgument(arg, min, max);
                }
            } else {
                argument = brigArg.getArgument(arg);
            }
        }
        if (argument == null) return super.walk(event, false);

        // GreedyString args have to be last
        List<Argument<?>> brigArgs = brigCommand.getArguments();
        if (brigArgs.size() > 0 && brigArgs.get(brigArgs.size() - 1) instanceof GreedyStringArgument) {
            Skript.error("You cannot register another argument after a 'greedystring' arg.");
            return super.walk(event, false);
        }

        if (trigger != null) {
            // Section so we apply stuff from section effects
            argument.includeSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> {
                BrigCommandSuggestEvent suggestEvent = new BrigCommandSuggestEvent(brigCommand, info.sender());
                Variables.setLocalVariables(suggestEvent, localVars);

                brigCommandEvent.setSender(info.sender());
                if (this.suggestions != null) {
                    for (Object object : this.suggestions.getArray(brigCommandEvent)) {
                        String string = (object instanceof String s) ? s : Classes.toString(object);
                        suggestEvent.addTooltip(string);
                    }
                }

                TriggerItem.walk(trigger, suggestEvent);
                Variables.setLocalVariables(suggestEvent, Variables.copyLocalVariables(suggestEvent));
                Variables.removeLocals(suggestEvent);
                return suggestEvent.getTooltips().toArray(new IStringTooltip[0]);
            }));
        } else if (this.pattern == 1 && this.suggestions != null) {
            // No section so we apply stuff if anything to apply
            argument.includeSuggestions(ArgumentSuggestions.stringsWithTooltips(info -> {
                List<IStringTooltip> tooltips = new ArrayList<>();

                brigCommandEvent.setSender(info.sender());
                for (Object object : this.suggestions.getArray(brigCommandEvent)) {
                    String string = (object instanceof String s) ? s : Classes.toString(object);
                    tooltips.add(BukkitStringTooltip.none(string));
                }

                return tooltips.toArray(new IStringTooltip[0]);
            }));
        }

        argument.setOptional(this.optional);
        brigCommand.addArgument(arg, argument);
        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String optional = this.optional ? "optional " : "";
        String start = "register " + optional + this.brigArg.toString(e, d) + " named " + this.argument.toString(e, d);
        return start + switch (this.pattern) {
            case 1 -> " with suggestions " + this.suggestions.toString(e, d);
            case 2 -> " with min " + this.min.toString(e, d) + " and max " + this.max.toString(e, d);
            default -> "";
        };
    }

}
