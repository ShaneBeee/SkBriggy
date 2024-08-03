package com.shanebeestudios.briggy.api.event;

import dev.jorel.commandapi.BukkitStringTooltip;
import dev.jorel.commandapi.IStringTooltip;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BrigCommandSuggestEvent extends Event {

    private final List<IStringTooltip> tooltips = new ArrayList<>();
    private Object[] args;

    public BrigCommandSuggestEvent() {
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
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
