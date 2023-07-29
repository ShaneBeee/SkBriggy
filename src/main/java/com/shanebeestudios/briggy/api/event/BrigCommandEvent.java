package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class BrigCommandEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final BrigCommand brigCommand;

    public BrigCommandEvent(BrigCommand brigCommand) {
        this.brigCommand = brigCommand;
    }

    public BrigCommand getBrigCommand() {
        return brigCommand;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
