package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.drawing.DrawingContext;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.RenderSystem;
import com.github.alexeybond.partly_solid_bicycle.game.systems.render.interfaces.RenderComponent;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.FloatProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.Vec2Property;
import com.github.oldnpluslusteam.old40_game.light.LightingSystem;
import com.github.oldnpluslusteam.old40_game.light.Source;
import com.github.oldnpluslusteam.old40_game.light.Surface;

public class LineSurfaceComponent implements Surface, Component, RenderComponent {
    private Vec2Property position;
    private FloatProperty rotation;

    protected Vector2 point1, point2, abs1 = new Vector2(), abs2 = new Vector2();
    private Vector2 absfix0 = new Vector2(), absfix1 = new Vector2(), absfix2 = new Vector2();

    private static final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2();

    public LineSurfaceComponent(Vector2 point1, Vector2 point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public void onConnect(Entity entity) {
        entity.game().systems().<LightingSystem>get("light").addSurface(this);
        entity.game().systems().<RenderSystem>get("render").addToPass("game-debug", this);

        position = entity.events().event("position", Vec2Property.make());
        rotation = entity.events().event("rotation", FloatProperty.make());
    }

    @Override
    public void onDisconnect(Entity entity) {
        entity.game().systems().<LightingSystem>get("light").removeSurface(this);
        entity.game().systems().<RenderSystem>get("render").removeFromPass("game-debug", this);
    }

    @Override
    public void getVertices(VertexCallback callback) {
        callback.call(this, abs1);
        callback.call(this, abs2);
    }

    @Override
    public void preSolve(LightingSystem system) {
        abs1.set(point1).rotate(rotation.get()).add(position.ref());
        abs2.set(point2).rotate(rotation.get()).add(position.ref());

        absfix0.set(abs1).sub(abs2).scl(0.1f);
        absfix1.set(abs1).add(absfix0);
        absfix2.set(abs2).sub(absfix0);
    }

    @Override
    public float trace(Vector2 start, Vector2 dir, float max) {
        tmp1.set(dir).setLength(max).add(start);
        if (Intersector.intersectSegments(absfix1, absfix2, start, tmp1, tmp2)) {
            return tmp2.sub(start).len();
        }

        return -1;
    }

    @Override
    public void receiveLight(Source source, Vector2 point1, Vector2 point2) {

    }

    @Override
    public void draw(DrawingContext context) {
        ShapeRenderer shaper = context.state().beginLines();
        shaper.setColor(1,1,1,1);
        shaper.line(abs1, abs2);
    }

    public static class Decl implements ComponentDeclaration {
        public float[] points;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new LineSurfaceComponent(
                    new Vector2(points[0], points[1]),
                    new Vector2(points[2], points[3]));
        }
    }
}
