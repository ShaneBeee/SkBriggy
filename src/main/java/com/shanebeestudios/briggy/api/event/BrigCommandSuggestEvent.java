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

    private final List<IStringTooltip> tooltips = new ArrayList<>();
    private Object[] args;
    private CommandSender commandSender;

    public BrigCommandSuggestEvent() {
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public CommandSender getCommandSender() {
        return this.commandSender;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public List<IStringTooltip> getTooltips() {
        return tooltips;
    }

    public void addTooltip(IStringTooltip tooltip) {
        this.tooltips.add(tooltip);
    }

    public void addTooltip(String string) {
        this.tooltips.add(BukkitStringTooltip.none(string));
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("BrigCommandSuggestEvent should not be called");
    }

}
