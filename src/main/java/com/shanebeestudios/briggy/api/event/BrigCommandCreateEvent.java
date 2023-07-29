package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrigCommandCreateEvent extends BrigCommandEvent {

    private final static HandlerList handlers = new HandlerList();


    public BrigCommandCreateEvent(BrigCommand brigCommand) {
        super(brigCommand);
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
