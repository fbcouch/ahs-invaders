package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.screens.LevelScreen;

/**
 * invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/8/14
 * Time: 10:33 AM
 */
public interface UpdateBehavior {
    public abstract void update(float delta, LevelScreen levelScreen);
}
