package com.shanebeestudios.briggy.api;

import ch.njol.skript.lang.Trigger;
import com.shanebeestudios.briggy.api.event.BrigCommandRunEvent;
import com.shanebeestudios.briggy.api.util.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;

import java.util.LinkedHashMap;
import java.util.Map;

public class BrigCommand {

    private final String name;
    private String permission = null;
    private final Map<String, Argument<?>> args = new LinkedHashMap<>();
    private Trigger trigger;

    public BrigCommand(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void addParam(String name, Argument<?> arg) {
        args.put(name, arg);
    }

    public void addExecution(Trigger trigger) {
        this.trigger = trigger;
    }

    public void build() {
        CommandAPICommand commandAPICommand = new CommandAPICommand(this.name);
        if (permission != null) {
            commandAPICommand.withPermission(this.permission);
        }
        commandAPICommand.withArguments(args.values().toArray(new Argument[0]));
        commandAPICommand.executes((commandSender, objects) -> {
            BrigCommandRunEvent brigCommandRunEvent = new BrigCommandRunEvent(this, commandSender, objects);
            Trigger.walk(trigger, brigCommandRunEvent);
        });

        commandAPICommand.register();
        Utils.reloadCommands();
    }

}
