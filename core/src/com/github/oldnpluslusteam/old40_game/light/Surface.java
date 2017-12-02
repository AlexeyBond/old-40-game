package com.github.oldnpluslusteam.old40_game.light;

import com.badlogic.gdx.math.Vector2;

public interface Surface {
    public interface VertexCallback {
        void call(Surface surface, Vector2 vertex);
    }

    void getVertices(VertexCallback callback);

    void preSolve(LightingSystem system);

    float trace(Vector2 start, Vector2 dir, float max);

    void receiveLight(Source source, Vector2 point1, Vector2 point2);
}
