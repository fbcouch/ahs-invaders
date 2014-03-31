package com.ahsgaming.perdition.screens;

import com.ahsgaming.perdition.GameSetupConfig;
import com.ahsgaming.perdition.ToPGame;
import com.ahsgaming.perdition.network.NetHost;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 1:25 PM
 */
public class MainMenuScreen extends AbstractScreen {
    public static String LOG = "MainMenuScreen";

    public MainMenuScreen(ToPGame game) {
        super(game);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        
        TextButton btnJoinGame, btnCreateGame, btnOptions, btnExit;
        
        btnJoinGame = new TextButton("Join Game", getSkin());
        btnJoinGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.log(LOG, "Join Game clicked");
                game.setGameJoinScreen();
            }
        });
        
        btnCreateGame = new TextButton("Create Game", getSkin());
        btnCreateGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.log(LOG, "Create Game clicked");
                GameSetupConfig gameSetupConfig = new GameSetupConfig();
                gameSetupConfig.isHost = true;
                game.setGameSetupScreen(gameSetupConfig, new NetHost(game));
            }
        });

        btnOptions = new TextButton("Options", getSkin());
        btnOptions.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.log(LOG, "Options clicked");
            }
        });

        btnExit = new TextButton("Exit", getSkin());
        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.log(LOG, "Exit clicked");
                Gdx.app.exit();
            }
        });

        Table layout = new Table(getSkin());

        layout.setFillParent(true);
        stage.addActor(layout);

        layout.add(new Label("Towers of Perdition", getSkin(), "large")).padBottom(50).row();

        layout.add(btnJoinGame).padBottom(25).fillX().row();
        layout.add(btnCreateGame).padBottom(25).fillX().row();
        layout.add(btnOptions).padBottom(25).fillX().row();
        layout.add(btnExit).padBottom(25).fillX().row();
    }
}
