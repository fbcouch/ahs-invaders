package com.ahsgaming.perdition.screens;

import com.ahsgaming.perdition.GameSetupConfig;
import com.ahsgaming.perdition.ToPGame;
import com.ahsgaming.perdition.network.NetInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 1:25 PM
 */
public class GameSetupScreen extends AbstractScreen {
    public static String LOG = "GameSetupScreen";

    public GameSetupScreen(ToPGame game, GameSetupConfig gameSetupConfig, NetInterface netInterface) {
        super(game);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        Table layout = new Table(getSkin());
        layout.setFillParent(true);
        stage.addActor(layout);


    }
}
