package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.frightnight.shaders.MenuBackground;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// abstract menu screen to derive from


public class MenuScreen implements Screen {

    protected Main game;
    protected Viewport viewport;
    protected ControllerMenuStage stage;      // from gdx-controllers-utils
    protected Skin skin;
    private MenuBackground background;


    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new ScreenViewport();

        skin = new Skin(Gdx.files.internal("skin/fright/fright.json"));
        stage = new ControllerMenuStage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);
        game.controllerToInputAdapter.setInputProcessor(stage); // forward controller input to stage

        background = new MenuBackground();

    }

    protected void playSelectNoise() {
        game.playSelectSound();
    }

    // override this!
    protected void rebuild() {

    }

    @Override
    public void render(float delta) {
        //ScreenUtils.clear(0.1f, 0.2f, 0.3f, 1);

        background.render();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.

        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        rebuild();
        background.resize(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}
