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
    Quaternion target = new Quaternion();
    boolean retarget = true;

    static Vector3 currentForward = new Vector3(), currentUp = new Vector3();
    static Vector3 vectorToTarget = new Vector3(), targetUp = new Vector3();
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
        currentUp = new Vector3(0, 1, 0).mul(q);

        gameObject.transform.getTranslation(tempVector);
        vectorToTarget.set(targetLocation).sub(tempVector).nor();
        targetUp = new Vector3(currentUp);
        tempVector.set(targetUp).crs(vectorToTarget).nor();
        targetUp.set(tempVector).crs(vectorToTarget).scl(-1).nor();

        target.setFromCross(currentForward, vectorToTarget).mul(q);

        float yaw = target.getYaw() - q.getYaw();
        float pitch = target.getPitch() - q.getPitch();

        gameObject.yaw(MathUtils.sinDeg(yaw));
        gameObject.pitch(MathUtils.sinDeg(pitch));

        if (Math.abs(pitch) < 5 && Math.abs(yaw) < 5) {
            throttle = 1f;
        } else {
            throttle = 0.33f;
        }

        gameObject.transform.getTranslation(tempVector);

        if (gameObject.bounds.contains(tempVector.sub(targetLocation).scl(-1))) {
            retarget = true;
        }

        super.update(delta, levelScreen);
    }

    void findNewTarget() {
        gameObject.transform.getTranslation(targetLocation);
        targetLocation.add(MathUtils.random(-10, 10), MathUtils.random(-10, 10), MathUtils.random(-10, 10));
        Gdx.app.log("retarget", targetLocation + "!");
    }
}
