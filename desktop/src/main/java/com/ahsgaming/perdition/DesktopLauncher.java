
package com.ahsgaming.perdition;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        cfg.title = "Towers of Perdition | ahsgaming.com | (c) 2014 Jami Couch";
        cfg.useGL20 = true;
        cfg.width = 1200;     // TODO load from config?
        cfg.height = 700;
        cfg.fullscreen = false;
        cfg.resizable = true;

		new LwjglApplication(new ToPGame(), cfg);
	}
}
