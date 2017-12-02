package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.drawing.DrawingContext;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.oldnpluslusteam.old40_game.light.LightingSystem;
import com.github.oldnpluslusteam.old40_game.light.Source;

public class LightLineSurfaceComponent extends LineSurfaceComponent {
    private final Vector2 focus, absFocus = new Vector2();
    private final float maxdst;

    public LightLineSurfaceComponent(Vector2 point1, Vector2 point2, Vector2 focus, float maxdst) {
        super(point1, point2);
        this.focus = focus;
        this.maxdst = maxdst;
    }

    @Override
    public void preSolve(LightingSystem system) {
        super.preSolve(system);

        absFocus.set(focus).rotate(rotation.get()).add(position.ref());

        Source source = system.addIterationSource(this);

        source.point1.set(abs1);
        source.point2.set(abs2);
        source.focus.set(absFocus);
        source.maxDist = maxdst;
    }

    @Override
    public void draw(DrawingContext context) {
        super.draw(context);

        context.state().beginLines().circle(focus.x, focus.y, 1);
    }

    public static class Decl implements ComponentDeclaration {
        public float[] points;
        public float[] focus;
        public float maxDst = 200;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new LightLineSurfaceComponent(
                    new Vector2(points[0], points[1]),
                    new Vector2(points[2], points[3]),
                    new Vector2(focus[0], focus[1]),
                    maxDst);
        }
    }
}
