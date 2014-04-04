
package com.ahsgaming.perdition;

import com.ahsgaming.perdition.network.NetInterface;
import com.ahsgaming.perdition.screens.GameJoinScreen;
import com.ahsgaming.perdition.screens.GameSetupScreen;
import com.ahsgaming.perdition.screens.MainMenuScreen;
import com.ahsgaming.perdition.screens.Test3dScreen;
import com.badlogic.gdx.Game;


public class ToPGame extends Game {
    public static boolean DEBUG_NOMENU = true;

    public final static String VERSION = "0.0.1";

	@Override
	public void create () {

        if (DEBUG_NOMENU) {
            setScreen(new Test3dScreen(this));
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
