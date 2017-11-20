package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.github.eoinf.cupofmethanol.CupOfMethanol;

import static com.github.eoinf.cupofmethanol.DebugTool.setProjectionMatrix;

/**
 * The screen for the entire downing mini-game
 */
public class DowningGameScreen implements Screen {

    private static final String TEXTURE_PREFIX = "UI/DowningGame/";

    private final CupOfMethanol game;
    private OrthographicCamera camera;
    private DowningGameState state;

    private PintGlass leftPint;
    private PintGlass rightPint;
    private Player leftPlayer;
    private Player rightPlayer;


    private static final float FILL_RATE = 0.7f;

    private TextureRegion getTextureByName(String name) {
        return game.config.atlas.findRegion(TEXTURE_PREFIX + name);
    }

    public DowningGameScreen(final CupOfMethanol game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, game.SCREEN_WIDTH, game.SCREEN_HEIGHT);

        setProjectionMatrix(camera.combined);

        //
        // Initialize STARTING state UI
        //

        // Left player
        TextureRegion pintGlassLeft = getTextureByName("HoldingPint");
        TextureRegion pintBaseLeft = getTextureByName("PintBase");
        TextureRegion pintContentsLeft = getTextureByName("PintContents");

        final Vector2 GLASS_POSITION_OFFSET = new Vector2(80, 50);

        leftPint = new PintGlass(GLASS_POSITION_OFFSET.x, GLASS_POSITION_OFFSET.y,
                pintGlassLeft,
                pintBaseLeft,
                pintContentsLeft,
                true,
                new Color(0.1f, 0, 0, 0.97f),
                new Color(0.9f, 0.8f, 0.7f, 0.9f));

        // Right player
        TextureRegion pintGlassRight = getTextureByName("HoldingPint");
        TextureRegion pintBaseRight = getTextureByName("PintBase");
        TextureRegion pintContentsRight = getTextureByName("PintContents");

        rightPint = new PintGlass(-GLASS_POSITION_OFFSET.x + game.SCREEN_WIDTH - pintGlassRight.getRegionWidth(),
                GLASS_POSITION_OFFSET.y,
                pintGlassRight,
                pintBaseRight,
                pintContentsRight,
                false,
                new Color(0.3f, 0.15f, 0.04f, 0.97f),
                new Color(0.9f, 0.8f, 0.7f, 0.9f));

        //
        // Initialize DOWNING state UI
        //
        leftPlayer = new ComputerPlayer(leftPint);
        rightPlayer = new ComputerPlayer(rightPint);

        //
        // Initialize VICTORY state UI
        //

        //
        // Initialize DEFEAT state UI
        //



        setState(DowningGameState.DOWNING);
    }

    private void setState(DowningGameState nextState) {
        leftPlayer.setDowning(nextState == DowningGameState.DOWNING);
        rightPlayer.setDowning(nextState == DowningGameState.DOWNING);


        this.state = nextState;
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
            case DOWNING:
                renderDowning(delta);
                break;
            case VICTORY:
                renderVictory(delta);
                break;
            case DEFEAT:
                renderDefeat(delta);
                break;
        }
        game.batch.end();
    }

    private void renderStarting(float delta) {
        leftPint.update(delta * FILL_RATE);
        rightPint.update(delta * FILL_RATE);

        if (leftPint.getAmountRemaining() >= 0.5f) {
            setState(DowningGameState.DOWNING);
        }

        leftPint.render(game.batch);
        rightPint.render(game.batch);
    }

    private void renderDowning(float delta) {
        leftPlayer.update(delta);
        rightPlayer.update(delta);

        if (leftPlayer.isFinished()) {
            setState(DowningGameState.DEFEAT);
        } else if (rightPlayer.isFinished()) {
            setState(DowningGameState.VICTORY);
        }

        leftPlayer.render(game.batch);
        rightPlayer.render(game.batch);
    }

    private void renderVictory(float delta) {
        rightPlayer.render(game.batch);
    }

    private void renderDefeat(float delta) {
        leftPlayer.render(game.batch);
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
