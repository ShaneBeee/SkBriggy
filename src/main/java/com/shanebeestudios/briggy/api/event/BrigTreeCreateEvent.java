package com.shanebeestudios.briggy.api.event;

import dev.jorel.commandapi.CommandTree;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrigTreeCreateEvent extends Event {

    private final CommandTree commandTree;

    public BrigTreeCreateEvent(CommandTree commandTree) {
        this.commandTree = commandTree;
    }

    public CommandTree getCommandTree() {
        return this.commandTree;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("BrigTreeCreateEvent should not be called");
    }

}
