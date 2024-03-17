package com.shanebeestudios.briggy.api.wrapper;

import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

public class ItemStackPredicate implements Predicate<ItemStack> {

    private final Predicate<ItemStack> predicate;

    public ItemStackPredicate(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return this.predicate.test(itemStack);
    }

}
