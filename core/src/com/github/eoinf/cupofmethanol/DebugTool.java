package com.github.eoinf.cupofmethanol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public abstract class DebugTool {

    private static final ShapeRenderer debugRenderer = new ShapeRenderer();

    private static Matrix4 matrix4;

    public static void setProjectionMatrix(Matrix4 matrix) {
        matrix4 = matrix;
    }

    public static void drawLine(Color colour, Vector2 start, Vector2 end) {
        Gdx.gl.glLineWidth(7);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setProjectionMatrix(matrix4);
        debugRenderer.setColor(colour);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
}
