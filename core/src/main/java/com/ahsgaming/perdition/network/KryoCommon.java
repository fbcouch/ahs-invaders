package com.ahsgaming.perdition.network;

import com.ahsgaming.perdition.ToPGame;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 2:40 PM
 */
public class KryoCommon {
    public static int udpPort = 54560, tcpPort = 54561;

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(String.class);
        kryo.register(PlayerConfig.class);
        kryo.register(PlayerConfig[].class);
        kryo.register(PlayerList.class);
        kryo.register(VersionError.class);
        kryo.register(GameFullError.class);
    }

    // acknowledgement classes and such

    public static class VersionError extends NetError {
        String serverVersion;

        @Override
        public String getMessage() {
            return String.format("Host version (%s) does not match client version (%s).", serverVersion, ToPGame.VERSION);
        }
    }

    public static class GameFullError extends NetError {
        @Override
        public String getMessage() {
            return "That game is full.";
        }
    }

    public static class PlayerList {
        PlayerConfig[] players;
    }

}
