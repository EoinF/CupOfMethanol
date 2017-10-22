package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Player {
    void update(float delta);
    void render(Batch batch);
    boolean isFinished();
}
