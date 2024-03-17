package com.shanebeestudios.briggy.api.wrapper;

import org.bukkit.block.Block;

import java.util.function.Predicate;

public class BlockPredicate implements Predicate<Block> {

    private final Predicate<Block> predicate;

    public BlockPredicate(Predicate<Block> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Block block) {
        return this.predicate.test(block);
    }
}
