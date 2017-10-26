package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

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

    private float amountRemaining;
    // The area taken up by the amount remaining
    private float contentsArea;
    public void setAmountRemaining(float value) {
        this.amountRemaining = value;
        this.contentsArea = this.pintContentsTexture.getRegionWidth() *
                this.pintContentsTexture.getRegionHeight() * amountRemaining;
    }

    public float getAmountRemaining() {
        return this.amountRemaining;
    }

    protected float rotation;
    protected boolean isFlipped;
    private Vector2 position;

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

        float widthDiff = pintGlassTexture.getRegionWidth() - pintContentsTexture.getRegionWidth();
        float heightDiff = pintGlassTexture.getRegionHeight() - pintContentsTexture.getRegionHeight();
        this.pintContents = new Sprite(pintContentsTexture);
        this.pintContents.setFlip(isFlipped, false);
        this.pintContents.setPosition(x + offsetX, y + heightDiff);

        this.pintContents.setOrigin(this.pintContents.getOriginX() + (widthDiff / 2),
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

        setAmountRemaining(0.5f);
        this.rotation = 0;
        this.position = new Vector2(x + pintGlassTexture.getRegionWidth() / 2,
                y + pintBaseTexture.getRegionHeight() / 2);
    }

    void update(float delta) {
        float newAmount = Math.max(0, Math.min(1, amountRemaining + delta));
        setAmountRemaining(newAmount);

        updateTextures();
    }

    private void updateTextures() {
        int centreX = (int)pintContents.getOriginX();
        int centreY = (int)pintContents.getOriginY();

        double cos = Math.cos(Math.toRadians(rotation));
        double sin = Math.sin(Math.toRadians(rotation));
        double tan = Math.tan(Math.toRadians(rotation));

        // Need to rotate in the opposite direction when calculating cutoff
        // because pixmap coordinates are y up, whereas screen and image coordinates are y down
        double cosReverse = Math.cos(Math.toRadians(-rotation));
        double sinReverse = Math.sin(Math.toRadians(-rotation));

        int width = pintContentsTexture.getRegionWidth();
        int height = pintContentsTexture.getRegionHeight();

        Color pixelColourBuffer = new Color();

        // First, calculate where the top left of the pint glass will start after rotating
        Vector2 topLeftRotated = getRotatedPixels(0, height, sinReverse, cosReverse, centreX, centreY);
        float heightDiff = height - topLeftRotated.y;

        // The height taken up by the upper section of liquid in the glass
        double heightInGlass = (
                2 * amountRemaining * width * height  - (width * width * tan)
        ) * cos / (2 * width);

        float textureCutoff = heightDiff + (float)(height * cos - heightInGlass);

        // The amount of head diminishes as the beer goes away
        float headAmount = HEAD_RATIO * pintContentsTexture.getRegionHeight() * amountRemaining;

        int regionX = pintContentsTexture.getRegionX();
        int regionY = pintContentsTexture.getRegionY();

        contentsPixmap.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < pintContentsTexture.getRegionWidth(); x++) {
            for (int y = 0; y < pintContentsTexture.getRegionHeight(); y++) {
                // Find the pixel location within the pixmap as it is offset by the regionX, regionY values
                int adjustedX = x + regionX;
                int adjustedY = y + regionY;
                Color.rgba8888ToColor(pixelColourBuffer, contentsPixmap.getPixel(adjustedX, adjustedY));

                // Only handle pixels that aren't always transparent
                if (!pixelColourBuffer.equals(CLEAR_BLACK)) {
                    Vector2 rotated = getRotatedPixels(x, y, sin, cos, centreX, centreY);

                    if (rotated.y < textureCutoff) {
                        contentsPixmap.drawPixel(adjustedX, adjustedY, Color.rgba8888(CLEAR_WHITE));
                    } else if (rotated.y < textureCutoff + headAmount) {
                        contentsPixmap.drawPixel(adjustedX, adjustedY, Color.rgba8888(headColour));
                    } else {
                        contentsPixmap.drawPixel(adjustedX, adjustedY, Color.rgba8888(pintColour));
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

    public Vector2 getPosition() {
        return position;
    }

    private Vector2 getRotatedPixels(int x, int y, double sin, double cos, int centreX, int centreY) {
        // The origin must be at 0,0 when rotating, so we must adjust all coordinates
        // so that the centre is at 0,0
        float m = x - centreX;
        float n = y - centreY;

        return new Vector2((float)(m * cos - n * sin) + centreX, (float)(n * cos + m * sin) + centreY);
    }
}
