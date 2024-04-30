package com.shanebeestudios.briggy.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;
import com.shanebeestudios.briggy.api.wrapper.BlockPredicate;
import com.shanebeestudios.briggy.api.wrapper.ItemStackPredicate;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@Name("Predicate - Matches")
@Description("Check if a block/item matches a predicate.")
@Examples({"# /remove @a #wool",
        "# /remove @a #minecraft:swords{Damage:0}",
        "brig command /remove <players> <i:itempredicate>:",
        "\ttrigger:",
        "\t\tloop {_players::*}:",
        "\t\t\tloop items in inventory of loop-value:",
        "\t\t\t\tif loop-item matches item predicate {_i}:",
        "\t\t\t\t\tremove loop-item from inventory of loop-value-1",
        "",
        "# /replace minecraft:short_grass minecraft:air 20",
        "# /replace #minecraft:dirt minecraft:sand 30",
        "# /replace #minecraft:logs minecraft:stone 50",
        "brig command /replace <b:blockpredicate> <block> [<rad:int>]:",
        "\ttrigger:",
        "\t\tif {_rad} is not set:",
        "\t\t\tset {_rad} to 5",
        "\t\tloop blocks in radius {_rad} around target block of player:",
        "\t\t\tif loop-block matches block predicate {_b}:",
        "\t\t\t\tset loop-block to {_block}"})
@Since("1.3.0")
public class CondPredicateMatch extends Condition {

    static {
        Skript.registerCondition(CondPredicateMatch.class,
                "%itemstacks/blocks% match[es] (item|block) predicate %predicate%",
                "%itemstacks/blocks% (doesn't|don't|do not) match[es] (item|block) predicate %predicate%");
    }

    private Expression<?> objects;
    private Expression<Predicate<?>> predicate;

    @SuppressWarnings({"NullableProblems", "unchecked"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        this.objects = exprs[0];
        this.predicate = (Expression<Predicate<?>>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean check(Event event) {
        if (this.predicate == null) return false;
        Predicate<?> predicate = this.predicate.getSingle(event);
        return this.objects.check(event, (Checker<Object>) object -> {
            if (object instanceof ItemStack itemStack && predicate instanceof ItemStackPredicate itemStackPredicate) {
                return itemStackPredicate.test(itemStack);
            } else if (object instanceof Block block && predicate instanceof BlockPredicate blockPredicate) {
                return blockPredicate.test(block);
            }
            return false;
        }, isNegated());
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        String match = isNegated() ? (this.objects.isSingle() ? "doesn't match" : "don't match") : (this.objects.isSingle() ? "matches" : "match");
        return String.format("%s %s predicate %s", this.objects.toString(e, d), match, this.predicate.toString(e, d));
    }

}
