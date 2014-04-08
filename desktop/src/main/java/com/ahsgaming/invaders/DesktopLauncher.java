
package com.ahsgaming.invaders;

import com.ahsgaming.invaders.tests.BasicBulletTest;
import com.ahsgaming.invaders.tests.BulletTest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        cfg.title = "Towers of Perdition | ahsgaming.com | (c) 2014 Jami Couch";
//        cfg.useGL30 = true;
        cfg.width = 1920;     // TODO load from config?
        cfg.height = 1050;
        cfg.fullscreen = false;
        cfg.resizable = true;

		new LwjglApplication(new InvadersGame(), cfg);
	}
}
