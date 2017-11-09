package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.g2d.Batch;

public class ComputerPlayer implements Player {

    private PintGlass pintGlass;

    public ComputerPlayer(PintGlass pintGlass) {
        this.pintGlass = pintGlass;
    }

    private float getDrinkRate() {
        return 0; // -Math.abs(pintGlass.rotation / 90);
    }

    public void update(float delta) {
        //pintGlass.update(delta * getDrinkRate());
    }

    public void render(Batch batch) {
        pintGlass.render(batch);
    }

    public boolean isFinished() {
        return false;//pintGlass.getAmountRemaining() <= 0;
    }

    public void setDowning(boolean isDowning) {
        // Do nothing
    }
}
