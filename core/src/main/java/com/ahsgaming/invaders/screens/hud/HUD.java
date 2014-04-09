package com.ahsgaming.invaders.screens.hud;

import com.ahsgaming.invaders.GameObject;
import com.ahsgaming.invaders.Weapon;
import com.ahsgaming.invaders.behaviors.PlayerShipBehavior;
import com.ahsgaming.invaders.behaviors.ShipBehavior;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

/**
 * invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 4/8/14
 * Time: 3:53 PM
 */
public class HUD extends Group {

    static int WP_SELECTED = 0, WP_UNSELECTED = 1, WP_OVERHEAT = 2;

    GameObject gameObject;

    Group lowerRight, lowerLeft;
    Image laserIcon, missileIcon, hullIcon, shieldIcon, selectedIcon, orientator, orient;
    PillGroup laserPills, missilePills, hullPills, shieldPills;

    Texture[][] weaponPillTextures;

    public HUD() {
        super();

        weaponPillTextures = new Texture[WP_OVERHEAT + 1][2];
        weaponPillTextures[WP_SELECTED][0] = new Texture("hud/hud-pill-yellow-empty.png");
        weaponPillTextures[WP_SELECTED][1] = new Texture("hud/hud-pill-yellow.png");
        weaponPillTextures[WP_UNSELECTED][0] = new Texture("hud/hud-pill-green-empty.png");
        weaponPillTextures[WP_UNSELECTED][1] = new Texture("hud/hud-pill-green.png");
        weaponPillTextures[WP_OVERHEAT][0] = new Texture("hud/hud-pill-red-empty.png");
        weaponPillTextures[WP_OVERHEAT][1] = new Texture("hud/hud-pill-red.png");

        lowerRight = new Group();
        lowerLeft = new Group();

        addActor(lowerRight);
        addActor(lowerLeft);

        // RIGHT PANEL
        Image lrPanel = new Image(new Texture("hud/hud-panel-lowerright.png"));
        lowerRight.addActor(lrPanel);
        lowerRight.setPosition(Gdx.graphics.getWidth() - lrPanel.getWidth(), 0);

        hullIcon = new Image(new Texture("hud/hud-icon-hull.png"));
        lowerRight.addActor(hullIcon);
        hullIcon.setPosition(42, 53);

        hullPills = new PillGroup(new Texture("hud/hud-pill-green-empty.png"), new Texture("hud/hud-pill-green.png"), 10, 300, (int)hullIcon.getHeight());
        lowerRight.addActor(hullPills);
        hullPills.setPosition(144, 53);

        shieldIcon = new Image(new Texture("hud/hud-icon-shield.png"));
        lowerRight.addActor(shieldIcon);
        shieldIcon.setPosition(42, 4);

        shieldPills = new PillGroup(new Texture("hud/hud-pill-blue-empty.png"), new Texture("hud/hud-pill-blue.png"), 10, 300, (int)hullIcon.getHeight());
        lowerRight.addActor(shieldPills);
        shieldPills.setPosition(144, 4);

        // LEFT PANEL
        Image llPanel = new Image(new Texture("hud/hud-panel-lowerleft.png"));
        lowerLeft.addActor(llPanel);

        laserIcon = new Image(new Texture("hud/hud-icon-laserred.png"));
        lowerLeft.addActor(laserIcon);
        laserIcon.setPosition(12, 53);

        laserPills = new PillGroup(new Texture("hud/hud-pill-green-empty.png"), new Texture("hud/hud-pill-green.png"), 10, 300, (int)hullIcon.getHeight());
        lowerLeft.addActor(laserPills);
        laserPills.setPosition(114, 53);

        missileIcon = new Image(new Texture("hud/hud-icon-missile1.png"));
        lowerLeft.addActor(missileIcon);
        missileIcon.setPosition(12, 4);

        missilePills = new PillGroup(new Texture("hud/hud-pill-green-empty.png"), new Texture("hud/hud-pill-green.png"), 10, 300, (int)hullIcon.getHeight());
        lowerLeft.addActor(missilePills);
        missilePills.setPosition(114, 4);

        selectedIcon = new Image(new Texture("hud/hud-selector-yellow.png"));
        selectedIcon.setPosition(12, 53);

        orientator = new Image(new Texture("orientator.png"));
        orientator.setPosition((Gdx.graphics.getWidth() - orientator.getWidth()) / 2, (Gdx.graphics.getHeight() - orientator.getHeight()) / 2);
        addActor(orientator);
        orientator.setColor(new Color(1, 1, 1, 0.5f));

        orient = new Image(new Texture("orientation.png"));
        orient.setPosition((Gdx.graphics.getWidth() - orient.getWidth()) / 2, (Gdx.graphics.getHeight() - orient.getHeight()) / 2);
        addActor(orient);
        orient.setColor(new Color(1, 1, 1, 0.5f));
    }

