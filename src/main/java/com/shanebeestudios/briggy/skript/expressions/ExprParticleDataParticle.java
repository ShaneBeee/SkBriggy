package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("ParticleData - Particle")
@Description("Represents the particle of a ParticleData.")
@Examples({"brig command /leparticle <p:particle> <loc:location>:",
        "\ttrigger:",
        "\t\tset {_particle} to particle type of {_p}",
        "\t\tset {_data} to data type of {_p}",
        "\t\tif {_data} is set:",
        "\t\t\tmake 1 of {_particle} using {_data} at {_loc} with extra 0",
        "\t\telse:",
        "\t\t\tmake 1 of {_particle} at {_loc} with extra 0"})
@Since("INSERT VERSION")
public class ExprParticleDataParticle extends SimplePropertyExpression<ParticleData<?>, Particle> {

    static {
        register(ExprParticleDataParticle.class, Particle.class, "particle type", "particledata");
    }

    @Override
    public @Nullable Particle convert(ParticleData particleData) {
        return particleData.particle();
    }

    @Override
    public @NotNull Class<? extends Particle> getReturnType() {
        return Particle.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "particle type";
    }

}
