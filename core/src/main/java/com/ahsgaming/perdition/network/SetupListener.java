package com.ahsgaming.perdition.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 5:28 PM
 */
public class SetupListener extends Listener {

    NetInterface netInterface;

    public SetupListener(NetInterface netInterface) {
        super();
        this.netInterface = netInterface;
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
    }

    @Override
    public void received(Connection connection, Object o) {

    }
}
