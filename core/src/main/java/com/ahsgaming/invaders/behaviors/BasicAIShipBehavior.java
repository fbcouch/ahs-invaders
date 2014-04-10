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
        gameObject.transform.getTranslation(tempVector);
        tempVector.add(gameObject.bounds.getCenter());
        vectorToTarget.set(targetLocation).sub(tempVector);

        gameObject.transform.getRotation(q);

        currentForward.set(0, 0, 1).mul(q);
        v1.set(currentForward.z, currentForward.y);
        v2.set(vectorToTarget.z, vectorToTarget.y);
//        Gdx.app.log("currentForward", "" + currentForward);
//        Gdx.app.log("vectorToTarget", "" + vectorToTarget);
        float pitch = v2.angle() - v1.angle();

        v1.set(currentForward.x, currentForward.z);
        v2.set(vectorToTarget.x, vectorToTarget.z);
        float yaw = v2.angle() - v1.angle();
//        float cYaw = v1.angle();
//        Gdx.app.log("cYaw", "" + cYaw);

//        Gdx.app.log("q (x, y, z)", "" + q.getPitch() + "," + q.getYaw() + "," + q.getRoll());
        Quaternion temp = new Quaternion();
        Vector3 a = new Vector3(currentForward).crs(vectorToTarget);
        temp.set(a.x, a.y, a.z, (float)Math.sqrt(currentForward.len2() + vectorToTarget.len2()) + currentForward.dot(vectorToTarget)).nor().mul(q);
//        Gdx.app.log("temp (x, y, z)", "" + temp.getPitch() + "," + temp.getYaw() + "," + temp.getRoll());
        temp.conjugate();
//        Gdx.app.log("tempconj(x, y, z)", "" + temp.getPitch() + "," + temp.getYaw() + "," + temp.getRoll());
//
//        Gdx.app.log("q (x, y, z)", "" + q.getPitch() + "," + q.getYaw() + "," + q.getRoll());

        currentForward.set(0, 1, 0).mul(q);
//        Gdx.app.log("currentForward", "" + currentForward);
//        Gdx.app.log("vectorToTarget", "" + vectorToTarget);
//
//        v1.set(currentForward.y, currentForward.x * MathUtils.cosDeg(cYaw) + currentForward.z * MathUtils.sinDeg(cYaw));
//        v2.set(vectorToTarget.y, vectorToTarget.x * MathUtils.cosDeg(cYaw) + vectorToTarget.z * MathUtils.sinDeg(cYaw));
        float roll = v2.angle() - v1.angle();

//        Gdx.app.log("v1", "" + v1.angle());

        while (yaw > 180) yaw -= 360;
        while (yaw < -180) yaw += 360;

        while (pitch > 180) pitch -= 360;
        while (pitch < -180) pitch += 360;

        while (roll > 180) roll -= 360;
        while (roll < -180) roll += 360;

        // new thing! ----------------------
        gameObject.transform.getRotation(q);
        float cPitch = 0, cYaw = 0, cRoll = 0;
        tempVector.set(getPYR(q));
        cPitch = tempVector.x;
        cRoll = tempVector.z;
//        Gdx.app.log("curPYR", "" + tempVector);

        pitch = temp.getPitch();
        roll = temp.getRoll();
        yaw = temp.getYaw();

        temp = new Quaternion();
        a = new Vector3(currentForward).crs(vectorToTarget);
        temp.set(a.x, a.y, a.z, (float)Math.sqrt(currentForward.len2() + vectorToTarget.len2()) + currentForward.dot(vectorToTarget)).nor().mul(q);
        float fPitch = 0, fYaw = 0, fRoll = 0;
        tempVector.set(getPYR(temp));
//        Gdx.app.log("q (x, y, z)", "" + q.getPitch() + "," + q.getYaw() + "," + q.getRoll());

//        Gdx.app.log("targetPYR", "" + tempVector);
        fPitch = tempVector.x;
        fRoll = tempVector.z;

//        pitch = -cPitch;//fPitch - cPitch;
//        roll = -cRoll;//fRoll - cRoll;
//        yaw = -cYaw;

        boolean doingSomething = false;

        gameObject.transform.getRotation(q);
        tempVector.set(gameObject.rigidBody.getAngularVelocity());

        throttle = 0f;
        // pitch first for now ??

        Gdx.app.log("roll, pitch", "" + roll + "," + pitch);

        if (Math.abs(pitch) > 5) {
            float pitchAmount = MathUtils.sin(- pitch / 2 * MathUtils.degreesToRadians);
            gameObject.pitch(pitchAmount);
            Gdx.app.log("pitch", pitch + " (" + pitchAmount + ") + vel: " + tempVector.x);
            doingSomething = true;

        } else
        if (Math.abs(yaw) > 5) {
            float yawAmount = MathUtils.sin(- yaw / 2 * MathUtils.degreesToRadians);
            gameObject.yaw(yawAmount);
            Gdx.app.log("yaw", yaw + " (" + yawAmount + ") + vel: " + tempVector.y);
            doingSomething = true;

        } else
        if (Math.abs(roll) > 5 && Math.abs(roll) < 175 ) {
            float rollAmount = MathUtils.sin(- roll * MathUtils.degreesToRadians);
            gameObject.roll(rollAmount);
            Gdx.app.log("roll", roll + " (" + rollAmount + ")" + " vel: " + tempVector.z);
        } //else




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

    Vector3 getPYR(Quaternion q) {
        Vector3 fwd = new Vector3(0, 0, 1);
        Vector3 up = new Vector3(0, 1, 0);

        fwd.mul(q);
        up.mul(q);

//        System.out.println(String.format("q: %f, %f, %f", q.getPitch(), q.getYaw(), q.getRoll()));

//        System.out.println("fwd:" + fwd);
//        System.out.println("up:" + up);

        float pitch = (float)Math.asin(-fwd.y);
        float yaw = (float)Math.atan2(fwd.x, fwd.z);

        float cosRoll = new Vector3(0, 1, 0).dot(up);
        float sinRoll = (cosRoll * 1 - up.y) / 1;

        float roll = (float)Math.acos(cosRoll);//(float)Math.asin(sinRoll);

//        System.out.println("pitch: " + pitch * MathUtils.radiansToDegrees);
//        System.out.println("yaw: " + yaw * MathUtils.radiansToDegrees);
//        System.out.println("roll: " + roll * MathUtils.radiansToDegrees);

        return new Vector3(pitch * MathUtils.radiansToDegrees, yaw * MathUtils.radiansToDegrees, roll * MathUtils.radiansToDegrees);
    }

    void findNewTarget() {
        gameObject.transform.getTranslation(targetLocation);
//        targetLocation.add(MathUtils.random(-10, 10), MathUtils.random(-10, 10), MathUtils.random(-10, 10));
//        targetLocation.add(-8, -9, -3);
        targetLocation.add(0, 2, 0);
        Gdx.app.log("retarget", targetLocation + "!");
    }
}
