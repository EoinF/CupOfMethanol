package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class HumanPlayer implements Player {

    private PintGlass pintGlass;

    public HumanPlayer(PintGlass pintGlass) {
        this.pintGlass = pintGlass;
    }

    private float getDrinkRate() {
        return Math.min(0, -pintGlass.getDrinkHeight() * PintGlass.DRINK_HEIGHT_RATIO);
    }

    public void update(float delta) {
        /*
        Vector2 pintPosition = pintGlass.getPosition();
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        Gdx.input.setCursorPosition((int)pintPosition.x, (int)pintPosition.y);

        float flipMultiplier = pintGlass.isFlipped ? 1 : -1;
        float diff = flipMultiplier * (pintPosition.x - x);
        pintGlass.rotate(delta * diff);

        pintGlass.update(delta * getDrinkRate());
        */
    }

    public void render(Batch batch) {
        pintGlass.render(batch);
    }

    public boolean isFinished() {
        return false; //pintGlass.getAmountRemaining() <= 0;
    }

    public void setDowning(boolean isDowning) {
        if (isDowning) {
            Gdx.input.setCursorCatched(true);
            Vector2 pintPosition = pintGlass.getPosition();
            Gdx.input.setCursorPosition((int)pintPosition.x, (int)pintPosition.y);
        } else {
            Gdx.input.setCursorCatched(false);
        }
    }
}
