package com.ahsgaming.invaders;

import com.ahsgaming.invaders.behaviors.CollideBehavior;
import com.ahsgaming.invaders.behaviors.DamageBehavior;
import com.ahsgaming.invaders.behaviors.UpdateBehavior;
import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Array;

/**
* invaders
* (c) 2013 Jami Couch
* User: jami
* Date: 4/7/14
* Time: 5:00 PM
*/
public class GameObject extends ModelInstance {

    public btBoxShape boxShape;
    public btRigidBody.btRigidBodyConstructionInfo info;
    public btDefaultMotionState motionState;
    public btRigidBody rigidBody;
    public BoundingBox bounds;

    boolean remove;

    int mass = 1;
    public float thrust = 10, rollThrust = 0.3f, pitchThrust = 0.5f, yawThrust = 0.5f;

    static Vector3 tempVector = new Vector3();
    static Quaternion q = new Quaternion();

    public CollideBehavior collideBehavior;
    public DamageBehavior damageBehavior;
    public UpdateBehavior updateBehavior;

    public Array<Weapon> weapons = new Array<Weapon>();
    public int curWeapon = -1;

    public GameObject(Model model) {
        this(model, 1);
    }

    public GameObject(Model model, int mass) {
        super(model);
        this.mass = mass;
        init();
    }

    private void init() {
        Vector3 tempVector = new Vector3();
        bounds = new BoundingBox();
        calculateBoundingBox(bounds);

        // physics stuff
        boxShape = new btBoxShape(bounds.getDimensions().scl(0.5f));
        boxShape.calculateLocalInertia(mass, tempVector);
        info = new btRigidBody.btRigidBodyConstructionInfo(mass, null, boxShape, tempVector);
        motionState = new btDefaultMotionState();
        rigidBody = new btRigidBody(info);
        rigidBody.setMotionState(motionState);

        rigidBody.setDamping(0.2f, 0.5f);
    }

    public void update(float delta, LevelScreen levelScreen) {
        motionState.getWorldTransform(transform);

        for (Weapon weapon: weapons) {
            weapon.update(delta);
        }

        if (updateBehavior != null) {
            updateBehavior.update(delta, levelScreen);
        }
    }

    public void dispose() {
        boxShape.dispose();
        info.dispose();
        motionState.dispose();
        rigidBody.dispose();
    }

    public void fire(LevelScreen screen) {
        if (curWeapon >= 0 && curWeapon < weapons.size) {
            weapons.get(curWeapon).fire(screen);
        }
    }

    public void selectWeapon(int weapon) {
        if (weapon >= 0 && weapon < weapons.size) {
            curWeapon = weapon;
        }
    }

    public void pitch(float amount) {
        transform.getRotation(q);
        q.nor();
        tempVector.set(amount * pitchThrust, 0, 0).mul(q);
        rigidBody.applyTorque(tempVector);
    }

    public void roll(float amount) {
        transform.getRotation(q);
        tempVector.set(0, 0, amount * rollThrust).mul(q);
        rigidBody.applyTorque(tempVector);
    }

    public void yaw(float amount) {
        transform.getRotation(q);
        q.nor();
        tempVector.set(0, amount * yawThrust, 0).mul(q);
        rigidBody.applyTorque(tempVector);
    }

    public GameObject rotate(Quaternion quaternion) {
        rotate(quaternion.getPitch(), quaternion.getYaw(), quaternion.getRoll());
        return this;
    }

    public GameObject rotate(Vector3 vector) {
        rotate(vector.x, vector.y, vector.z);
        return this;
    }

    public GameObject rotate(float x, float y, float z) {
        Matrix4 tr = new Matrix4();
        tr.idt();
        Quaternion quat = new Quaternion();
        quat.setEulerAngles(y, x, z);
        tr.set(quat);
        rigidBody.setCenterOfMassTransform(tr);
        return this;
    }

    public GameObject translate(Vector3 vector) {
        rigidBody.translate(vector);
        return this;
    }

    public GameObject translate(float x, float y, float z) {
        rigidBody.translate(new Vector3(x, y, z));
        return this;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public void onCollide(GameObject other) {
        if (collideBehavior != null) {
            collideBehavior.onCollide(other);
        }
    }

    public void takeDamage(float amount) {
        if (damageBehavior != null) {
            damageBehavior.takeDamage(amount);
        }
    }
}
