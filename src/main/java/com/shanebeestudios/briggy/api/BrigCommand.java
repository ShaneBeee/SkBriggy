package com.shanebeestudios.briggy.api;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.variables.Variables;
import com.shanebeestudios.briggy.api.event.BrigCommandTriggerEvent;
import com.shanebeestudios.briggy.api.util.ObjectConverter;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.executors.ExecutorType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BrigCommand {

    private List<ExecutorType> executorType = List.of(ExecutorType.ALL);
    private final String namespace;
    private final String name;
    private String permission = null;
    private String description = null;
    private String usage = null;
    private List<String> aliases = null;
    private final Map<String, Argument<?>> args = new LinkedHashMap<>();
    private Trigger trigger;

    public BrigCommand(String name) {
        if (name.contains(":")) {
            String[] split = name.split(":");
            this.namespace = split[0];
            this.name = split[1];
        } else {
            this.namespace = "minecraft";
            this.name = name;
        }
    }

    public void setExecutorType(List<ExecutorType> executorType) {
        this.executorType = executorType;
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
        commandAPICommand.executes(info -> {
            CommandArguments arguments = info.args();
            CommandSender sender = info.sender();
            World world = sender instanceof Entity entity ? entity.getWorld() : null;
            BrigCommandTriggerEvent brigCommandRunEvent = new BrigCommandTriggerEvent(this, sender, arguments.args(), world);

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
        }, this.executorType.toArray(new ExecutorType[0]));

        commandAPICommand.register(this.namespace);
    }

}
