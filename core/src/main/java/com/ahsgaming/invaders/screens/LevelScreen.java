package com.ahsgaming.invaders.screens;

import com.ahsgaming.invaders.InvadersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/4/14
 * Time: 2:45 PM
 */
public class LevelScreen extends AbstractScreen {

    PerspectiveCamera cam;
    Array<GameObject> instances;
    ModelBatch modelBatch;
    Environment environment;
    CameraInputController cameraInputController;
    AssetManager assets;
    boolean loading;

    Array<GameObject> blocks;
    Array<GameObject> invaders;
    GameObject ship;
    ModelInstance space;

    Label label;
    StringBuilder stringBuilder;

    int selected = -1, selecting = -1;
    Material selectionMaterial;
    Material originalMaterial;

    Shape blockShape, invaderShape, shipShape;
    BoundingBox bounds = new BoundingBox();

    btCollisionConfiguration collisionConfiguration;
    btCollisionDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    btConstraintSolver solver;
    btDynamicsWorld collisionWorld;

    Vector3 tempVector = new Vector3();

    Vector3 gravity = new Vector3(0, 0, 0);

    DebugDrawer debugDrawer = null;

    boolean[] keysDown;

    ContactListener testContactListener;

    ModelBuilder modelBuilder;

    Image reticule;

    // collision flags
    static int PLAYER_FLAG = 2; // can an object collide with the player?

    public LevelScreen(InvadersGame game) {
        super(game);
        instances = new Array<GameObject>();
        blocks = new Array<GameObject>();
        invaders = new Array<GameObject>();
        keysDown = new boolean[255];
    }

    @Override
    public void show() {
        super.show();
        label = new Label(" ", getSkin());
        stringBuilder = new StringBuilder();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 7, 10);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        cameraInputController = new CameraInputController(cam);
        InputAdapter inputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                selecting = getObject(screenX, screenY);
                return selecting >= 0;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (selecting < 0) return false;

                if (selected == selecting) {
                    Ray ray = cam.getPickRay(screenX, screenY);
                    final float distance = -ray.origin.y / ray.direction.y;
                    Vector3 position = new Vector3(ray.direction);
                    position.scl(distance).add(ray.origin);
                    instances.get(selected).transform.setTranslation(position);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (selecting >= 0) {
                    if (selecting == getObject(screenX, screenY))
                        setSelected(selecting);
                    selecting = -1;
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                keysDown[keycode] = true;

                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                keysDown[keycode] = false;
                return true;
            }
        };
        Gdx.input.setInputProcessor(new InputMultiplexer(inputAdapter, cameraInputController));

        assets = new AssetManager();
        assets.load("ship/ship.obj", Model.class);
        assets.load("block/block.obj", Model.class);
        assets.load("invader/invader.obj", Model.class);
        assets.load("laser/laser.g3db", Model.class);
        assets.load("spacesphere/spacesphere.obj", Model.class);
        assets.load("reticule.png", Texture.class);
        loading = true;

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();

        // Create Bullet World
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        collisionWorld.setGravity(gravity);

        debugDrawer = new DebugDrawer();
        collisionWorld.setDebugDrawer(debugDrawer);
        debugDrawer.setDebugMode(1);

