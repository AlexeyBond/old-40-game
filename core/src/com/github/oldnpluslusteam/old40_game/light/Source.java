package com.github.oldnpluslusteam.old40_game.light;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Source {
    private static final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2();

    public final Vector2 focus = new Vector2();
    public final Vector2 point1 = new Vector2();
    public final Vector2 point2 = new Vector2();

    public float maxDist;

    public Surface surface;

    public void setRayToPoints(Ray ray, Vector2 target1, Vector2 target2) {
        Intersector.intersectLines(focus, target1, point1, point2, ray.points[0]);
        Intersector.intersectLines(focus, target2, point1, point2, ray.points[1]);
        ray.points[2].set(target2);
        ray.points[3].set(target1);
    }

    public float minDistanceDirection(Vector2 dir) {
        tmp1.set(dir).setLength(maxDist).add(focus);
        Intersector.intersectLines(point1, point2, focus, tmp1, tmp2);
        return tmp2.sub(focus).len();
    }
}
