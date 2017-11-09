package com.github.eoinf.cupofmethanol.DowningGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.github.eoinf.cupofmethanol.DebugTool;

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
    public void setAmountRemaining(float value) {
        this.amountRemaining = value;
    }

    public float getAmountRemaining() {
        return this.amountRemaining;
    }

    protected float rotation;
    protected boolean isFlipped;
    private Vector2 position;
    private float drinkHeight;
    public float getDrinkHeight() {
        return drinkHeight;
    }

    public float getGlassHeight() {
        return pintGlass.getHeight();
    }

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

        setAmountRemaining(0.8f);
        this.rotation = 0;
        this.drinkHeight = 0;
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

        int width = pintContentsTexture.getRegionWidth();
        int height = pintContentsTexture.getRegionHeight();

        Color pixelColourBuffer = new Color();

        //
        // NB: The calculations are done with the assumption of breaking the glass into three sections
        // - A triangle (Non-existent when the glass is upright) represents the lower section
        // - A parallelogram (Rectangle when glass is upright) represents the upper section
        // - A parallelogram and 2 triangles (Rectangle that appears when the liquid is pouring out)
        //
        // These calculations don't work for a negative rotation
        //

        // First, calculate where the top left of the pint glass will start after rotating
        double[] topLeftRotated = getRotatedPixels(0, 0, sin, cos, centreX, centreY);
        topLeftRotated[1] = height - topLeftRotated[1];
        double heightDiffAboveGlass = height - topLeftRotated[1];

        /*
        *
        *
        * Calculate the bottom segment of the glass
        *
        *
         */

        CalculationResult glassBottom = calculateHeightInGlassLower(width, height, sin, cos, tan);

        /*
        *
        *
        * Calculate the middle segment of the glass
        *
        *
         */

        double volumeAvailable = (amountRemaining * width * height) - glassBottom.volume;

        float spilloverLength = 0;

        CalculationResult glassMiddle;

        if (glassBottom.spillover == 1) {
            glassMiddle = calculateHeightInGlassMiddleFromLeftCorner(width, height, sin, cos, tan, volumeAvailable);
        } else {
            // Spillover begins if we pass the right corner only
            glassMiddle = calculateHeightInGlassMiddleFromRightCorner(width, height, sin, cos, tan, volumeAvailable);
            spilloverLength += glassMiddle.spillover;
        }

        /*
        *
        *
        * Calculate the upper segment of the glass
        *
        *
         */

        volumeAvailable -= glassMiddle.volume;

        CalculationResult glassUpper = calculateHeightInGlassUpper(width, height, sin, cos, tan, volumeAvailable);
        spilloverLength += glassUpper.spillover;

        /*
        *
        *
        * Calculate the values used for rendering and calculating game progress
        *
        *
         */

        double heightOfGlassTotal = (height * cos + width * sin);

        double textureCutoff = (heightDiffAboveGlass +
                (heightOfGlassTotal - (glassUpper.height + glassMiddle.height + glassBottom.height)));

        drinkHeight = spilloverLength;

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
                    double[] rotated = getRotatedPixels(x, y, sin, cos, centreX, centreY);

                    if (rotated[1] < textureCutoff) {
                        contentsPixmap.drawPixel(adjustedX, adjustedY, Color.rgba8888(CLEAR_WHITE));
                    } else if (rotated[1] < textureCutoff + headAmount) {
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

    class CalculationResult {
        double volume;
        double height;
        // For the lower segment: the direction of which the corner has been reached for this shape
        // Otherwise, the length that, the drink which is escaping the glass, spans over
        float spillover;

        CalculationResult(double volume, double height, float spillover) {
            this.volume = volume;
            this.height = height;
            this.spillover = spillover;
        }
    }


    private CalculationResult calculateHeightInGlassLower(double width, double height, double sin, double cos, double tan) {
        // Calculate the maximum volume that can appear at the bottom of the glass
        double volumeAtBottomMax1 = (width * width * tan) / 2;
        double volumeAtBottomMax2 = (height * height) / (2 * tan);

        // Take the lower max, because the higher value means the shape is extending outside of the glass bounds
        double volumeAtBottomMax = Math.min(volumeAtBottomMax1, volumeAtBottomMax2);

        // This tells us which corner has been reached first
        int direction = volumeAtBottomMax1 == volumeAtBottomMax ? 1 : -1;
        // Special case: Both corners are hit at the same time
        if (volumeAtBottomMax1 == volumeAtBottomMax2) {
            direction = 0;
        }

        // If the volume available is lower than the max possible, use that instead
        double volumeAtBottom = Math.min(amountRemaining * width * height, volumeAtBottomMax);

        // Get the max height within the lower section of the glass
        double heightInGlassLower = Math.sqrt(2 * volumeAtBottom * sin * cos);
        return new CalculationResult(volumeAtBottom, heightInGlassLower, direction);
    }


    /**
     * Calculate the height occupied by the following shape with the parameters given for the glass shape
     *    ___
     *  /   /
     * /___/
     *
     *
     * @param width Width of the glass
     * @param height Height of the glass
     * @param sin
     * @param cos
     * @param tan
     * @param volumeAvailable
     * @return
     */
    private CalculationResult calculateHeightInGlassMiddleFromLeftCorner(double width, double height, double sin, double cos, double tan,
                                                   double volumeAvailable) {

        // The height taken up by the middle section of liquid in the glass
        double heightInGlassMiddle = (
                volumeAvailable * cos
        ) / width;

        double heightInGlassMiddleMax = (height * cos) - (width * sin);
        // If the volume in the middle is lower than the max, use that instead
        heightInGlassMiddle = Math.max(0, Math.min(heightInGlassMiddle, heightInGlassMiddleMax));

        double volumeAtMiddle = heightInGlassMiddle * width / cos;

        //Note: There's no spillover because the liquid hasn't reached the top right corner of the glass yet
        float spillover = 0;

        return new CalculationResult(volumeAtMiddle, heightInGlassMiddle, spillover);
    }


    /**
     * Calculate the height occupied by the following shape with the parameters given for the glass shape
     *  ___
     * \   \
     *  \___\
     *
     * @param width Width of the glass
     * @param height Height of the glass
     * @param sin
     * @param cos
     * @param tan
     * @param volumeAvailable
     * @return
     */
    private CalculationResult calculateHeightInGlassMiddleFromRightCorner(double width, double height, double sin, double cos, double tan,
                                                                 double volumeAvailable) {

        // The height taken up by the middle section of liquid in the glass
        double heightInGlassMiddle = (
                volumeAvailable * sin
        ) / height;

        double heightInGlassMiddleMax = (width * sin) - (height * cos);
        // If the height in the middle is lower than the max, use that instead
        heightInGlassMiddle = Math.max(0, Math.min(heightInGlassMiddle, heightInGlassMiddleMax));

        double volumeAtMiddle = heightInGlassMiddle * height / sin;

        float spillover = (float)(heightInGlassMiddle / sin);

        return new CalculationResult(volumeAtMiddle, heightInGlassMiddle, spillover);
    }


    /**
     * Calculate the height occupied by the following shape with the parameters given for the glass shape
     *    __
     *   /  \
     *  /____\
     *
     * @param width Width of the glass
     * @param height Height of the glass
     * @param sin
     * @param cos
     * @param tan
     * @param volumeAvailable
     * @return
     */
    private CalculationResult calculateHeightInGlassUpper(double width, double height, double sin, double cos, double tan,
                                                   double volumeAvailable) {
        double heightInGlassUpper = width * sin -
                Math.sqrt(width * width * sin * sin
                        - 2 * volumeAvailable * sin * cos);

        heightInGlassUpper = Math.max(0, heightInGlassUpper);

        float spillover = (float)(heightInGlassUpper / sin);

        return new CalculationResult(volumeAvailable, heightInGlassUpper, spillover);
    }

    void render(Batch batch) {
        pintBase.draw(batch);
        pintContents.draw(batch);
        pintGlass.draw(batch);
    }

    public Vector2 getPosition() {
        return position;
    }

    private double[] getRotatedPixels(int x, int y, double sin, double cos, int centreX, int centreY) {
        // The origin must be at 0,0 when rotating, so we must adjust all coordinates
        // so that the centre is at 0,0
        double m = x - centreX;
        double n = y - centreY;

        return new double[]{ (m * cos - n * sin) + centreX, (n * cos + m * sin) + centreY };
    }
}
