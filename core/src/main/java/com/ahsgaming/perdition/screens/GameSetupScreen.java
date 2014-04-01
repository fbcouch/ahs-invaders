package com.ahsgaming.perdition.screens;

import com.ahsgaming.perdition.GameSetupConfig;
import com.ahsgaming.perdition.ToPGame;
import com.ahsgaming.perdition.network.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 1:25 PM
 */
public class GameSetupScreen extends AbstractScreen {
    public static String LOG = "GameSetupScreen";

    GameSetupConfig gameSetupConfig;
    NetInterface netInterface;

    Table playerTable;

    public GameSetupScreen(ToPGame game, GameSetupConfig gameSetupConfig, NetInterface netInterface) {
        super(game);
        this.gameSetupConfig = gameSetupConfig;
        this.netInterface = netInterface;

        netInterface.addListener(new NetListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onError(NetError netError) {

            }

            @Override
            public void onPlayerUpdate(NetInterface netInterface) {
                updatePlayerList();
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        playerTable = new Table(getSkin());

        Table layout = new Table(getSkin());
        layout.setFillParent(true);
        stage.addActor(layout);

        layout.add(new Label("Game Setup", getSkin(), "medium")).padBottom(25).colspan(2).row();

        layout.add(playerTable);
        layout.add("player config");

        updatePlayerList();
    }

    public void updatePlayerList() {
        playerTable.clearChildren();

        for (PlayerConfig playerConfig: netInterface.getPlayerList()) {
            playerTable.add(playerConfig.getName()).row();
        }
    }
}
