package com.ahsgaming.perdition.network;

import com.ahsgaming.perdition.ToPGame;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 4:09 PM
 */
public class PlayerConfig {
    public static String LOG = "PlayerConfig";

    int id;
    String name = "";
    String classId = "";
    boolean ready = false;
    String version = ToPGame.VERSION;

    public PlayerConfig() {}

    public PlayerConfig(int id, String name, String classId) {
        this.id = id;
        this.name = name;
        this.classId = classId;
    }

    public PlayerConfig set(PlayerConfig playerConfig) {
        this.id = playerConfig.id;
        this.name = playerConfig.name;
        this.classId = playerConfig.classId;
        return this;
    }
}
