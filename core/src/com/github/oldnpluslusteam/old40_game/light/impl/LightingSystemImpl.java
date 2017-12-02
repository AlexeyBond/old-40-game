package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.github.alexeybond.partly_solid_bicycle.drawing.Drawable;
import com.github.alexeybond.partly_solid_bicycle.drawing.DrawingContext;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.GameSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.RenderSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.interfaces.RenderComponent;
import com.github.oldnpluslusteam.old40_game.light.LightingSystem;
import com.github.oldnpluslusteam.old40_game.light.Ray;
import com.github.oldnpluslusteam.old40_game.light.Source;
import com.github.oldnpluslusteam.old40_game.light.Surface;

public class LightingSystemImpl implements LightingSystem, GameSystem, Drawable {
    private final Array<Surface> surfaces = new Array<Surface>(false, 64);
    private final Queue<Source> iterationSources = new Queue<Source>(64);
    private final Array<Ray> rays = new Array<Ray>(64);

    private RenderSystem renderSystem;
    private final RenderComponent renderComponent = new RenderComponent() {
        @Override
        public void draw(DrawingContext context) {
            LightingSystemImpl.this.draw(context);
        }

        @Override public void onConnect(Entity entity) { }
        @Override public void onDisconnect(Entity entity) { }
    };

    private int maxIterations = 10000;

    @Override
    public void update(float deltaTime) {
        iterationSources.clear();
        rays.clear();

        for (int i = 0; i < surfaces.size; i++) surfaces.get(i).preSolve(this);

        int nIterations = 0;

        while (iterationSources.size != 0 && (nIterations++) < maxIterations) {
            Source source = iterationSources.removeFirst();

            ///////
            Ray ray = new Ray();
            ray.points[0].set(source.point1);
            ray.points[1].set(source.point2);
            ray.points[2].set(source.point2).sub(source.focus).setLength(source.maxDist).add(source.point2);
            ray.points[3].set(source.point1).sub(source.focus).setLength(source.maxDist).add(source.point1);

            rays.add(ray);
        }
    }

    @Override
    public void onConnect(Game game) {
        renderSystem = game.systems().get("render");
        renderSystem.addToPass("game-light", renderComponent);
    }

    @Override
    public void onDisconnect(Game game) {
        renderSystem.removeFromPass("game-light", renderComponent);
    }

    @Override
    public void addSurface(Surface surface) {
        surfaces.add(surface);
    }

    @Override
    public void removeSurface(Surface surface) {
        surfaces.add(surface);
    }

    @Override
    public Source addIterationSource(Surface owner) {
        Source source = new Source();
        source.surface = owner;
        iterationSources.addLast(source);
        return source;
    }

    @Override
    public void draw(DrawingContext context) {
        ShapeRenderer shaper = context.state().beginFilled();

        Color lightColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);

        for (int i = 0; i < rays.size; i++) {
            Ray ray = rays.get(i);

            shaper.triangle(
                    ray.points[0].x, ray.points[0].y,
                    ray.points[1].x, ray.points[1].y,
                    ray.points[2].x, ray.points[2].y,
                    lightColor, lightColor, lightColor
            );
            shaper.triangle(
                    ray.points[0].x, ray.points[0].y,
                    ray.points[2].x, ray.points[2].y,
                    ray.points[3].x, ray.points[3].y,
                    lightColor, lightColor, lightColor
            );
        }
    }
}
