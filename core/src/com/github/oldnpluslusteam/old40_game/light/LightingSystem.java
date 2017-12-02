package com.github.oldnpluslusteam.old40_game.light;

import com.github.alexeybond.partly_solid_bicycle.game.GameSystem;

public interface LightingSystem extends GameSystem {
    void addSurface(Surface surface);

    void removeSurface(Surface surface);

    Source addIterationSource(Surface owner);
}
