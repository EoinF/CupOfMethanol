package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.g2d.Batch;

public class ComputerPlayer implements Player {

    private PintGlass pintGlass;

    private static final float DRINK_RATE_MIN = -0.1f;
    private static final float ROTATE_FORCE = 20f;

    public ComputerPlayer(PintGlass pintGlass) {
        this.pintGlass = pintGlass;
        amountToMove = 0;
    }

    private float getDrinkRate() {
        return Math.min(0, -pintGlass.getDrinkHeight() * PintGlass.DRINK_HEIGHT_RATIO);
    }

    int amountToMove;

    public void update(float delta) {
        float drinkRate = getDrinkRate();

        System.out.println(drinkRate);

        if (drinkRate > DRINK_RATE_MIN && Math.random() > 0.90) {
            amountToMove = 10 + (int)(Math.random() * 20);
            if (Math.random() > 0.95) {
                amountToMove += 20;
            }
        }

        if (amountToMove > 0) {
            pintGlass.rotate(delta * ROTATE_FORCE);
            amountToMove--;
        }

        pintGlass.update(delta * drinkRate);
    }

    public void render(Batch batch) {
        pintGlass.render(batch);
    }

    public boolean isFinished() {
        return pintGlass.getAmountRemaining() <= 0.01f;
    }

    public void setDowning(boolean isDowning) {
        // Do nothing
    }
}
