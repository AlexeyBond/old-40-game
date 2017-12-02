package com.github.oldnpluslusteam.old40_game.modules;

import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoCStrategy;
import com.github.alexeybond.partly_solid_bicycle.ioc.modules.Module;
import com.github.oldnpluslusteam.old40_game.screens.GameScreen;

public class StartupScreenModule implements Module {
    @Override
    public void init() {
        IoC.register("initial screen", new IoCStrategy() {
            @Override
            public Object resolve(Object... args) {
                return new GameScreen(GameScreen.INITIAL_LEVEL);
            }
        });
    }

    @Override
    public void shutdown() {

    }
}
