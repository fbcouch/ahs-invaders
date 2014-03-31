package com.ahsgaming.perdition.network;

import com.ahsgaming.perdition.GameSetupConfig;
import com.ahsgaming.perdition.ToPGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 2:50 PM
 */
public class NetClient implements NetInterface {
    public static String LOG = "NetClient";

    ToPGame game;
    Client client;

    boolean connecting = false;

    GameSetupConfig gameConfig;
    PlayerConfig playerConfig;

    Listener currentListener;
    Array<NetListener> listeners;

    public NetClient(ToPGame game, GameSetupConfig gameSetupConfig, PlayerConfig player) {
        this.game = game;
        gameConfig = gameSetupConfig;
        this.playerConfig = player;
        listeners = new Array<NetListener>();

        client = new Client();
        client.start();

        KryoCommon.register(client);

        currentListener = new ConnectListener(this);
        client.addListener(currentListener);

        connect();
    }

    public void connect() {
        connecting = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    Gdx.app.log(LOG, "Connecting to " + gameConfig.ipAddress + ":" + KryoCommon.tcpPort);
                    client.connect(5000, gameConfig.ipAddress, KryoCommon.tcpPort);
                } catch (IOException e) {
                    Gdx.app.log(LOG, "Client connection failed: " + e.getMessage());
                    e.printStackTrace();
                    connecting = false;
                }
            }
        }.start();
    }

    @Override
    public void onConnected() {
        connecting = false;
        client.removeListener(currentListener);

        currentListener = new SetupListener(this);
        client.addListener(currentListener);

        for (NetListener listener: listeners) {
            listener.onConnected();
        }
    }

    @Override
    public void onError(NetError error) {
        for (NetListener listener: listeners) {
            listener.onError(error);
        }
    }

    @Override
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    @Override
    public boolean isConnecting() {
        return connecting;
    }

    @Override
    public void updatePlayerConfig(PlayerConfig playerConfig) {
        this.playerConfig.set(playerConfig);
    }

    @Override
    public void sendPlayerConfig() {
        client.sendTCP(playerConfig);
    }

    @Override
    public void addListener(NetListener netListener) {
        listeners.add(netListener);
    }

    @Override
    public void removeListener(NetListener netListener) {
        listeners.removeValue(netListener, true);
    }
}
