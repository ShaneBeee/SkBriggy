package com.shanebeestudios.briggy.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.event.BrigCommandSuggestEvent;
import dev.jorel.commandapi.BukkitStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffRegisterSuggestion extends Effect {

    static {
        Skript.registerEffect(EffRegisterSuggestion.class,
                "apply suggestion %string% with tooltip %string%",
                "apply suggestion[s] %objects%");
    }

    private int pattern;
    private Expression<String> suggestion;
    private Expression<String> tooltip;
    private Expression<?> objects;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(BrigCommandSuggestEvent.class)) {
            Skript.error("A suggestion can only be applied in a 'register argument' section.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        this.pattern = matchedPattern;
        if (matchedPattern == 0) {
            this.suggestion = (Expression<String>) exprs[0];
            this.tooltip = (Expression<String>) exprs[1];
        } else {
            this.objects = exprs[0];
        }
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void execute(Event event) {
        if (!(event instanceof BrigCommandSuggestEvent suggestEvent)) return;

        if (this.pattern == 0) {
            String suggestion = this.suggestion.getSingle(suggestEvent);
            String tooltip = this.tooltip.getSingle(suggestEvent);

            if (suggestion == null || tooltip == null) return;

            StringTooltip stringTooltip = BukkitStringTooltip.ofString(suggestion, tooltip);
            suggestEvent.addTooltip(stringTooltip);
        } else {
            for (Object object : this.objects.getArray(suggestEvent)) {
                String string;
                if (object instanceof String s) string = s;
                else string = Classes.toString(object);
                StringTooltip tooltip = BukkitStringTooltip.none(string);
                suggestEvent.addTooltip(tooltip);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "null";
    }

}
