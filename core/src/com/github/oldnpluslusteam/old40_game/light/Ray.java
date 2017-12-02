package com.github.oldnpluslusteam.old40_game.light;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ray {
    private static final Vector2 tmp1 = new Vector2();

    public final Vector2[] points = new Vector2[] {
            new Vector2(), new Vector2(), new Vector2(), new Vector2()};

    public boolean isTooSmall() {
        return (tmp1.set(points[0]).sub(points[1]).len2() + tmp1.set(points[2]).sub(points[3]).len2()) < 0.1;
    }
}
