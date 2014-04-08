package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.GameObject;

/**
 * invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/8/14
 * Time: 9:30 AM
 */
public interface CollideBehavior {
    public abstract void onCollide(GameObject other);
}
