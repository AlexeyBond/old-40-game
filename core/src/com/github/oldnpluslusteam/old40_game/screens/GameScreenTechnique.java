package com.github.oldnpluslusteam.old40_game.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.alexeybond.partly_solid_bicycle.drawing.Pass;
import com.github.alexeybond.partly_solid_bicycle.drawing.TargetSlot;
import com.github.alexeybond.partly_solid_bicycle.drawing.tech.PlainTechnique;
import com.github.alexeybond.partly_solid_bicycle.ioc.IoC;

public class GameScreenTechnique extends PlainTechnique {
    private Pass setupCameraPass, backgroundPass, lightPass, objectsPass, debugPass, uiPass;

    private ShaderProgram blurShader;
    private TargetSlot[] lightSlots;

    @Override
    protected void setup() {
        setupCameraPass = newPass("setup-main-camera");
        backgroundPass = newPass("game-background");
        lightPass = newPass("game-light");
        objectsPass = newPass("game-objects");
        debugPass = newPass("game-debug");
        uiPass = newPass("ui-main");

        blurShader = loadShader(
                "shaders/postfx-vs.glsl",
                "shaders/postfx-ps.glsl");

        lightSlots = new TargetSlot[] {
                scene().context().getSlot("light-1"),
                scene().context().getSlot("light-2")
        };
    }

    @Override
    protected void draw() {
        ensureMatchingFBO(lightSlots[0], context().getOutputTarget());
        ensureMatchingFBO(lightSlots[1], context().getOutputTarget());

        lightSlots[0].swap(lightSlots[1]);
        toTarget(lightSlots[0]);
        gl.glClearColor(0,0,0,1);
        clear();

        Batch batch = context().state().beginBatch();
        batch.setShader(blurShader);
        blurShader.setUniformf("u_targetSizeInv",
                    1f / context().getOutputTarget().getPixelsWidth(),
                    1f / context().getOutputTarget().getPixelsHeight()
                );
        screenQuad(lightSlots[1].get().asColorTexture(), true);
        batch.setShader(null);
        doPass(setupCameraPass);
        doPass(lightPass);

        toOutput();
        clear();
        doPass(setupCameraPass);
        doPass(backgroundPass);
        screenQuad(lightSlots[0].get().asColorTexture(), true);
        doPass(setupCameraPass);
        doPass(objectsPass);
        doPass(debugPass);
        doPass(uiPass);
    }
}
