package com.ahsgaming.invaders.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 5:28 PM
 */
public class ServerSetupListener extends Listener {

    NetHost netHost;

    public ServerSetupListener(NetHost netHost) {
        super();
        this.netHost = netHost;
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
    }

    @Override
    public void received(Connection connection, Object o) {
        if (o instanceof PlayerConfig) {
            netHost.onConnected(connection, (PlayerConfig)o);
        }
    }
}
