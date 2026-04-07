package com.shanebeestudios.briggy.skript;

import com.shanebeestudios.briggy.api.skript.Registration;
import com.shanebeestudios.briggy.skript.conditions.CondPredicateMatch;
import com.shanebeestudios.briggy.skript.effects.EffApplySuggestion;
import com.shanebeestudios.briggy.skript.expressions.ExprArgSuggestions;
import com.shanebeestudios.briggy.skript.expressions.ExprBrigArg;
import com.shanebeestudios.briggy.skript.expressions.ExprIntegerRange;
import com.shanebeestudios.briggy.skript.expressions.ExprParticleDataDataType;
import com.shanebeestudios.briggy.skript.expressions.ExprParticleDataParticle;
import com.shanebeestudios.briggy.skript.expressions.ExprPredicateFilter;
import com.shanebeestudios.briggy.skript.sections.SecRegisterArg;
import com.shanebeestudios.briggy.skript.sections.SecSubCommand;
import com.shanebeestudios.briggy.skript.structures.StructBrigCommand;
import com.shanebeestudios.briggy.skript.structures.StructBrigCommandTree;
import com.shanebeestudios.briggy.skript.testing.ExprRunCommand;
import com.shanebeestudios.briggy.skript.types.Types;

public class ElementRegistration {

    public static void register(Registration registration) {
        // CONDITIONS
        CondPredicateMatch.register(registration);

        // EFFECTS
        EffApplySuggestion.register(registration);

        // EXPRESSIONS
        ExprArgSuggestions.register(registration);
        ExprBrigArg.register(registration);
        ExprIntegerRange.register(registration);
        ExprParticleDataDataType.register(registration);
        ExprParticleDataParticle.register(registration);
        ExprPredicateFilter.register(registration);

        // SECTIONS
        SecRegisterArg.register(registration);
        SecSubCommand.register(registration);

        // STRUCTURES
        StructBrigCommand.register(registration);
        StructBrigCommandTree.register(registration);

        // TESTING
        ExprRunCommand.register(registration);

        // TYPES
        Types.register(registration);

        registration.finalizeRegistration();
    }

}
