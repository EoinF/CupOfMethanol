package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PintGlass {

    private Sprite pintGlass;
    private Color pintColour;
    private Color headColour;
    private Sprite pintContents;
    private TextureRegion pintContentsTexture;

    private static Color CLEAR_WHITE = new Color(1, 1, 1, 0);
    private static Color CLEAR_BLACK = new Color(0, 0, 0, 0);

    private Sprite pintBase;

    private Pixmap contentsPixmap;
    private Texture pintContentsTemp;

    public float amountRemaining;
    protected float rotation;
    protected boolean isFlipped;

    private static final float HEAD_RATIO = 0.1f;

    public PintGlass(float x, float y,
                  TextureRegion pintGlassTexture, TextureRegion pintBaseTexture, TextureRegion pintContentsTexture,
                  boolean isFlipped, Color pintColour, Color headColour) {
        this.pintGlass = new Sprite(pintGlassTexture);
        this.pintGlass.setFlip(isFlipped, false);
        this.pintGlass.setPosition(x, y);

        float offsetX = isFlipped ? pintGlassTexture.getRegionWidth() - pintContentsTexture.getRegionWidth(): 0;

        this.pintBase = new Sprite(pintBaseTexture);
        this.pintBase.setFlip(isFlipped, false);
        this.pintBase.setPosition(x, y);

        float heightDiff = pintGlassTexture.getRegionHeight() - pintContentsTexture.getRegionHeight();
        this.pintContents = new Sprite(pintContentsTexture);
        this.pintContents.setFlip(isFlipped, false);
        this.pintContents.setPosition(x + offsetX, heightDiff);

        this.pintContents.setOrigin(this.pintContents.getOriginX(),
                this.pintContents.getOriginY() - (heightDiff / 2));
        this.pintContentsTexture = pintContentsTexture;

        TextureData textureDataOriginal = pintContentsTexture.getTexture().getTextureData();
        if (!textureDataOriginal.isPrepared()) {
            textureDataOriginal.prepare();
        }
        contentsPixmap = textureDataOriginal.consumePixmap();

        /*
         * Separate the texture into black transparent pixels and white transparent pixels
         * The white pixels represent the contents
         * The black pixels represent the parts that are always transparent
         */
        contentsPixmap.setBlending(Pixmap.Blending.None);
        for (int i = 0; i < contentsPixmap.getWidth(); i++) {
            for (int j = 0; j < contentsPixmap.getHeight(); j++) {
                Color pixelColour = new Color();
                Color.rgba8888ToColor(pixelColour, contentsPixmap.getPixel(i, j));

                if (pixelColour.a > 0) {
                    contentsPixmap.drawPixel(i, j, Color.rgba8888(CLEAR_WHITE));
                } else {
                    contentsPixmap.drawPixel(i, j, Color.rgba8888(CLEAR_BLACK));
                }
            }
        }

        this.pintColour = pintColour;
        this.headColour = headColour;

        this.isFlipped = isFlipped;

        this.amountRemaining = 0;
        this.rotation = 45;
    }

    void update(float delta) {
        amountRemaining += delta;
        amountRemaining = Math.min(1, amountRemaining);
        amountRemaining = Math.max(0, amountRemaining);

        updateTextures();
    }

    private void updateTextures() {
        float textureCutoff = pintContentsTexture.getRegionHeight() * (1 - amountRemaining);

        // The amount of head diminishes as the beer goes away
        float headAmount = HEAD_RATIO * pintContentsTexture.getRegionHeight() * amountRemaining;

        contentsPixmap.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < contentsPixmap.getWidth(); x++) {
            for (int y = 0; y < contentsPixmap.getHeight(); y++) {
                Color pixelColour = new Color();
                Color.rgba8888ToColor(pixelColour, contentsPixmap.getPixel(x, y));

                // Only handle pixels that aren't always transparent
                if (!pixelColour.equals(CLEAR_BLACK)) {
                    if (y < textureCutoff) {
                        contentsPixmap.drawPixel(x, y, Color.rgba8888(Color.BLACK));
                    } else if (y < textureCutoff + headAmount) {
                        contentsPixmap.drawPixel(x, y, Color.rgba8888(headColour));
                    } else {
                        contentsPixmap.drawPixel(x, y, Color.rgba8888(pintColour));
                    }
                }
            }
        }
        if (pintContentsTemp != null) {
            pintContentsTemp.dispose();
        }
        pintContentsTemp = new Texture(contentsPixmap);
        pintContents.setTexture(pintContentsTemp);

        float rotationAdjusted = isFlipped ? rotation : -rotation;

        pintContents.setRotation(rotationAdjusted);
        pintGlass.setRotation(rotationAdjusted);
        pintBase.setRotation(rotationAdjusted);
    }

    void render(Batch batch) {
        pintBase.draw(batch);
        pintContents.draw(batch);
        pintGlass.draw(batch);
    }
}
