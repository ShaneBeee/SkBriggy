package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class BrigCommandTriggerEvent extends BrigCommandEvent {

    private final Object[] args;

    public BrigCommandTriggerEvent(BrigCommand brigCommand, CommandSender sender, Object[] args, World world) {
        super(brigCommand, sender, world);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

}
