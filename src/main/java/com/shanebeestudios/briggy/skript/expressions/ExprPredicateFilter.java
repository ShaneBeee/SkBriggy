package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.wrapper.BlockPredicate;
import com.shanebeestudios.briggy.api.wrapper.ItemStackPredicate;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@Name("Predicate - Filter")
@Description("Represents a item/block predicate for filtering objects.")
@Examples({"# /destroy minecraft:short_grass 10",
        "# /destroy #minecraft:stairs 50",
        "brig command /destroy <b:blockpredicate> [<rad:int>]:",
        "\ttrigger:",
        "\t\tif {_rad} is not set:",
        "\t\t\tset {_rad} to 5",
        "\t\tloop ((blocks in radius {_rad} around target block) that match block predicate {_b}):",
        "\t\t\tbreak loop-block",
        "",
        "# /remove @a #minecraft:swords",
        "# /remove @a #minecraft:swords{Damage:0}",
        "brig command /remove <players> <i:itempredicate>:",
        "\ttrigger:",
        "\t\tloop {_players::*}:",
        "\t\t\tremove ((items in loop-value's inventory) that match item predicate {_i}) from inventory of loop-value"})
@Since("INSERT VERSION")
public class ExprPredicateFilter extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprPredicateFilter.class, Object.class, ExpressionType.COMBINED,
                "%itemstacks% (that|which) match item[[ ]stack] predicate %predicate%",
                "%blocks% (that|which) match block predicate %predicate%");
    }

    private int pattern;
    private Expression<ItemStack> itemStacks;
    private Expression<Block> blocks;
    private Expression<Predicate<?>> predicate;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.pattern = matchedPattern;
        if (matchedPattern == 0) {
            this.itemStacks = (Expression<ItemStack>) exprs[0];
        } else {
            this.blocks = (Expression<Block>) exprs[0];
        }
        this.predicate = (Expression<Predicate<?>>) exprs[1];
        return true;
    }

    @SuppressWarnings({"NullableProblems"})
    @Override
    protected @Nullable Object[] get(Event event) {
        if (this.predicate == null) return null;

        Predicate<?> predicate = this.predicate.getSingle(event);
        if (predicate == null) return null;

        if (this.pattern == 0 && predicate instanceof ItemStackPredicate itemStackPredicate) {
            return this.itemStacks.stream(event).filter(itemStackPredicate).toArray();
        } else if (this.pattern == 1 && predicate instanceof BlockPredicate blockPredicate) {
            return this.blocks.stream(event).filter(blockPredicate).toArray();
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return this.pattern == 0 ? ItemStack.class : Block.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String message = "%s that match %s predicate %s";
        String object = this.pattern == 0 ? this.itemStacks.toString(e, d) : this.blocks.toString(e, d);
        String type = this.pattern == 0 ? "item" : "block";
        return String.format(message, object, type, this.predicate.toString(e, d));
    }

}
