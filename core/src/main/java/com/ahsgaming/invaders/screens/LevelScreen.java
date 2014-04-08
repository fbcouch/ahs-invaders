package com.ahsgaming.invaders.screens;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.InvadersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
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
    public AssetManager assets;
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

    ContactListener contactListener;

    ModelBuilder modelBuilder;

    Image reticule;

    Vector3 camPos = new Vector3(0, 2, -7), camTarget = new Vector3(0, 1.2f, 0);
    int scrollAmount = 0, maxScroll = 20;

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

        InputAdapter inputAdapter = new InputAdapter() {

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

            @Override
            public boolean scrolled(int amount) {

                scrollAmount += amount;
                if (scrollAmount > maxScroll) scrollAmount = maxScroll;
                if (scrollAmount < 0) scrollAmount = 0;
                return true;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);

        assets = new AssetManager();
        assets.load("ship/ship.obj", Model.class);
        assets.load("block/block.obj", Model.class);
        assets.load("invader/invader.obj", Model.class);
        assets.load("tie/tieblend.g3db", Model.class);
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

        contactListener = new ContactListener() {
            @Override
            public void onContactStarted(btCollisionObject colObj0, boolean match0, btCollisionObject colObj1, boolean match1) {
//                if (colObj0.userData == ship || colObj1.userData == ship) {
//                    if (invaders.contains((GameObject)colObj0.userData, true)) {
//                        ((GameObject)colObj0.userData).setRemove(true);
//                    }
//                    if (invaders.contains((GameObject)colObj1.userData, true)) {
//                        ((GameObject)colObj1.userData).setRemove(true);
//                    }
//                }
            }
        };

        catchCursor();
    }

    public GameObject createGameObject(Model model) {
        return createGameObject(model, 1);
    }

    public GameObject createGameObject(Model model, int mass) {
        GameObject object = new GameObject(model, mass);
        object.calculateBoundingBox(bounds);
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
        ship.rigidBody.setDamping(0.2f, 0.2f);
//        ship.throttle = 1;


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
        space.transform.scale(3, 3, 3);

        loading = false;
    }

    @Override
    public void render(float delta) {
        if (loading && assets.update())
            doneLoading();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!loading) {
            Quaternion q = new Quaternion();
            ship.transform.getRotation(q);

            if (keysDown[Input.Keys.NUM_1]) {
                camPos.set(0, 2, -7);
                camTarget.set(0, 1.2f, 0);
            } else if (keysDown[Input.Keys.NUM_2]) {
                camPos.set(0, 0, 0);
                camTarget.set(0, 0, 1);
            } else if (keysDown[Input.Keys.NUM_3]) {
                camPos.set(0, 2, 7);
                camTarget.set(0, 0, 0);
            }

            if (keysDown[Input.Keys.SPACE] || Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                // TODO fire weapon!
                ship.fire(this);
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

            if (keysDown[Input.Keys.ESCAPE])
                Gdx.app.exit();

            Gdx.input.setCursorPosition(
                    (Gdx.input.getX() < 0 ? 0 : (Gdx.input.getX() > Gdx.graphics.getWidth() ? Gdx.graphics.getWidth() : Gdx.input.getX())),
                    (Gdx.input.getY() < 0 ? 0 : (Gdx.input.getY() > Gdx.graphics.getHeight() ? Gdx.graphics.getHeight() : Gdx.input.getY()))
            );

            float x = (Gdx.graphics.getWidth() * 0.5f - Gdx.input.getX()) / (Gdx.graphics.getWidth() * 0.5f);

            if (Math.abs(x) > 0.1f)
                ship.roll(- x);

            float y = (Gdx.graphics.getHeight() * 0.5f - Gdx.input.getY()) / (Gdx.graphics.getHeight() * 0.5f);

            if (Math.abs(y) > 0.1f)
                ship.pitch(y);

            ship.throttle = scrollAmount / maxScroll;

            ship.transform.getTranslation(tempVector);

            cam.position.set(new Vector3(tempVector).add(new Vector3(camPos).mul(q)));
            cam.lookAt(new Vector3(tempVector).add(new Vector3(camTarget).mul(q)));

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
            instance.update(delta);
            modelBatch.render(instance, environment);
            visibleCount++;
        }
        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.end();

        if (debugDrawer != null) {
            debugDrawer.lineRenderer.setProjectionMatrix(cam.combined);
            debugDrawer.begin();
//            collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        if (!loading)
            stringBuilder.append(" Speed: ").append(ship.rigidBody.getLinearVelocity().len());

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

    void catchCursor() {
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    @Override
    public void resume() {
        super.resume();

        catchCursor();
    }
}
