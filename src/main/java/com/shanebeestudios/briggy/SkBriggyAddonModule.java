package com.shanebeestudios.briggy;

import com.shanebeestudios.briggy.api.skript.Registration;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;

public class SkBriggyAddonModule implements AddonModule {

    private final Registration registration;

    public SkBriggyAddonModule(Registration registration) {
        this.registration = registration;
    }

    @Override
    public void init(SkriptAddon addon) {
    }

    @Override
    public void load(SkriptAddon addon) {

    }

    @Override
    public String name() {
        return "SkBriggy";
    }

}
