
package com.ahsgaming.invaders;

import com.ahsgaming.invaders.network.NetInterface;
import com.ahsgaming.invaders.screens.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.bullet.Bullet;


public class InvadersGame extends Game {
    public static boolean DEBUG_NOMENU = true;

    public final static String VERSION = "0.0.1";

	@Override
	public void create () {
        Bullet.init();

        if (DEBUG_NOMENU) {
            setScreen(new LevelScreen(this));
        } else {
            setMainMenuScreen();
        }
	}

    public void setMainMenuScreen() {
        setScreen(new MainMenuScreen(this));
    }

    public void setGameSetupScreen(GameSetupConfig gameSetupConfig, NetInterface netInterface) {
        setScreen(new GameSetupScreen(this, gameSetupConfig, netInterface));
    }

    public void setGameJoinScreen() {
        setScreen(new GameJoinScreen(this));
    }
}
