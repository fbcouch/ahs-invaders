package com.ahsgaming.perdition.network;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 5:47 PM
 */
public interface NetListener {
    public void onConnected();
    public void onError(NetError netError);
}
