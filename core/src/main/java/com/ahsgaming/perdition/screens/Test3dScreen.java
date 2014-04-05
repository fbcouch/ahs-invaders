package com.ahsgaming.perdition.screens;

import com.ahsgaming.perdition.ToPGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

/**
 * towers-of-perdition
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/4/14
 * Time: 2:45 PM
 */
public class Test3dScreen extends AbstractScreen {

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

    public Test3dScreen(ToPGame game) {
        super(game);
        instances = new Array<GameObject>();
        blocks = new Array<GameObject>();
        invaders = new Array<GameObject>();
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
        };
        Gdx.input.setInputProcessor(new InputMultiplexer(inputAdapter, cameraInputController));

        assets = new AssetManager();
        assets.load("ship/ship.obj", Model.class);
        assets.load("block/block.obj", Model.class);
        assets.load("invader/invader.obj", Model.class);
        assets.load("spacesphere/spacesphere.obj", Model.class);
        loading = true;

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();
    }

    void doneLoading() {
        ship = new GameObject(assets.get("ship/ship.obj", Model.class));
        ship.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f);
        ship.calculateBoundingBox(bounds);
        shipShape = new Sphere(bounds);
        ship.shape = shipShape;
        instances.add(ship);

        Model blockModel = assets.get("block/block.obj", Model.class);
        for (int x = -5; x < 5; x += 2) {
            GameObject block = new GameObject(blockModel);
            if (blockShape == null) {
                block.calculateBoundingBox(bounds);
                blockShape = new Sphere(bounds);
            }
            block.transform.setToTranslation(x, 0, 3f);
            block.shape = blockShape;
            blocks.add(block);
            instances.add(block);
        }

        Model invaderModel = assets.get("invader/invader.obj", Model.class);
        for (int x = -5; x < 5; x += 2) {
            for (int z = -8; z <= 0; z += 2) {
                GameObject invader = new GameObject(invaderModel);
                if (invaderShape == null) {
                    invader.calculateBoundingBox(bounds);
                    invaderShape = new Sphere(bounds);
                }
                invader.transform.setToTranslation(x, 0, z);
                invader.shape = invaderShape;
                blocks.add(invader);
                instances.add(invader);
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

        int visibleCount = 0;
        modelBatch.begin(cam);
        for (final GameObject instance : instances) {
            if (instance.isVisible(cam)) {
                modelBatch.render(instance, environment);
                visibleCount++;
            }
        }
        if (space != null) {
            modelBatch.render(space);
        }
        modelBatch.end();

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
            final float dist2 = instances.get(i).intersects(ray);
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

        public GameObject(Model model) {
            super(model);
        }

        boolean isVisible(Camera cam) {
            return shape == null ? false : shape.isVisible(transform, cam);
        }

        public float intersects(Ray ray) {
            return shape == null ? -1f : shape.intersects(transform, ray);
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
