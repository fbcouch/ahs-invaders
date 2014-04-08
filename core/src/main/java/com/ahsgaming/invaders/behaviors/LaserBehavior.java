package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.screens.LevelScreen;

/**
* invaders
* (c) 2013 Jami Couch
* User: jami
* Date: 4/8/14
* Time: 1:30 PM
*/
public class LaserBehavior extends BaseBehavior implements CollideBehavior, DamageBehavior, UpdateBehavior {

    public float damage = 1;
    public float lifetime = 1;
    public float speed = 25;

    public LaserBehavior(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void onCollide(GameObject other) {
        other.takeDamage(damage);
        gameObject.setRemove(true); // TODO explode!
    }

    @Override
    public void takeDamage(float amount) {
        // no-op
    }

    @Override
    public float getCurHP() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getMaxHP() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getCurSP() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getMaxSP() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(float delta, LevelScreen levelScreen) {
        lifetime -= delta;
        if (lifetime < 0) gameObject.setRemove(true);

        tempVector.set(gameObject.rigidBody.getLinearVelocity());
        float curSpeed = tempVector.len();
        if (curSpeed < speed) {
            gameObject.rigidBody.setLinearVelocity(tempVector.scl(speed / curSpeed));
        }
    }
}
