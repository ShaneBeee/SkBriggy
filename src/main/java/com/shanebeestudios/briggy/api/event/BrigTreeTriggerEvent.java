package com.shanebeestudios.briggy.api.event;

import dev.jorel.commandapi.commandsenders.BukkitCommandSender;
import dev.jorel.commandapi.executors.ExecutionInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrigTreeTriggerEvent extends Event {

    private final ExecutionInfo<CommandSender, BukkitCommandSender<? extends CommandSender>> executionInfo;

    public BrigTreeTriggerEvent(ExecutionInfo<CommandSender, BukkitCommandSender<? extends CommandSender>> executionInfo) {
        this.executionInfo = executionInfo;
    }

    public CommandSender getSender() {
        return this.executionInfo.sender();
    }

    public Object[] getArgs() {
        return this.executionInfo.args().args();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("BrigTreeTriggerEvent should not be called");
    }

}
