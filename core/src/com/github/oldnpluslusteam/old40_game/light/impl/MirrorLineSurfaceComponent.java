package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.oldnpluslusteam.old40_game.light.LightingSystem;
import com.github.oldnpluslusteam.old40_game.light.Source;

public class MirrorLineSurfaceComponent extends LineSurfaceComponent {
    private static final Vector2 tmp = new Vector2();
    public MirrorLineSurfaceComponent(Vector2 point1, Vector2 point2) {
        super(point1, point2);
    }

    @Override
    public void receiveLight(Source source, Vector2 point1, Vector2 point2, LightingSystem system) {
        Source reflection = system.addIterationSource(this);

        tmp.set(point1).sub(point2).rotate90(1).nor();
        reflection.focus.set(source.focus).sub(point1);

        reflection.focus.sub(tmp.scl(2f * tmp.dot(reflection.focus))).add(point1);

        reflection.point1.set(point2);
        reflection.point2.set(point1);

        reflection.surface = this;
        reflection.maxDist = source.maxDist;
    }

    public static class Decl implements ComponentDeclaration {
        public float[] points;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new MirrorLineSurfaceComponent(
                    new Vector2(points[0], points[1]),
                    new Vector2(points[2], points[3]));
        }
    }
}
