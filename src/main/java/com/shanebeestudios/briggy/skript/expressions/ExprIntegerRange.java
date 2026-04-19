package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.jorel.commandapi.wrappers.IntegerRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprIntegerRange extends SimplePropertyExpression<IntegerRange, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprIntegerRange.class, Number.class,
                "(low|:high) range", "intrange")
            .name("IntegerRange - Low/High")
            .description("Get the low/high point of an integer range.")
            .examples("set {_low} to low range of {_intrange}",
                "set {_high} to high range of {_intrange}",
                "set {_ints::} to integers between {_low} and {_high}")
            .since("1.0.0")
            .register();
    }

    private boolean high;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.high = parseResult.hasTag("high");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable Number convert(IntegerRange integerRange) {
        return this.high ? integerRange.getUpperBound() : integerRange.getLowerBound();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return (this.high ? "high" : "low") + " range";
    }

}
