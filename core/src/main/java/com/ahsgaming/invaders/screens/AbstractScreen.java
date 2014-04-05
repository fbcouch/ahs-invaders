package com.ahsgaming.invaders.screens;

import com.ahsgaming.invaders.InvadersGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * towers-of-invaders
 * (c) 2013 Jami Couch
 * User: jami
 * Date: 3/31/14
 * Time: 1:19 PM
 */
public class AbstractScreen implements Screen {

    final InvadersGame game;
    Stage stage;
    Skin skin;
    BitmapFont fontSmall;
    BitmapFont fontMed;
    BitmapFont fontLarge;

    public AbstractScreen(InvadersGame game) {
        this.game = game;
        this.stage = new Stage();
        getSkin();
    }

    public Skin getSkin() {
        if (skin == null) {
            skin = new Skin(Gdx.files.internal("uiskin.json"));
            getSmallFont();
            getMedFont();
            getLargeFont();
        }
        return skin;
    }

    public BitmapFont getSmallFont() {
        if (fontSmall == null) {
            fontSmall = getSkin().getFont("small-font");
        }
        return fontSmall;
    }

    public BitmapFont getMedFont() {
        if (fontMed == null) {
            fontMed = getSkin().getFont("medium-font");
        }
        return fontMed;
    }

    public BitmapFont getLargeFont() {
        if (fontLarge == null) {
            fontLarge = getSkin().getFont("large-font");
        }
        return fontLarge;
    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.clear();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
