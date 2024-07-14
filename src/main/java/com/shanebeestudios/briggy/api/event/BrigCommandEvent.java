package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BrigCommandEvent extends Event {

    private final BrigCommand brigCommand;
    private CommandSender sender;
    private final World world;

    public BrigCommandEvent(@NotNull BrigCommand brigCommand, @Nullable CommandSender sender, @Nullable World world) {
        this.brigCommand = brigCommand;
        this.sender = sender;
        this.world = world;
    }

    public BrigCommandEvent(@NotNull BrigCommand brigCommand, @Nullable CommandSender sender) {
        this(brigCommand, sender, null);
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

    @NotNull
    public World getWorld() {
        if (this.world == null) {
            if (this.sender instanceof Entity entity) return entity.getWorld();
            return Bukkit.getWorlds().get(0);
        }
        return this.world;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        throw new RuntimeException("This event should not be called!");
    }

}
