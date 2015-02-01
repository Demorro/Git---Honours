package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {

	final static int windowWidth = 1280;
	final static int windowHeight = 800;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = windowWidth;
		config.height = windowHeight;
		config.vSyncEnabled = false;
		config.fullscreen = false;
		config.backgroundFPS = 0;
		config.foregroundFPS = 0;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
