package com.shanebeestudios.briggy.api.event;

import com.shanebeestudios.briggy.api.BrigCommand;
import dev.jorel.commandapi.BukkitStringTooltip;
import dev.jorel.commandapi.IStringTooltip;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BrigCommandSuggestEvent extends BrigCommandEvent {

    private final List<IStringTooltip> tooltips = new ArrayList<>();
    private Object[] args;

    public BrigCommandSuggestEvent(@NotNull BrigCommand brigCommand, @Nullable CommandSender sender) {
        super(brigCommand, sender);
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public List<IStringTooltip> getTooltips() {
        return tooltips;
    }

    public void addTooltip(IStringTooltip tooltip) {
        this.tooltips.add(tooltip);
    }

    public void addTooltip(String string) {
        this.tooltips.add(BukkitStringTooltip.none(string));
    }
}
