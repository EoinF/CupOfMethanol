package com.github.eoinf.cupofmethanol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.eoinf.ethanolshared.Config;
import com.github.eoinf.ethanolshared.GameObjects.Entity;

public class CupOfMethanol extends ApplicationAdapter {
	SpriteBatch batch;
	TextureRegion img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		Config config = Config.loadConfig();
		String textureName = config.getTextureNames(Entity.class)[0];
		img = config.getTexture(textureName, Entity.class);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
