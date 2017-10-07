package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.eoinf.cupofmethanol.CupOfMethanol;

/**
 * The screen for the entire downing mini-game
 */
public class DowningGameScreen implements Screen {

    private final CupOfMethanol game;
    OrthographicCamera camera;
    DowningGameState state;

    Sprite pintGlassLeft;
    Sprite pintGlassRight;

    public DowningGameScreen(final CupOfMethanol game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
        state = DowningGameState.STARTING;

        // Initialize STARTING state UI
        pintGlassLeft = new Sprite(game.config.atlas.findRegion("UI/DowningGame/HoldingPint"));
        pintGlassLeft.flip(true, false);
        pintGlassRight = new Sprite(game.config.atlas.findRegion("UI/DowningGame/HoldingPint"));


        // Initialize DOWNING state UI
        // Initialize VICTORY state UI
        // Initialize DEFEAT state UI
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        switch(state) {
            case STARTING:
                renderStarting(delta);
                break;
        }

        game.batch.end();
    }

    private void renderStarting(float delta) {
        game.batch.draw(pintGlassLeft, 0, 0);
        game.batch.draw(pintGlassRight,  game.SCREEN_WIDTH - pintGlassRight.getWidth(), 0);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

    }
}
