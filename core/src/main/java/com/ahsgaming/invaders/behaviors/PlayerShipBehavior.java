package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.collision.Collision;

/**
* invaders
* (c) 2013 Jami Couch
* User: jami
* Date: 4/8/14
* Time: 1:30 PM
*/
public class PlayerShipBehavior extends ShipBehavior {

    public Vector2 mouseOrientation = new Vector2();

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
            gameObject.roll(-1);
        } else if (Gdx.input.isKeyPressed(Input.Keys.E) && !Gdx.input.isKeyPressed(Input.Keys.Q)) {
            gameObject.roll(1);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            gameObject.selectWeapon(0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            gameObject.selectWeapon(1);
        }

        Gdx.input.setCursorPosition(
                (Gdx.input.getX() < 0 ? 0 : (Gdx.input.getX() > Gdx.graphics.getWidth() ? Gdx.graphics.getWidth() : Gdx.input.getX())),
                (Gdx.input.getY() < 0 ? 0 : (Gdx.input.getY() > Gdx.graphics.getHeight() ? Gdx.graphics.getHeight() : Gdx.input.getY()))
        );

        float x = (Gdx.graphics.getWidth() * 0.5f - Gdx.input.getX()) / (Gdx.graphics.getWidth() * 0.5f);

        float y = - (Gdx.graphics.getHeight() * 0.5f - Gdx.input.getY()) / (Gdx.graphics.getHeight() * 0.5f);

        mouseOrientation.set(x,y);
        if (mouseOrientation.len2() > 1)
            mouseOrientation.nor();

        x = mouseOrientation.x;
        y = mouseOrientation.y;

        if (Math.abs(x) > 0.1f)
            gameObject.yaw(x);

        if (Math.abs(y) > 0.1f)
            gameObject.pitch(y);

        throttle = (float)levelScreen.scrollAmount / levelScreen.maxScroll;

        super.update(delta, levelScreen);
    }
}
