package com.ahsgaming.invaders;

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
        rechargeTimer += delta;
        if (rechargeTimer > rechargeTime) {
            rechargeTimer -= rechargeTime;
            curAmmo++;
            if (curAmmo > maxAmmo) curAmmo = maxAmmo;
        }

        fireTimer += delta;
    }

    public abstract void fire(LevelScreen levelScreen);

    GameObject fireBullet(LevelScreen levelScreen, Vector3 location) {
        gameObject.transform.getTranslation(GameObject.tempVector);
        gameObject.transform.getRotation(GameObject.q);

        GameObject bullet = levelScreen.createGameObject(model, 1);

        bullet.rotate(GameObject.q).translate(GameObject.tempVector.add(new Vector3(location).mul(GameObject.q)));
        return bullet;
    }

    public static class BasicLaser extends Weapon {

        public float bulletSpeed = 100;

        public BasicLaser(GameObject gameObject, Model model) {
            super(gameObject, model);
        }

        @Override
        public void fire(LevelScreen levelScreen) {
                if (curAmmo > firePoints.size && fireTimer > fireTime) {
                    fireTimer = 0;
                    curAmmo -= firePoints.size;

                    for (Vector3 point: firePoints) {
                        GameObject bullet = fireBullet(levelScreen, point);

                        bullet.rigidBody.setLinearVelocity(new Vector3(0, 0, bulletSpeed).mul(GameObject.q));

                        Behaviors.LaserBehavior laserBehavior = new Behaviors.LaserBehavior(bullet);
                        laserBehavior.speed = bulletSpeed;
                        bullet.collideBehavior = laserBehavior;
                        bullet.damageBehavior = laserBehavior;
                        bullet.updateBehavior = laserBehavior;
                    }
                }
        }
    }
}
