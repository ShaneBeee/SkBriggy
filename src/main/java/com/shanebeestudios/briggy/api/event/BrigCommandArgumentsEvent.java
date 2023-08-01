package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrigCommandArgumentsEvent extends BrigCommandEvent {

    public BrigCommandArgumentsEvent(BrigCommand brigCommand) {
        super(brigCommand);
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        throw new RuntimeException("This event should not be called!");
    }

}
