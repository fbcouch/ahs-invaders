package com.ahsgaming.invaders.network;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 5:47 PM
 */
public interface NetListener {
    public void onConnected();
    public void onError(NetError netError);

    void onPlayerUpdate(NetInterface netInterface);
}
