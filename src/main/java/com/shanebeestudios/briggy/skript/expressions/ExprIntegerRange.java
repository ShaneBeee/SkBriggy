package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import dev.jorel.commandapi.wrappers.IntegerRange;
import org.jetbrains.annotations.Nullable;

@Name("IntegerRange - Low/High")
@Description("Get the low/high point of an integer range.")
@Since("INSERT VERSION")
public class ExprIntegerRange extends SimplePropertyExpression<IntegerRange, Number> {

    static {
        register(ExprIntegerRange.class, Number.class, "(low|:high) range", "integer range");
    }

    private boolean high;

    @SuppressWarnings("NullableProblems")
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
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected String getPropertyName() {
        return (this.high ? "high" : "low") + " range";
    }

}
