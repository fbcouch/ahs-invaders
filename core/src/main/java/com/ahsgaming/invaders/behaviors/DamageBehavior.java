package com.ahsgaming.invaders.behaviors;

/**
 * invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/8/14
 * Time: 9:30 AM
 */
public interface DamageBehavior {
    public abstract void takeDamage(float amount);
    public abstract float getCurHP();
    public abstract float getMaxHP();
    public abstract float getCurSP();
    public abstract float getMaxSP();
}
