package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.frightnight.Settings;
import com.monstrous.frightnight.shaders.MenuBackground;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// abstract menu screen to derive from


public class MenuScreen extends StdScreenAdapter {

    protected Main game;
    protected Viewport viewport;
    protected Stage stage;      // from gdx-controllers-utils
    protected Skin skin;
    private MenuBackground background;


    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new ScreenViewport();

        skin = new Skin(Gdx.files.internal("skin/fright/fright.json"));
        if(Settings.supportControllers)
            stage = new ControllerMenuStage(new ScreenViewport());
        else
            stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);
        if(Settings.supportControllers)
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
        super.render(delta);
        background.render();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.

        Gdx.app.log("MenuScreen","resize "+width+" x "+height);
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        rebuild();
        background.resize(width, height);
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
