package com.ahsgaming.invaders.screens;

import com.ahsgaming.invaders.GameSetupConfig;
import com.ahsgaming.invaders.InvadersGame;
import com.ahsgaming.invaders.network.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 1:25 PM
 */
public class GameJoinScreen extends AbstractScreen {
    public static String LOG = "GameJoinScreen";

    NetClient client;

    Label lblError;

    GameSetupConfig gameSetupConfig;

    boolean connected = false;

    public GameJoinScreen(InvadersGame game) {
        super(game);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        Label lblIPAddr;
        final TextField txtIPAddr;
        TextButton btnConnect, btnCancel;

        lblIPAddr = new Label("IP Address", getSkin());
        lblError = new Label("", getSkin());

        txtIPAddr = new TextField("", getSkin());

        btnConnect = new TextButton("Connect", getSkin());
        btnConnect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (txtIPAddr.getText().equals("")) return;

                GameSetupConfig config = new GameSetupConfig();
                config.isHost = false;
                config.ipAddress = txtIPAddr.getText();
                attemptConnection(config);
            }
        });

        btnCancel = new TextButton("Cancel", getSkin(), "cancel");
        btnCancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);    //To change body of overridden methods use File | Settings | File Templates.
                game.setMainMenuScreen();
            }
        });

        Table layout = new Table(getSkin());

        layout.setFillParent(true);
        stage.addActor(layout);

        layout.add(new Label("Join Game", getSkin(), "large")).padBottom(50).colspan(2).row();

        layout.add(lblIPAddr).padRight(10);
        layout.add(txtIPAddr).width(200);

        layout.row();

        layout.add();
        layout.add(btnConnect).right().padTop(15).row();
        layout.add();
        layout.add(btnCancel).right().padTop(15).row();

        layout.add(lblError).padTop(15).colspan(2).row();

        new Thread() {
            public void run() {
                System.setProperty("java.net.preferIPV4Stack", "true");
                Client c = new Client();
                c.start();

                InetAddress iAddress = c.discoverHost(KryoCommon.udpPort, 5000);
                if (iAddress != null && txtIPAddr.getText().equals("")) {
                    txtIPAddr.setText(iAddress.getHostAddress());
                }
            }
        }.start();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (connected) {
            game.setGameSetupScreen(gameSetupConfig, client);
        }
    }

    public void attemptConnection(final GameSetupConfig config) {

        client = new NetClient(game, config, new PlayerConfig(-1, "Test", "mage"));
        client.addListener(new NetListener() {
            @Override
            public void onConnected() {
                Gdx.app.log(LOG, "Connected! We did it!");
                client.removeListener(this);
                gameSetupConfig = config;
                connected = true;
            }

            @Override
            public void onError(NetError netError) {
                Gdx.app.log(LOG, "An error occurred");
                lblError.setText(netError.getMessage());
            }

            @Override
            public void onPlayerUpdate(NetInterface netInterface) {

            }
        });
    }
}
