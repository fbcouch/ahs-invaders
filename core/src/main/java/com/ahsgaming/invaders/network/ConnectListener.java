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
public class ConnectListener extends Listener {

    NetInterface netInterface;

    public ConnectListener(NetInterface netInterface) {
        super();
        this.netInterface = netInterface;
    }

    @Override
    public void connected(Connection connection) {
        netInterface.sendPlayerConfig();
    }

    @Override
    public void disconnected(Connection connection) {

    }

    @Override
    public void received(Connection connection, Object o) {
        if (o instanceof NetError) {
            netInterface.onError((NetError)o);
        } else if (o instanceof PlayerConfig) {
            netInterface.updatePlayerConfig((PlayerConfig)o);
            netInterface.onConnected();
        }
    }
}