        testContactListener = new ContactListener() {
            @Override
            public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
                if (colObj0.userData == ship || colObj1.userData == ship) {
                    if (invaders.contains((GameObject)colObj0.userData, true)) {
                        ((GameObject)colObj0.userData).setRemove(true);
                    }
                    if (invaders.contains((GameObject)colObj1.userData, true)) {
                        ((GameObject)colObj1.userData).setRemove(true);
                    }
                }
            }
        };

    }

    GameObject createGameObject(Model model) {
        return createGameObject(model, 1);
    }

    GameObject createGameObject(Model model, int mass) {
        GameObject object = new GameObject(model, mass);
        object.calculateBoundingBox(bounds);
        shipShape = new Sphere(bounds);
        object.shape = shipShape;
        instances.add(object);
        collisionWorld.addRigidBody(object.rigidBody);
        object.rigidBody.userData = object;
        object.rigidBody.setContactCallbackFlag(PLAYER_FLAG);
        object.rigidBody.setContactCallbackFilter(PLAYER_FLAG);
        return object;
    }

    void doneLoading() {

        reticule = new Image(assets.get("reticule.png", Texture.class));
        reticule.setPosition((stage.getWidth() - reticule.getWidth()) / 2, (stage.getHeight() - reticule.getHeight()) / 2);
        reticule.setColor(0.2f, 0.4f, 1.0f, 0.8f);
        stage.addActor(reticule);

        ship = createGameObject(assets.get("ship/ship.obj", Model.class));
        ship.rotate(0, 180, 180).translate(0, 0, 6);
        ship.rigidBody.forceActivationState(Collision.DISABLE_DEACTIVATION);
        ship.rigidBody.setDamping(0.1f, 0.1f);

        GameObject laser = createGameObject(assets.get("laser/laser.g3db", Model.class), 1);
        laser.translate(0, 2, 0);

        Model blockModel = assets.get("block/block.obj", Model.class);
        for (int x = -5; x < 5; x += 2) {
            GameObject block = createGameObject(blockModel);
            block.translate(x, 0, 3);
            blocks.add(block);
        }

        Model invaderModel = assets.get("invader/invader.obj", Model.class);
        for (int x = -5; x < 5; x += 2) {
            for (int z = -8; z <= 0; z += 2) {
                GameObject invader = createGameObject(invaderModel);
                invader.translate(x, 0, z);
                invaders.add(invader);
            }
        }

        space = new ModelInstance(assets.get("spacesphere/spacesphere.obj", Model.class));

        loading = false;
    }

    @Override
    public void render(float delta) {
        if (loading && assets.update())
            doneLoading();
        cameraInputController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!loading) {
            Quaternion q = new Quaternion();
            ship.transform.getRotation(q);

            if (keysDown[Input.Keys.SPACE]) {
                ship.rigidBody.setGravity(new Vector3(0, 0, 10).mul(q));
            } else {
                ship.rigidBody.setGravity(new Vector3(0, 0, 0));
            }

            if (keysDown[Input.Keys.W] && !keysDown[Input.Keys.S]) {
                ship.rigidBody.applyTorque(new Vector3(1, 0, 0).mul(q));
            } else if (keysDown[Input.Keys.S] && !keysDown[Input.Keys.W]) {
                ship.rigidBody.applyTorque(new Vector3(-1, 0, 0).mul(q));
            }

            if (keysDown[Input.Keys.A] && !keysDown[Input.Keys.D]) {
                ship.rigidBody.applyTorque(new Vector3(0, 1, 0).mul(q));
            } else if (keysDown[Input.Keys.D] && !keysDown[Input.Keys.A]) {
                ship.rigidBody.applyTorque(new Vector3(0, -1, 0).mul(q));
            }

            if (keysDown[Input.Keys.Q] && !keysDown[Input.Keys.E]) {
                ship.rigidBody.applyTorque(new Vector3(0, 0, -1).mul(q));
            } else if (keysDown[Input.Keys.E] && !keysDown[Input.Keys.Q]) {
                ship.rigidBody.applyTorque(new Vector3(0, 0, 1).mul(q));
            }

            ship.transform.getTranslation(tempVector);

            cam.position.set(new Vector3(tempVector).add(new Vector3(0, 2, -5).mul(q)));
            cam.lookAt(new Vector3(tempVector).add(new Vector3(0, 1, 0).mul(q)));

            // lock the camera up to the ship's up
            Vector3 yRotation = new Vector3(0, 1, 0).mul(q);

            cam.up.set(yRotation);

            cam.update();

            space.transform.setToTranslation(tempVector);
        }

        for (int i = 0; i < instances.size; i++) {
            GameObject instance = instances.get(i);
            if (instance.isRemove()) {
                instances.removeIndex(i);
                blocks.removeValue(instance, true);
                invaders.removeValue(instance, true);
                collisionWorld.removeRigidBody(instance.rigidBody);
                instance.dispose();
            }
        }

        collisionWorld.stepSimulation(delta, 5);

        int visibleCount = 0;
        modelBatch.begin(cam);
        for (final GameObject instance : instances) {
            instance.update();
            if (instance.isVisible(cam)) {
                modelBatch.render(instance, environment);
                visibleCount++;
            }
        }
        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.end();

        if (debugDrawer != null) {
            debugDrawer.lineRenderer.setProjectionMatrix(cam.combined);
            debugDrawer.begin();
            collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        stringBuilder.append(" Selected: ").append(selected);
        label.setText(stringBuilder);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.addActor(label);
    }

    @Override
    public void dispose() {
        super.dispose();
        modelBatch.dispose();
        instances.clear();
        assets.dispose();

        collisionWorld.dispose();
        solver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfiguration.dispose();

        for (GameObject gameObject: instances) {
            gameObject.dispose();
        }
    }

    public void setSelected(int value) {
        if (selected == value) return;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            mat.clear();
            mat.set(originalMaterial);
        }
        selected = value;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            mat.set(selectionMaterial);
        }
    }

    public int getObject(int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < instances.size; i++) {
            if (!(instances.get(i) instanceof GameObject)) continue;
            final float dist2 = ((GameObject)instances.get(i)).intersects(ray);
            if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }

    public interface Shape {
        public abstract boolean isVisible(Matrix4 transform, Camera cam);
        /**
         * @param ray
         * @return -1 if no intersection, otherwise, the squared distance between the
         * center of this object and the point on the ray closest to this object
         */
        public abstract float intersects(Matrix4 transform, Ray ray);
    }

    public static class GameObject extends ModelInstance {
        public Shape shape;

        public btBoxShape boxShape;
        public btRigidBodyConstructionInfo info;
        public btDefaultMotionState motionState;
        public btRigidBody rigidBody;
        public BoundingBox bounds;

        boolean remove;

        int mass = 1;

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
            info = new btRigidBodyConstructionInfo(mass, null, boxShape, tempVector);
            motionState = new btDefaultMotionState();
            rigidBody = new btRigidBody(info);
            rigidBody.setMotionState(motionState);
        }

        public void update() {
            motionState.getWorldTransform(transform);
        }

        public void dispose() {
            boxShape.dispose();
            info.dispose();
            motionState.dispose();
            rigidBody.dispose();
        }

        boolean isVisible(Camera cam) {
            return shape == null ? false : shape.isVisible(transform, cam);
        }

        public float intersects(Ray ray) {
            return shape == null ? -1f : shape.intersects(transform, ray);
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

    public static abstract class BaseShape implements Shape {
        protected final static Vector3 position = new Vector3();
        public final Vector3 center = new Vector3();
        public final Vector3 dimensions = new Vector3();

        public BaseShape(BoundingBox bounds) {
            center.set(bounds.getCenter());
            dimensions.set(bounds.getDimensions());
        }
    }

    public static class Sphere extends BaseShape {
        public float radius;

        public Sphere(BoundingBox bounds) {
            super(bounds);
            radius = bounds.getDimensions().len() / 2f;
        }

        @Override
        public boolean isVisible(Matrix4 transform, Camera cam) {
            return cam.frustum.sphereInFrustum(transform.getTranslation(position).add(center), radius);
        }

        @Override
        public float intersects(Matrix4 transform, Ray ray) {
            transform.getTranslation(position).add(center);
            final float len = ray.direction.dot(position.x - ray.origin.x, position.y - ray.origin.y, position.z - ray.origin.z);
            if (len < 0f) return -1;
            float dist2 = position.dst2(ray.origin.x + ray.direction.x * len, ray.origin.y + ray.direction.y * len, ray.origin.z + ray.direction.z * len);
            return (dist2 <= radius * radius) ? dist2 : -1;
        }
    }
}
