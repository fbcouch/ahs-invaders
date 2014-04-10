
package com.ahsgaming.invaders;

import com.ahsgaming.invaders.tests.BasicBulletTest;
import com.ahsgaming.invaders.tests.BulletTest;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        cfg.title = "AHS Invaders | ahsgaming.com | (c) 2014 Jami Couch";
//        cfg.useGL30 = true;
        cfg.width = 1920;     // TODO load from config?
        cfg.height = 1050;
        cfg.fullscreen = false;
        cfg.resizable = true;

//        Quaternion q = new Quaternion();
//        q.setEulerAngles(90, 90, 90);
//
//        Vector3 fwd = new Vector3(0, 0, 1);
//        Vector3 up = new Vector3(0, 1, 0);
//
//        fwd.mul(q);
//        up.mul(q);
//
//        System.out.println(String.format("q: %f, %f, %f", q.getPitch(), q.getYaw(), q.getRoll()));
//
//        System.out.println("fwd:" + fwd);
//        System.out.println("up:" + up);
//
//        float pitch = (float)Math.asin(-fwd.y);
//        float yaw = (float)Math.atan2(fwd.x, fwd.z);
//
//        float cosRoll = new Vector3(0, 1, 0).dot(up);
//        float sinRoll = (cosRoll * 0 - up.x) / 1;
//
//        float roll = (float)Math.asin(sinRoll);
//
//        System.out.println("pitch: " + pitch * MathUtils.radiansToDegrees);
//        System.out.println("yaw: " + yaw * MathUtils.radiansToDegrees);
//        System.out.println("roll: " + roll * MathUtils.radiansToDegrees);


		new LwjglApplication(new InvadersGame(), cfg);
	}
}
