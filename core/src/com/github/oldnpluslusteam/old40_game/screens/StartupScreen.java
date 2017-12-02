package com.github.oldnpluslusteam.old40_game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.github.alexeybond.partly_solid_bicycle.application.Layer;
import com.github.alexeybond.partly_solid_bicycle.application.Screen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.DefaultScreen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayer;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayerWith2DPhysicalGame;
import com.github.alexeybond.partly_solid_bicycle.application.util.ScreenUtils;
import com.github.alexeybond.partly_solid_bicycle.drawing.Technique;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.EDSLTechnique;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.visitor.impl.ApplyGameDeclarationVisitor;
import com.github.alexeybond.partly_solid_bicycle.game.systems.box2d_physics.PhysicsSystem;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
import com.github.alexeybond.partly_solid_bicycle.util.event.Event;
import com.github.alexeybond.partly_solid_bicycle.util.event.EventListener;
import com.github.alexeybond.partly_solid_bicycle.util.event.props.IntProperty;
import com.github.alexeybond.partly_solid_bicycle.util.parts.AParts;
import com.github.oldnpluslusteam.old40_game.light.impl.LightingSystemImpl;

public class StartupScreen extends DefaultScreen {
    @Override
    protected Technique createTechnique() {
        return new EDSLTechnique() {
            @Override
            protected Runnable build() {
                return seq(
                        clearColor(),
                        pass("setup-main-camera"),
                        pass("game-background"),
                        pass("game-light"),
                        pass("game-objects"),
                        pass("game-debug")
                );
            }
        };
    }

    @Override
    protected void createLayers(AParts<Screen, Layer> layers) {
        super.createLayers(layers);

        ScreenUtils.enableToggleDebug(this, true);

        GameLayer gameLayer = new GameLayerWith2DPhysicalGame();
        layers.add("game", gameLayer);

        gameLayer.game().systems().add("light", new LightingSystemImpl());

        gameLayer.game().systems().<PhysicsSystem>get("physics").world()
                .setGravity(new Vector2(0, -100));

        GameDeclaration gameDeclaration = IoC.resolve(
                "load game declaration",
                Gdx.files.internal("level0.json"));

        new ApplyGameDeclarationVisitor().doVisit(gameDeclaration, gameLayer.game());

        final IntProperty wc = gameLayer.game().events().event("winCondition", IntProperty.make());
        final IntProperty wd = gameLayer.game().events().event("winDone", IntProperty.make());

        EventListener<IntProperty> winListener = new EventListener<IntProperty>() {
            @Override
            public boolean onTriggered(IntProperty event) {
                Gdx.graphics.setTitle(""+wd.get()+"/"+wc.get()+" done");
                if (wd.get() != 0 && wd.get() == wc.get()) {
                    // win
                } else {
                    // no win
                }
                return false;
            }
        };

        wc.subscribe(winListener);
        wd.subscribe(winListener);

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.24f, 0.0f);
    }
}
