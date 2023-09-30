package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ParticleData - Data Type")
@Description("Represents the data type of a ParticleData.")
@Examples({"brig command /leparticle <p:particle> <loc:location>:",
        "\ttrigger:",
        "\t\tset {_particle} to particle type of {_p}",
        "\t\tset {_data} to data type of {_p}",
        "\t\tif {_data} is set:",
        "\t\t\tmake 1 of {_particle} using {_data} at {_loc} with extra 0",
        "\t\telse:",
        "\t\t\tmake 1 of {_particle} at {_loc} with extra 0"})
@Since("1.1.0")
public class ExprParticleDataDataType extends SimplePropertyExpression<ParticleData<?>,Object> {

    static {
        register(ExprParticleDataDataType.class, Object.class, "data type", "particledata");
    }

    @Override
    public @Nullable Object convert(ParticleData<?> particleData) {
        return particleData.data();
    }

    @Override
    public @NotNull Class<Object> getReturnType() {
        return Object.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "data type";
    }

}
