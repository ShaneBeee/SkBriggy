package com.shanebeestudios.briggy.api;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.variables.Variables;
import com.shanebeestudios.briggy.api.event.BrigCommandTriggerEvent;
import com.shanebeestudios.briggy.api.util.ObjectConverter;
import com.shanebeestudios.briggy.api.util.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BrigCommand {

    private final String name;
    private String permission = null;
    private String description = null;
    private String usage = null;
    private List<String> aliases = null;
    private final Map<String, Argument<?>> args = new LinkedHashMap<>();
    private Trigger trigger;

    public BrigCommand(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public void addArgument(String name, Argument<?> arg) {
        args.put(name, arg);
    }

    public List<Argument<?>> getArguments() {
        return new ArrayList<>(this.args.values());
    }

    public Map<String, Argument<?>> getArgumentMap() {
        return this.args;
    }

    public void addExecution(Trigger trigger) {
        this.trigger = trigger;
    }

    public void build() {
        CommandAPICommand commandAPICommand = new CommandAPICommand(this.name);
        if (this.permission != null) commandAPICommand.withPermission(this.permission);
        if (this.aliases != null) commandAPICommand.setAliases(this.aliases.toArray(new String[0]));
        if (this.usage != null) {
            if (this.usage.contains(",")) commandAPICommand.withUsage(this.usage.split(","));
            else commandAPICommand.withUsage(this.usage);
        }

        commandAPICommand.withArguments(args.values().toArray(new Argument[0]));
        commandAPICommand.withShortDescription(this.description);
        commandAPICommand.executes((commandSender, arguments) -> {
            BrigCommandTriggerEvent brigCommandRunEvent = new BrigCommandTriggerEvent(this, commandSender, arguments.args());

            // Register local variables for arg names
            arguments.argsMap().forEach((argName, argObject) -> {
                if (argObject instanceof ArrayList<?> arrayList) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        Object convert = ObjectConverter.convert(arrayList.get(i));
                        Variables.setVariable(argName + "::" + i, convert, brigCommandRunEvent, true);
                    }
                } else {
                    Object convert = ObjectConverter.convert(argObject);
                    Variables.setVariable(argName, convert, brigCommandRunEvent, true);
                }
            });

            trigger.execute(brigCommandRunEvent);
        });

        commandAPICommand.register();
        Utils.reloadCommands();
    }

}
