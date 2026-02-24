package com.shanebeestudios.briggy.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.shanebeestudios.briggy.api.skript.Registration;
import dev.jorel.commandapi.wrappers.ParticleData;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprParticleDataParticle extends SimplePropertyExpression<ParticleData<?>, Particle> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprParticleDataParticle.class, Particle.class,
                "particle type", "particledata")
            .name("ParticleData - Particle")
            .description("Represents the particle of a ParticleData.")
            .examples("brig command /leparticle <p:particle> <loc:location>:",
                "\ttrigger:",
                "\t\tset {_particle} to particle type of {_p}",
                "\t\tset {_data} to data type of {_p}",
                "\t\tif {_data} is set:",
                "\t\t\tmake 1 of {_particle} using {_data} at {_loc} with extra 0",
                "\t\telse:",
                "\t\t\tmake 1 of {_particle} at {_loc} with extra 0")
            .since("1.1.0")
            .register();
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
