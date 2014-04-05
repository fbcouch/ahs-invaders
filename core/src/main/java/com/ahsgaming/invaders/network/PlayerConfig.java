package com.ahsgaming.invaders.network;

import com.ahsgaming.invaders.InvadersGame;

/**
 * towers-of-invaders
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
    String version = InvadersGame.VERSION;

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
