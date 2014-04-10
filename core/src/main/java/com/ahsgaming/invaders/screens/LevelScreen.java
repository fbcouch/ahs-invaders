package com.ahsgaming.invaders.screens;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.InvadersGame;
import com.ahsgaming.invaders.Weapon;
import com.ahsgaming.invaders.behaviors.BasicAIShipBehavior;
import com.ahsgaming.invaders.behaviors.PlayerShipBehavior;
import com.ahsgaming.invaders.behaviors.ShipBehavior;
import com.ahsgaming.invaders.screens.hud.HUD;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.scenes.scene2d.Group;
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
    public Array<GameObject> instances;
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

    ContactListener contactListener;

    Image reticule;
    HUD hud;

    boolean debugRender = false;

    Vector3 camPos = new Vector3(0, 2, -7), camTarget = new Vector3(0, 1.2f, 0);
    public int scrollAmount = 0;
    public int maxScroll = 20;

    // collision flags
    static int PLAYER_FLAG = 2; // can an object collide with the player?

    public LevelScreen(InvadersGame game) {
        super(game);
        instances = new Array<GameObject>();
        blocks = new Array<GameObject>();
        invaders = new Array<GameObject>();
    }

    @Override
    public void show() {
        super.show();
        hud = new HUD();
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
            public boolean scrolled(int amount) {

                scrollAmount -= amount;
                if (scrollAmount > maxScroll) scrollAmount = maxScroll;
                if (scrollAmount < 0) scrollAmount = 0;
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.NUM_0) {
                    debugRender = !debugRender;
                    return true;
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);

        assets = new AssetManager();
        assets.load("ship/ship.obj", Model.class);
        assets.load("block/block.obj", Model.class);
        assets.load("invader/invader.obj", Model.class);
        assets.load("tie/tieblend.g3db", Model.class);
        assets.load("bomber/bomber.g3db", Model.class);
        assets.load("laser/laser.g3db", Model.class);
        assets.load("spacesphere/spacesphere.obj", Model.class);
        assets.load("missile/missile.g3db", Model.class);
        assets.load("reticule.png", Texture.class);
        assets.load("orientator.png", Texture.class);
        assets.load("orientation.png", Texture.class);
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
                if (colObj0.userData != null && colObj1.userData != null) {
                    ((GameObject)colObj0.userData).onCollide((GameObject)colObj1.userData);
                    ((GameObject)colObj1.userData).onCollide((GameObject)colObj0.userData);
                }
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
//        stage.addActor(reticule);

        ship = createGameObject(assets.get("ship/ship.obj", Model.class));
        ship.rotate(0, 180, 0).translate(0, 0, 6);
        ShipBehavior shipBehavior = new PlayerShipBehavior(ship);
        ship.damageBehavior = shipBehavior;
        ship.collideBehavior = shipBehavior;
        ship.updateBehavior = shipBehavior;

        Weapon laser = new Weapon.BasicLaser(ship, assets.get("laser/laser.g3db", Model.class));
        laser.firePoints.add(new Vector3(0.5f, 0, 2f));
        laser.firePoints.add(new Vector3(-0.5f, 0, 2f));
        ship.weapons.add(laser);

        Weapon missile = new Weapon.BasicMissile(ship, assets.get("missile/missile.g3db", Model.class));
        missile.firePoints.add(new Vector3(0, 0, 2f));
        ship.weapons.add(missile);

        ship.curWeapon = 0;

//        Model blockModel = assets.get("block/block.obj", Model.class);
//        for (int x = -5; x < 5; x += 2) {
//            GameObject block = createGameObject(blockModel);
//            block.translate(x, 0, 3);
//            blocks.add(block);
//            shipBehavior = new ShipBehavior(block);
//            block.damageBehavior = shipBehavior;
//            block.collideBehavior = shipBehavior;
//            block.updateBehavior = shipBehavior;
//        }

        Model invaderModel = assets.get("ship/ship.obj", Model.class);
//        for (int x = -5; x < 5; x += 10) {
//            for (int z = -8; z <= 0; z += 2) {
                GameObject invader = createGameObject(invaderModel, 1);
                invader.rotate(90, 90, 0);
                invader.translate(0, 0, 0);
                invaders.add(invader);
                shipBehavior = new BasicAIShipBehavior(invader);
                invader.damageBehavior = shipBehavior;
                invader.collideBehavior = shipBehavior;
                invader.updateBehavior = shipBehavior;
                invader.rigidBody.setAngularVelocity(new Vector3(0, 0f, 0));
//            }
//        }

        space = new ModelInstance(assets.get("spacesphere/spacesphere.obj", Model.class));
        space.transform.scale(3, 3, 3);

        loading = false;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();

        if (loading && assets.update()) {
            doneLoading();
            hud.setGameObject(ship);
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!loading) {
            Quaternion q = new Quaternion();
            ship.transform.getRotation(q);

            if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
                camPos.set(0, 2, -7);
                camTarget.set(0, 1.2f, 0);
                reticule.remove();
            } else if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
                camPos.set(0, 0, 0);
                camTarget.set(0, 0, 1);
                stage.addActor(reticule);
            } else if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
                camPos.set(0, 2, 7);
                camTarget.set(0, 0, 0);
                reticule.remove();
            }

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
            instance.update(delta, this);
            modelBatch.render(instance, environment);
            if (instance.updateBehavior instanceof BasicAIShipBehavior) {
                modelBatch.render(((BasicAIShipBehavior)instance.updateBehavior).targetBox, environment);
            }
            visibleCount++;
        }
        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.end();

        if (debugDrawer != null) {
            debugDrawer.lineRenderer.setProjectionMatrix(cam.combined);
            debugDrawer.begin();
            if (debugRender) collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        if (!loading && ship.rigidBody != null) {
            // for some strange reason, calling ship.rigidBody.getLinearVelocity() here causes a segfault sometimes
//            stringBuilder.append(" Speed: ").append(ship.rigidBody.getLinearVelocity().len());
            stringBuilder.append(" Speed: ").append(((PlayerShipBehavior)ship.updateBehavior).throttle * ((PlayerShipBehavior)ship.updateBehavior).maxSpeed);
        }

        label.setText(stringBuilder);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.addActor(hud);
        stage.addActor(label);
        label.setPosition(0, height - label.getHeight());
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
