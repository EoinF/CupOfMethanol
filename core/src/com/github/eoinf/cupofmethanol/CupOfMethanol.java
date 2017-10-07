package com.github.eoinf.cupofmethanol;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.eoinf.cupofmethanol.DowningGame.DowningGameScreen;
import com.github.eoinf.ethanolshared.Config;

public class CupOfMethanol extends Game {
	public SpriteBatch batch;
	public Config config;

	public final int SCREEN_WIDTH = 1280;
	public final int SCREEN_HEIGHT = 720;

	@Override
	public void create () {
		batch = new SpriteBatch();
		config = Config.loadConfig();

		this.setScreen(new DowningGameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
