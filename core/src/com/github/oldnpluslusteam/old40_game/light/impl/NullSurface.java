package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.oldnpluslusteam.old40_game.light.LightingSystem;
import com.github.oldnpluslusteam.old40_game.light.Source;
import com.github.oldnpluslusteam.old40_game.light.Surface;

public enum NullSurface implements Surface {
    INST;

    @Override
    public void getVertices(VertexCallback callback) {

    }

    @Override
    public void preSolve(LightingSystem system) {

    }

    @Override
    public float trace(Vector2 start, Vector2 dir, float max) {
        return max * 0.99f;
    }

    @Override
    public void receiveLight(Source source, Vector2 point1, Vector2 point2) {

    }
}