    public void setGameObject(GameObject object) {
        gameObject = object;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // TODO update

        if (gameObject != null) {
            if (gameObject.weapons.size > 0) {
                updateWeaponPill(gameObject.weapons.get(0), laserPills, (gameObject.curWeapon == 0));
            }

            if (gameObject.weapons.size > 1) {
                updateWeaponPill(gameObject.weapons.get(1), missilePills, (gameObject.curWeapon == 1));
            }

            if (gameObject.curWeapon >= 0) {
                if (!selectedIcon.hasParent()) lowerLeft.addActor(selectedIcon);
                if (!(selectedIcon.getActions().size > 0) && selectedIcon.getY() != 53 - 49 * gameObject.curWeapon) {
                    selectedIcon.addAction(Actions.moveTo(12, 53 - 49 * gameObject.curWeapon, 0.2f));
                }
            }

            hullPills.update(gameObject.damageBehavior.getCurHP() / gameObject.damageBehavior.getMaxHP());
            if (gameObject.damageBehavior.getMaxSP() > 0)
                shieldPills.update(gameObject.damageBehavior.getCurSP() / gameObject.damageBehavior.getMaxSP());

            // orientator (move to HUD?)
            Vector2 orientPos = new Vector2(orientator.getX() + orientator.getWidth() * 0.5f, orientator.getY() + orientator.getHeight() * 0.5f);
            Vector2 offset = new Vector2(((PlayerShipBehavior)gameObject.updateBehavior).mouseOrientation);
            offset.scl(orientator.getWidth() * 0.4f);
            orientPos.sub(offset);
            orient.setPosition(orientPos.x - orient.getWidth() * 0.5f, orientPos.y - orient.getHeight() * 0.5f);
        }
    }

    public void updateWeaponPill(Weapon weapon, PillGroup pillGroup, boolean selected) {
        pillGroup.setPills(weapon.maxAmmo);
        pillGroup.update((float) weapon.curAmmo / weapon.maxAmmo);
        if (weapon.curAmmo == 0) {
            pillGroup.setTextures(weaponPillTextures[WP_OVERHEAT][0], weaponPillTextures[WP_OVERHEAT][1]);
        } else {
            if (selected) {
                pillGroup.setTextures(weaponPillTextures[WP_SELECTED][0], weaponPillTextures[WP_SELECTED][1]);
            } else {
                pillGroup.setTextures(weaponPillTextures[WP_UNSELECTED][0], weaponPillTextures[WP_UNSELECTED][1]);
            }
        }
    }

    public static class PillGroup extends Group {
        Texture empty, full;

        int numPills = 10;
        float fraction = 0;

        public PillGroup(Texture empty, Texture full, int num, int width, int height) {
            super();
            this.empty = empty;
            this.full = full;
            this.numPills = num;
            setSize(width, height);
            setPills(numPills);
        }

        public void update(float fraction) {
            this.fraction = fraction;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float size = getWidth() / numPills;
            for (int i = 0; i < numPills; i++) {
                Texture cur = null;
                if (i + 1 <= fraction * numPills) {
                    cur = full;
                } else {
                    cur = empty;
                }
                batch.draw(cur, getX() + i * size, getY(), 0, 0, size - 2, getHeight(), 1, 1, 0, 0, 0, 44, 44, false, false);
            }
        }

        public void setPills(int num) {
            this.numPills = num;
        }

        public void setTextures(Texture empty, Texture full) {
            this.empty = empty;
            this.full = full;
        }
    }
}
