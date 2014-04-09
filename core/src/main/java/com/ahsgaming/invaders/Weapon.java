package com.ahsgaming.invaders;

import com.ahsgaming.invaders.behaviors.LaserBehavior;
import com.ahsgaming.invaders.behaviors.MissileBehavior;
import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
* invaders
* (c) 2013 Jami Couch
* User: jami
* Date: 4/8/14
* Time: 12:23 PM
*/
public abstract class Weapon {
    public int curAmmo = 10, maxAmmo = 10;
    public float rechargeTime = 1;
    public float fireTime = 0.2f;
    public float lifetime = 1;

    float rechargeTimer = 0;
    float fireTimer = 0;

    final GameObject gameObject;

    Model model;

    public Weapon(GameObject gameObject, Model model) {
        this.gameObject = gameObject;
        this.model = model;
    }

    public Array<Vector3> firePoints = new Array<Vector3>();

    public void update(float delta) {
        if (rechargeTime >= 0) {
            rechargeTimer += delta;
            if (rechargeTimer > rechargeTime) {
                rechargeTimer -= rechargeTime;
                curAmmo++;
                if (curAmmo > maxAmmo) curAmmo = maxAmmo;
            }
        }

        fireTimer += delta;
    }

    public abstract void fire(LevelScreen levelScreen);

    GameObject fireBullet(LevelScreen levelScreen, Vector3 location) {
        gameObject.transform.getTranslation(GameObject.tempVector);
        gameObject.transform.getRotation(GameObject.q);

        GameObject bullet = levelScreen.createGameObject(model, 10);

        bullet.rotate(GameObject.q).translate(GameObject.tempVector.add(new Vector3(location).mul(GameObject.q)));
        return bullet;
    }

    public static class BasicLaser extends Weapon {

        public float bulletSpeed = 80;

        public BasicLaser(GameObject gameObject, Model model) {
            super(gameObject, model);
            lifetime = 2;
        }

        @Override
        public void fire(LevelScreen levelScreen) {
                if (curAmmo >= firePoints.size && fireTimer > fireTime) {
                    fireTimer = 0;
                    curAmmo -= firePoints.size;

                    for (Vector3 point: firePoints) {
                        GameObject bullet = fireBullet(levelScreen, point);

                        bullet.rigidBody.setLinearVelocity(new Vector3(0, 0, bulletSpeed).mul(GameObject.q));

                        LaserBehavior laserBehavior = new LaserBehavior(bullet);
                        laserBehavior.speed = bulletSpeed;
                        laserBehavior.lifetime = lifetime;
                        bullet.collideBehavior = laserBehavior;
                        bullet.damageBehavior = laserBehavior;
                        bullet.updateBehavior = laserBehavior;
                    }
                }
        }
    }

    public static class BasicMissile extends Weapon {

        public float thrust = 20;

        public BasicMissile(GameObject gameObject, Model model) {
            super(gameObject, model);
            rechargeTime = -1;
            fireTime = 1;
            curAmmo = 5;
            maxAmmo = 5;
            lifetime = 3;
        }

        @Override
        public void fire(LevelScreen levelScreen) {
                if (curAmmo >= firePoints.size && fireTimer > fireTime) {
                    fireTimer = 0;
                    curAmmo -= firePoints.size;

                    for (Vector3 point: firePoints) {
                        GameObject bullet = fireBullet(levelScreen, point);

                        bullet.rigidBody.setLinearVelocity(gameObject.rigidBody.getLinearVelocity());

                        MissileBehavior missileBehavior = new MissileBehavior(bullet);
                        bullet.thrust = thrust;
                        missileBehavior.lifetime = lifetime;
                        bullet.collideBehavior = missileBehavior;
                        bullet.damageBehavior = missileBehavior;
                        bullet.updateBehavior = missileBehavior;
                    }
                }
        }
    }
}
