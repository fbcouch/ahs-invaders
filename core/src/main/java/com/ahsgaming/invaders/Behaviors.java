package com.ahsgaming.invaders;

import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/8/14
 * Time: 9:31 AM
 */
public abstract class Behaviors {
    final GameObject gameObject;

    static Vector3 tempVector = new Vector3();
    static Quaternion q = new Quaternion();

    public Behaviors(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public static class ShipBehavior extends Behaviors implements CollideBehavior, DamageBehavior, UpdateBehavior {

        public float curHP = 10, maxHP = 10;
        public float throttle = 0;
        public float maxSpeed = 10;

        public ShipBehavior(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void onCollide(GameObject other) {
            other.takeDamage(1);
        }

        @Override
        public void takeDamage(float amount) {
            curHP -= amount;
            if (curHP < 0) {
                curHP = 0;
                gameObject.setRemove(true); // TODO explode!
            }
        }

        @Override
        public void update(float delta, LevelScreen levelScreen) {
            tempVector.set(gameObject.rigidBody.getLinearVelocity());
            float speed = tempVector.len();
            if (speed > maxSpeed) {
                tempVector.scl(maxSpeed / speed);
                gameObject.rigidBody.setLinearVelocity(tempVector);
            }

            gameObject.transform.getRotation(q);

            gameObject.rigidBody.setGravity(tempVector.set(0, 0, throttle * gameObject.thrust).mul(q));
        }
    }

    public static class PlayerShipBehavior extends ShipBehavior {

        public PlayerShipBehavior(GameObject gameObject) {
            super(gameObject);
        }

        @Override
        public void update(float delta, LevelScreen levelScreen) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                gameObject.fire(levelScreen);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)) {
                gameObject.pitch(1);
            } else if (Gdx.input.isKeyPressed(Input.Keys.S) && !Gdx.input.isKeyPressed(Input.Keys.W)) {
                gameObject.pitch(-1);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)) {
                gameObject.yaw(1);
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.A)) {
                gameObject.yaw(-1);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Q) && !Gdx.input.isKeyPressed(Input.Keys.E)) {
                gameObject.roll(1);
            } else if (Gdx.input.isKeyPressed(Input.Keys.E) && !Gdx.input.isKeyPressed(Input.Keys.Q)) {
                gameObject.roll(-1);
            }

            Gdx.input.setCursorPosition(
                    (Gdx.input.getX() < 0 ? 0 : (Gdx.input.getX() > Gdx.graphics.getWidth() ? Gdx.graphics.getWidth() : Gdx.input.getX())),
                    (Gdx.input.getY() < 0 ? 0 : (Gdx.input.getY() > Gdx.graphics.getHeight() ? Gdx.graphics.getHeight() : Gdx.input.getY()))
            );

            float x = (Gdx.graphics.getWidth() * 0.5f - Gdx.input.getX()) / (Gdx.graphics.getWidth() * 0.5f);

            if (Math.abs(x) > 0.1f)
                gameObject.roll(- x);

            float y = (Gdx.graphics.getHeight() * 0.5f - Gdx.input.getY()) / (Gdx.graphics.getHeight() * 0.5f);

            if (Math.abs(y) > 0.1f)
                gameObject.pitch(y);

            throttle = levelScreen.scrollAmount / levelScreen.maxScroll;
            
            super.update(delta, levelScreen);
        }
    }

    public static class LaserBehavior extends Behaviors implements CollideBehavior, DamageBehavior, UpdateBehavior {

        public float damage = 1;
        public float lifetime = 1;
        public float speed = 100;

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
}
