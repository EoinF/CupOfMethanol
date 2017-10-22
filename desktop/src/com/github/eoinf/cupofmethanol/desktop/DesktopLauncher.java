package com.github.eoinf.cupofmethanol.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.eoinf.cupofmethanol.CupOfMethanol;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Cup Of Methanol";
		config.width = 1280;
		config.height = 720;
		config.fullscreen = false;
		new LwjglApplication(new CupOfMethanol(), config);
	}
}
