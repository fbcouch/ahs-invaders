package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.physics.bullet.collision.Collision;

/**
* invaders
* (c) 2013 Jami Couch
* User: jami
* Date: 4/8/14
* Time: 1:30 PM
*/
public class ShipBehavior extends BaseBehavior implements CollideBehavior, DamageBehavior, UpdateBehavior {

    public float curHP = 10, maxHP = 10, curSP = 0, maxSP = 0;
    public float throttle = 0;
    public float maxSpeed = 10;

    public ShipBehavior(GameObject gameObject) {
        super(gameObject);
        gameObject.rigidBody.forceActivationState(Collision.DISABLE_DEACTIVATION);
    }

    @Override
    public void onCollide(GameObject other) {
//        other.takeDamage(1);
    }

    @Override
    public void takeDamage(float amount) {
        curHP -= amount;
        if (curHP <= 0) {
            curHP = 0;
            gameObject.setRemove(true); // TODO explode!
        }
    }

    @Override
    public float getCurHP() {
        return curHP;
    }

    @Override
    public float getMaxHP() {
        return maxHP;
    }

    @Override
    public float getCurSP() {
        return curSP;
    }

    @Override
    public float getMaxSP() {
        return maxSP;
    }

    @Override
    public void update(float delta, LevelScreen levelScreen) {
        tempVector.set(gameObject.rigidBody.getLinearVelocity());
        float speed = tempVector.len();
//        if (speed > maxSpeed * throttle) {
//            tempVector.scl(maxSpeed / speed * throttle);
//            gameObject.rigidBody.setLinearVelocity(tempVector);
//        }

        gameObject.transform.getRotation(q);

        if (speed < maxSpeed * throttle) {
            gameObject.rigidBody.setGravity(tempVector.set(0, 0, throttle * gameObject.thrust).mul(q));
        } else {
            gameObject.rigidBody.setGravity(tempVector.set(0, 0, 0));
        }
    }
}
