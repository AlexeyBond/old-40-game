package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        Vector2 tmp1 = new Vector2(), tmp2 = new Vector2();
        iterationSources.clear();
        rays.clear();

        for (int i = 0; i < surfaces.size; i++) surfaces.get(i).preSolve(this);

        int nIterations = 0;

        while (iterationSources.size != 0 && (nIterations++) < maxIterations) {
            final Source source = iterationSources.removeFirst();

            traceVertices.clear();
            prepareClipPlanes(source);

            for (int i = 0; i < surfaces.size; i++) {
                if (surfaces.get(i) == source.surface) continue;
                surfaces.get(i).getVertices(clippingCallback);
            }

            // now traceVertices contains all the vertices in clip area

            trace(source.focus, tmp1.set(source.point1).sub(source.focus),
                    source.minDistanceDirection(tmp1), source.maxDist, source.surface);
            traceVertices.add(new Vertex(new Vector2(tr_point), tr_surface));
            trace(source.focus, tmp1.set(source.point2).sub(source.focus),
                    source.minDistanceDirection(tmp1), source.maxDist, source.surface);
            traceVertices.add(new Vertex(new Vector2(tr_point), tr_surface));

            // Sort vertices by a angle to one of ray edges
            Collections.sort(traceVertices, new Comparator<Vertex>() {
                private final Vector2 va = new Vector2(), vb = new Vector2();

                @Override
                public int compare(Vertex a, Vertex b) {
                    vb.set(source.point2).sub(source.focus);

                    va.set(a.point).sub(source.focus);
                    float aa = va.angle(vb);

                    va.set(b.point).sub(source.focus);
                    float ab = va.angle(vb);

                    return Float.compare(aa, ab);
                }
            });

//            for (int i = 0; i < traceVertices.size() - 1; i++) {
//                Vertex a = traceVertices.get(i), b = traceVertices.get(i + 1);
//                Ray ray = new Ray();
//                source.setRayToPoints(ray, a.point, b.point);
//                rays.add(ray);
//            }
            Vertex pv = traceVertices.get(0);

            for (int i = 1; i < traceVertices.size(); i++) {
                Vertex cv = traceVertices.get(i);

                Ray ray = new Ray();

                Surface surface;

                if (pv.surface == cv.surface) {
                    source.setRayToPoints(ray, pv.point, cv.point);
                    surface = pv.surface;
                } else {
                    float d = cv.surface.trace(source.focus, tmp1.set(pv.point).sub(source.focus), source.maxDist);

                    if (d < source.maxDist && d > 0) {
                        tmp1.setLength(d).add(source.focus);
                        source.setRayToPoints(ray, tmp1, cv.point);
                        surface = cv.surface;
                    } else {
                        d = pv.surface.trace(source.focus, tmp1.set(cv.point).sub(source.focus), source.maxDist);
                        if (d < source.maxDist && d > 0) {
                            tmp1.setLength(d).add(source.focus);
                            source.setRayToPoints(ray, pv.point, tmp1);
                            surface = pv.surface;
                        } else {
                            trace(source.focus, tmp1.set(pv.point).add(cv.point).scl(0.5f).sub(source.focus),
                                    source.minDistanceDirection(tmp1), source.maxDist, source.surface);
                            surface = tr_surface;

                            float dp = surface
                                    .trace(source.focus, tmp1.set(pv.point).sub(source.focus), source.maxDist);
                            float dc = surface
                                    .trace(source.focus, tmp2.set(cv.point).sub(source.focus), source.maxDist);

                            tmp1.setLength(dp).add(source.focus);
                            tmp2.setLength(dc).add(source.focus);

                            source.setRayToPoints(ray, tmp1, tmp2);
                        }
                    }
                }

                if (!ray.isTooSmall()) {
                    rays.add(ray);
                    surface.receiveLight(source, ray.points[3], ray.points[2], this);
                }

                pv = cv;
            }
        }
    }

    // Ray tracing

    private Surface tr_surface;
    private final Vector2 tr_point = new Vector2();

    private float trace(Vector2 from, Vector2 dir, float min, float max, Surface exclude) {
        tr_surface = NullSurface.INST;
        tr_point.set(dir).setLength(max).add(from);
        for (int i = 0; i < surfaces.size; i++) {
            Surface surface = surfaces.get(i);

            if (surface == exclude) continue;

            float x = surface.trace(from, dir, max);

            if (x < max && x > min) {
                max = x;
                tr_surface = surface;
                tr_point.set(dir).setLength(max).add(from);
            }
        }

        return max;
    }

    //

    private class Vertex {
        Vertex(Vector2 point, Surface surface) {
            this.point = point;
            this.surface = surface;
        }

        Vector2 point;
        Surface surface;
    }

    // Vertex clipping

    private final Plane clip_plane1 = new Plane(), clip_plane2 = new Plane(), clip_plane0 = new Plane();
    private Source clip_source;
    private ArrayList<Vertex> traceVertices = new ArrayList<Vertex>(64);
    private final Surface.VertexCallback clippingCallback = new Surface.VertexCallback() {
        private final Vector2 trDir = new Vector2();

        @Override
        public void call(Surface surface, Vector2 vertex) {
            if (Plane.PlaneSide.Front != clip_plane0.testPoint(vertex.x, vertex.y, 0)) return;
            if (Plane.PlaneSide.Front != clip_plane1.testPoint(vertex.x, vertex.y, 0)) return;
            if (Plane.PlaneSide.Front != clip_plane2.testPoint(vertex.x, vertex.y, 0)) return;

            trDir.set(vertex).sub(clip_source.focus);

            float d = trace(clip_source.focus, trDir,
                    clip_source.minDistanceDirection(trDir), clip_source.maxDist, clip_source.surface);

            if ((trDir.set(vertex).sub(clip_source.focus).len() - d) > 5) return;
//            System.out.println(tr_point.sub(vertex).len2());
//            if (tr_point.sub(vertex).len2() > 0.001) return;

            traceVertices.add(new Vertex(vertex, surface));
        }
    };

    private void prepareClipPlanes(Source source) {
        Vector2 tmp1 = new Vector2();

        planeFromPoints(tmp1, source.point1, source.point2, clip_plane0);
        planeFromPoints(tmp1, source.point1, source.focus, clip_plane1);
        planeFromPoints(tmp1, source.focus, source.point2, clip_plane2);

        clip_source = source;
    }

    private void planeFromPoints(Vector2 tmp, Vector2 p1, Vector2 p2, Plane plane) {
        tmp.set(p2).sub(p1).rotate90(-1).nor();
        plane.set(tmp.x, tmp.y, 0, -tmp.dot(p1));
    }

    // The rest methods ...

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

        Color lightColor = new Color(0.1f, 0.1f, 0.1f, 0.1f);

        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glEnable(GL20.GL_BLEND);

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

        shaper.flush();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glDisable(GL20.GL_BLEND);

//        for (int i = 0; i < traceVertices.size(); i++) {
//            shaper.circle(traceVertices.get(i).point.x, traceVertices.get(i).point.y, i + 1);
//        }
//        Gdx.graphics.setTitle("" + traceVertices.size());
    }
}
