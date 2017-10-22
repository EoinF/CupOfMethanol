package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.g2d.Batch;

public class HumanPlayer implements Player{

    private PintGlass pintGlass;

    public HumanPlayer(PintGlass pintGlass) {
        this.pintGlass = pintGlass;
    }

    public void update(float delta) {

    }

    public void render(Batch batch) {
        pintGlass.render(batch);
    }

    public boolean isFinished() {
        return pintGlass.amountRemaining <= 0;
    }
}
