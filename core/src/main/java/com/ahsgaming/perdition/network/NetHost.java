package com.ahsgaming.perdition.network;

import com.ahsgaming.perdition.ToPGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.util.HashMap;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 2:50 PM
 */
public class NetHost implements NetInterface {
    public static String LOG = "NetHost";
    public static final int MAX_PLAYERS = 4;

    final ToPGame game;

    Server server, broadcastServer;

    Listener currentListener;
    Array<NetListener> listeners;

    HashMap<Connection, PlayerConfig> playerConfigs;

    int nextPlayerId = 0;

    public NetHost(ToPGame game) {
        this.game = game;

        init();
    }

    public void init() {
        server = new Server();
        KryoCommon.register(server);

        playerConfigs = new HashMap<Connection, PlayerConfig>();
        listeners = new Array<NetListener>();

        broadcastServer = new Server();

        try {
            server.bind(KryoCommon.tcpPort);
            server.start();
        } catch (Exception ex) {
            Gdx.app.log(LOG, "Server failed to start");
            Gdx.app.log(LOG, ex.getMessage());
            // TODO tell the game that this failed
            return;
        }

        try {
            broadcastServer.bind(0, KryoCommon.udpPort);
        } catch (Exception ex) {
            Gdx.app.log(LOG, ex.getMessage());
        }

        currentListener = new ServerSetupListener(this);
        server.addListener(currentListener);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public void updatePlayerConfig(PlayerConfig playerConfig) {

    }

    @Override
    public void sendPlayerConfig() {
        // TODO broadcast player list
    }

    @Override
    public void onConnected() {

    }

    public void onConnected(Connection conn, PlayerConfig playerConfig) {
        if (playerConfigs.containsKey(conn)) {
            playerConfigs.get(conn).set(playerConfig);
            return;
        }

        playerConfig.id = nextPlayerId++;
        if (!playerConfig.version.equals(ToPGame.VERSION)) {
            KryoCommon.VersionError ve = new KryoCommon.VersionError();
            ve.serverVersion = ToPGame.VERSION;
            conn.sendTCP(ve);
            onError(ve);
        } else if (playerConfigs.values().size() >= MAX_PLAYERS) {
            KryoCommon.GameFullError ge = new KryoCommon.GameFullError();
            conn.sendTCP(ge);
            onError(ge);
        } else {
            playerConfigs.put(conn, playerConfig);
            conn.sendTCP(playerConfig);
        }
    }

    @Override
    public void onError(NetError error) {
        for (NetListener listener: listeners) {
            listener.onError(error);
        }
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
