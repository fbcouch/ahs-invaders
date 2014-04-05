package com.ahsgaming.invaders.network;

import com.ahsgaming.invaders.InvadersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.util.HashMap;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 2:50 PM
 */
public class NetHost implements NetInterface {
    public static String LOG = "NetHost";
    public static final int MAX_PLAYERS = 4;

    final InvadersGame game;

    Server server, broadcastServer;

    Listener currentListener;
    Array<NetListener> listeners;

    HashMap<Integer, PlayerConfig> playerMap;
    HashMap<Integer, Connection> connectionMap;
    PlayerConfig playerConfig;

    int nextPlayerId = 0;

    public NetHost(InvadersGame game, PlayerConfig playerConfig) {
        this.game = game;
        this.playerConfig = playerConfig;

        init();
    }

    public void init() {
        server = new Server();
        KryoCommon.register(server);

        playerMap = new HashMap<Integer, PlayerConfig>();
        connectionMap = new HashMap<Integer, Connection>();

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
        onConnected(null, playerConfig);
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
        if (playerConfig.getId() > -1 && playerMap.containsKey(playerConfig.getId())) {
            playerMap.get(playerConfig.getId()).set(playerConfig);
            return;
        }

        playerConfig.id = nextPlayerId++;
        if (!playerConfig.version.equals(InvadersGame.VERSION)) {
            KryoCommon.VersionError ve = new KryoCommon.VersionError();
            ve.serverVersion = InvadersGame.VERSION;
            conn.sendTCP(ve);
            onError(ve);
        } else if (playerMap.values().size() >= MAX_PLAYERS) {
            KryoCommon.GameFullError ge = new KryoCommon.GameFullError();
            conn.sendTCP(ge);
            onError(ge);
        } else {
            Gdx.app.log(LOG, "Player connected...");
            playerMap.put(playerConfig.getId(), playerConfig);
            for (NetListener listener: listeners) {
                listener.onPlayerUpdate(this);
            }

            if (conn != null) {
                connectionMap.put(playerConfig.getId(), conn);
                conn.sendTCP(playerConfig);
                sendPlayerList(conn);
            }
        }
    }

    public void sendPlayerList(Connection conn) {
        KryoCommon.PlayerList playerList = new KryoCommon.PlayerList();
        playerList.players = playerMap.values().toArray(new PlayerConfig[playerMap.size()]);
        conn.sendTCP(playerList);
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

    @Override
    public Array<PlayerConfig> getPlayerList() {
        Array<PlayerConfig> players = new Array<PlayerConfig>();
        for (PlayerConfig playerConfig: playerMap.values()) {
            players.add(playerConfig);
        }

        return players;
    }

    @Override
    public void setPlayerConfigs(PlayerConfig[] playerConfigs) {

    }
}
