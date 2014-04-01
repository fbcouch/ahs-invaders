package com.ahsgaming.perdition.network;

import com.badlogic.gdx.utils.Array;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 2:49 PM
 */
public interface NetInterface {

    boolean isConnected();
    boolean isConnecting();
    void updatePlayerConfig(PlayerConfig playerConfig);
    void sendPlayerConfig();
    void onConnected();
    void onError(NetError error);
    void addListener(NetListener netListener);
    void removeListener(NetListener netListener);
    Array<PlayerConfig> getPlayerList();
    void setPlayerConfigs(PlayerConfig[] playerConfigs);
}
