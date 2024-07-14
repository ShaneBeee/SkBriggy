package com.shanebeestudios.briggy.api.event;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BrigTreeSubCommandEvent extends Event {

    private CommandTree commandTree;
    private Argument<?> argument;

    public BrigTreeSubCommandEvent(CommandTree commandTree) {
        this.commandTree = commandTree;
    }

    public BrigTreeSubCommandEvent(Argument<?> arg) {
        this.argument = arg;
    }

    public CommandTree getCommandTree() {
        return this.commandTree;
    }

    public Argument<?> getArgument() {
        return this.argument;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        throw new IllegalStateException("BrigTreeThenEvent should not be called");
    }

}
