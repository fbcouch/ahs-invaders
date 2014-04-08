package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.GameObject;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/8/14
 * Time: 9:31 AM
 */
public abstract class BaseBehavior {
    final GameObject gameObject;

    static Vector3 tempVector = new Vector3();
    static Quaternion q = new Quaternion();

    public BaseBehavior(GameObject gameObject) {
        this.gameObject = gameObject;
    }

}
