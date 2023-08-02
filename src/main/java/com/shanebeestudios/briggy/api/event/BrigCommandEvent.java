package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BrigCommandEvent extends Event {

    private final BrigCommand brigCommand;
    private CommandSender sender;

    public BrigCommandEvent(@NotNull BrigCommand brigCommand, @Nullable CommandSender sender) {
        this.brigCommand = brigCommand;
        this.sender = sender;
    }

    public BrigCommand getBrigCommand() {
        return brigCommand;
    }

    @Nullable
    public CommandSender getSender() {
        return sender;
    }

    public void setSender(@Nullable CommandSender sender) {
        this.sender = sender;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        throw new RuntimeException("This event should not be called!");
    }

}
