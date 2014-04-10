package com.ahsgaming.invaders.behaviors;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.*;

/**
* invaders
* (c) 2013 Jami Couch
* User: jami
* Date: 4/8/14
* Time: 1:30 PM
*/
public class BasicAIShipBehavior extends ShipBehavior {

    static ModelBuilder modelBuilder = new ModelBuilder();
    static Model box;

    Vector3 targetLocation = new Vector3();
    boolean retarget = true;

    static Vector3 currentForward = new Vector3();
    static Vector3 vectorToTarget = new Vector3();
    static Vector2 v1 = new Vector2(), v2 = new Vector2();
    boolean recoverMode = false;

    public ModelInstance targetBox;

    public BasicAIShipBehavior(GameObject gameObject) {
        super(gameObject);
        if (box == null) {
            Material material = new Material();
            material.set(ColorAttribute.createDiffuse(Color.ORANGE));
            box = modelBuilder.createBox(0.2f, 0.2f, 0.2f, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        }
    }

    @Override
    public void update(float delta, LevelScreen levelScreen) {
        if (retarget) {
            findNewTarget();
            retarget = false;
        }

        if (targetBox == null) {
            targetBox = new ModelInstance(box);
        }

        targetBox.transform.setToTranslation(targetLocation);

        // navigate toward the target!
        gameObject.transform.getRotation(q);
        currentForward.set(0, 0, 1).mul(q);
        gameObject.transform.getTranslation(tempVector);
        tempVector.add(gameObject.bounds.getCenter());
        vectorToTarget.set(targetLocation).sub(tempVector);

        v1.set(currentForward.z, currentForward.y);
        v2.set(vectorToTarget.z, vectorToTarget.y);
//        Gdx.app.log("currentForward", "" + currentForward);
//        Gdx.app.log("vectorToTarget", "" + vectorToTarget);
        float pitch = v2.angle() - v1.angle();

        v1.set(currentForward.x, currentForward.z);
        v2.set(vectorToTarget.x, vectorToTarget.z);
        float yaw = v2.angle() - v1.angle();

        currentForward.set(0, 1, 0).mul(q);
        v1.set(currentForward.y, currentForward.x);
        v2.set(vectorToTarget.y, vectorToTarget.x);
        float roll = v2.angle() - v1.angle();
        Gdx.app.log("rollMags", v2.sub(v1).nor().len() + "");

        while (yaw > 180) yaw -= 360;
        while (yaw < -180) yaw += 360;

        while (pitch > 180) pitch -= 360;
        while (pitch < -180) pitch += 360;

        while (roll > 180) roll -= 360;
        while (roll < -180) roll += 360;

        boolean doingSomething = false;

        throttle = 1;


        gameObject.transform.getRotation(q);
        tempVector.set(gameObject.rigidBody.getAngularVelocity().mul(q));

        throttle = 0.3f;
        // pitch first for now ??

        Gdx.app.log("roll, yaw, pitch", "" + roll + "," + yaw + "," + pitch);


//
        if (Math.abs(pitch) > 5) {
            float pitchAmount = -1 * (Math.abs(pitch) / pitch) * ((float)Math.max(0.1, Math.min(1, 3 * (float)Math.pow(Math.abs(pitch) / 180, 2))));
            gameObject.pitch(pitchAmount);
////            gameObject.rotate(pitch, 0, 0);
//            doingSomething = true;
////            Gdx.app.log("pitch", pitch + " (" + pitchAmount + ")");

            doingSomething = true;

        }
        if (Math.abs(yaw) > 5) {
            float yawAmount = -1 * (Math.abs(yaw) / yaw) * ((float)Math.max(0.1, Math.min(1, 3*(float)Math.pow(Math.abs(yaw) / 180, 2))));
            gameObject.yaw(yawAmount);

            doingSomething = true;
//            Gdx.app.log("yaw", yaw + " (" + yawAmount + ")" + " vel: " + tempVector.y);
        }

        if (doingSomething && Math.abs(roll) > 5 && Math.abs(roll) < 175 ) {

            float mag = Math.abs(roll);
            float dir = -1 * Math.abs(roll) / roll;
//            if (mag > 180) {
//                mag = 180 - mag;
//                dir *= -1;
//            }

            float rollAmount = dir * ((float)Math.max(0.1, Math.min(1, 3*(float)Math.pow(mag / 90, 2))));

//            if (Math.abs(roll) > 85 && Math.abs(roll) < 95)
//                gameObject.roll(1);
//            else
            gameObject.roll(rollAmount);

//            Gdx.app.log("roll", roll + " (" + rollAmount + ")" + " vel: " + tempVector.z);
        }


        if (!recoverMode && !doingSomething) {
            throttle = 1;
        }

//        gameObject.rigidBody.setGravity(vectorToTarget.nor().scl(gameObject.thrust));

        gameObject.transform.getTranslation(tempVector);

        if (gameObject.bounds.contains(tempVector.sub(targetLocation).scl(-1))) {
            retarget = true;
        }

//        super.update(delta, levelScreen);
    }

    void findNewTarget() {
        gameObject.transform.getTranslation(targetLocation);
//        targetLocation.add(MathUtils.random(-10, 10), MathUtils.random(-10, 10), MathUtils.random(-10, 10));
        targetLocation.add(-2, 2, 0);
        Gdx.app.log("retarget", targetLocation + "!");
    }
}
