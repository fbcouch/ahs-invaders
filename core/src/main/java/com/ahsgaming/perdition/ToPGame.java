
package com.ahsgaming.perdition;

import com.ahsgaming.perdition.network.NetInterface;
import com.ahsgaming.perdition.screens.GameJoinScreen;
import com.ahsgaming.perdition.screens.GameSetupScreen;
import com.ahsgaming.perdition.screens.MainMenuScreen;
import com.badlogic.gdx.Game;


public class ToPGame extends Game {

    public final static String VERSION = "0.0.1";

	@Override
	public void create () {
        setMainMenuScreen();
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
