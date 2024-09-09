package com.shanebeestudios.briggy.api.event;

import dev.jorel.commandapi.BukkitStringTooltip;
import dev.jorel.commandapi.IStringTooltip;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BrigCommandSuggestEvent extends Event {

    private final List<IStringTooltip> suggestions = new ArrayList<>();
    private Object[] args;
    private CommandSender commandSender;

    public BrigCommandSuggestEvent() {
    }

    /**
     * Get the arguments used for brig-arg-x
     *
     * @return Arguments to use for brig-arg-x
     */
    public Object[] getBrigArgs() {
        return args;
    }

    /**
     * Set the arguments used for brig-arg-x
     *
     * @param args Arguments to add
     */
    public void setBrigArgs(Object[] args) {
        this.args = args;
    }

    public CommandSender getCommandSender() {
        return this.commandSender;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    /**
     * Get suggestions to apply to a command
     *
     * @return Suggestions to apply to command
     */
    public List<IStringTooltip> getSuggestions() {
        return suggestions;
    }

    /**
     * Add a suggestion to apply to a command
     *
     * @param suggestion Suggestion to apply
     */
    public void addSuggestion(IStringTooltip suggestion) {
        this.suggestions.add(suggestion);
    }

    /**
     * Add a suggestion to apply to a command
     *
     * @param suggestion Suggestion to apply
     */
    public void addSuggestion(String suggestion) {
        this.suggestions.add(BukkitStringTooltip.none(suggestion));
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("BrigCommandSuggestEvent should not be called");
    }

}
