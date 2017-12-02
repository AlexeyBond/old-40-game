package com.github.oldnpluslusteam.old40_game.screens;

import com.badlogic.gdx.Gdx;
import com.github.alexeybond.partly_solid_bicycle.application.Layer;
import com.github.alexeybond.partly_solid_bicycle.application.Screen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.DefaultScreen;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayer;
import com.github.alexeybond.partly_solid_bicycle.application.impl.layers.GameLayerWith2DPhysicalGame;
import com.github.alexeybond.partly_solid_bicycle.drawing.Technique;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.EDSLTechnique;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.GameDeclaration;
import com.github.alexeybond.partly_solid_bicycle.game.declarative.visitor.impl.ApplyGameDeclarationVisitor;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;
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

        GameLayer gameLayer = new GameLayerWith2DPhysicalGame();
        layers.add("game", gameLayer);

        gameLayer.game().systems().add("light", new LightingSystemImpl());

        GameDeclaration gameDeclaration = IoC.resolve(
                "load game declaration",
                Gdx.files.internal("level0.json"));

        new ApplyGameDeclarationVisitor().doVisit(gameDeclaration, gameLayer.game());
    }
}
