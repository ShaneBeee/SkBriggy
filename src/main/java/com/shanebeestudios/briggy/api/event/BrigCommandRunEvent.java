package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrigCommandRunEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final BrigCommand brigCommand;
    private final CommandSender sender;
    private final Object[] args;

    public BrigCommandRunEvent(BrigCommand brigCommand, CommandSender sender, Object[] args) {
        this.brigCommand = brigCommand;
        this.sender = sender;
        this.args = args;
    }

    public BrigCommand getBrigCommand() {
        return brigCommand;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Object[] getArgs() {
        return args;
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
