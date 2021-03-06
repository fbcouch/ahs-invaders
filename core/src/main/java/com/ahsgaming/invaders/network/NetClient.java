package com.ahsgaming.invaders.network;

import com.ahsgaming.invaders.GameSetupConfig;
import com.ahsgaming.invaders.InvadersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 2:50 PM
 */
public class NetClient implements NetInterface {
    public static String LOG = "NetClient";

    InvadersGame game;
    Client client;

    boolean connecting = false;

    GameSetupConfig gameConfig;
    PlayerConfig playerConfig;

    Listener currentListener;
    Array<NetListener> listeners;

    Array<PlayerConfig> playerConfigs;

    public NetClient(InvadersGame game, GameSetupConfig gameSetupConfig, PlayerConfig player) {
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

    @Override
    public Array<PlayerConfig> getPlayerList() {
        return new Array<PlayerConfig>(playerConfigs);
    }

    @Override
    public void setPlayerConfigs(PlayerConfig[] playerConfigs) {
        this.playerConfigs = new Array<PlayerConfig>(playerConfigs);
        for (NetListener listener: listeners) {
            listener.onPlayerUpdate(this);
        }
    }
}
