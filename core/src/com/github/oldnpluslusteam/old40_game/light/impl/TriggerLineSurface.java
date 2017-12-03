package com.github.oldnpluslusteam.old40_game.light.impl;

import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.game.Component;
import com.github.alexeybond.partly_solid_bicycle.game.Entity;
import com.github.alexeybond.partly_solid_bicycle.game.Game;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.ComponentDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.util.event.helpers.Subscription;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.BooleanProperty;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.IntProperty;
import com.github.oldnpluslusteam.old40_game.light.LightingSystem;
import com.github.oldnpluslusteam.old40_game.light.Source;

public class TriggerLineSurface extends LineSurfaceComponent {
    private boolean triggering;
    private BooleanProperty triggeringEvent, notTriggeringEvent;
    private IntProperty winCondition, winDone;

    private Subscription<BooleanProperty> triggeredSub = new Subscription<BooleanProperty>() {
        @Override
        public boolean onTriggered(BooleanProperty event) {
            notTriggeringEvent.set(!event.get());
            winDone.set(winDone.get() + (event.get() ? 1 : -1));
            return false;
        }
    };

    public TriggerLineSurface(Vector2 point1, Vector2 point2) {
        super(point1, point2);

        triggering = false;
    }

    @Override
    public void onConnect(Entity entity) {
        super.onConnect(entity);

        winCondition = entity.game().events().event("winCondition", IntProperty.make());
        winDone = entity.game().events().event("winDone", IntProperty.make());
        triggeringEvent = entity.events().event("triggered", BooleanProperty.make(false));
        notTriggeringEvent = entity.events().event("notTriggered", BooleanProperty.make(true));
        triggeredSub.set(triggeringEvent);

        winCondition.set(winCondition.get() + 1);
    }

    @Override
    public void onDisconnect(Entity entity) {
        super.onDisconnect(entity);

        winCondition.set(winCondition.get() - 1);

        triggeringEvent.set(false);
        triggeredSub.clear();
    }

    @Override
    public void preSolve(LightingSystem system) {
        super.preSolve(system);

        triggeringEvent.set(triggering);
        triggering = false;
    }

    @Override
    public void receiveLight(Source source, Vector2 point1, Vector2 point2, LightingSystem system) {
        super.receiveLight(source, point1, point2, system);

        triggering = true;
    }

    public static class Decl implements ComponentDeclaration {
        public float[] points;

        @Override
        public Component create(GameDeclaration gameDeclaration, Game game) {
            return new TriggerLineSurface(
                    new Vector2(points[0], points[1]),
                    new Vector2(points[2], points[3]));
        }
    }
}
