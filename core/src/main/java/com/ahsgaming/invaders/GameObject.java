package com.ahsgaming.invaders;

import com.ahsgaming.invaders.screens.LevelScreen;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

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
    int maxSpeed = 10;
    public float throttle = 0, thrust = 10;

    static Vector3 tempVector = new Vector3();
    static Quaternion q = new Quaternion();

    float fireTimer = 1;
    float fireCountdown = 1;

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
    }

    public void update(float delta) {
        motionState.getWorldTransform(transform);
        tempVector.set(rigidBody.getLinearVelocity());
        float speed = tempVector.len();
        if (speed > 10) {
            tempVector.scl(10/speed);
            rigidBody.setLinearVelocity(tempVector);
        }

        transform.getRotation(q);

        rigidBody.setGravity(tempVector.set(0, 0, throttle * thrust).mul(q));

        fireCountdown -= delta;
        if (fireCountdown < 0)
            fireCountdown = 0;
    }

    public void dispose() {
        boxShape.dispose();
        info.dispose();
        motionState.dispose();
        rigidBody.dispose();
    }

    public void fire(LevelScreen screen) {
        if (fireCountdown <= 0) {
            fireCountdown = fireTimer;
            GameObject bullet = screen.createGameObject(screen.assets.get("laser/laser.g3db", Model.class), 10);
            transform.getTranslation(tempVector);
            transform.getRotation(q);
            bullet.rotate(q).translate(tempVector.add(new Vector3(0, 0, 2).mul(q)));
            bullet.maxSpeed = 100;
            bullet.thrust = 100;
            bullet.throttle = 1;
            // fire!
        }
    }

    public GameObject rotate(Quaternion quaternion) {
        rotate(quaternion.getYaw(), quaternion.getPitch(), quaternion.getRoll());
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
        quat.setEulerAngles(x, y, z);
        tr.set(quat.x, quat.y, quat.z, quat.w);
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
}