package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.frightnight.Settings;
import com.monstrous.frightnight.Sounds;
import com.monstrous.frightnight.shaders.MenuBackground;
import de.golfgl.gdx.controllers.ControllerMenuStage;

import static com.badlogic.gdx.Gdx.input;


// abstract menu screen to derive from, this is the base class for different menu screens


public class MenuScreen extends StdScreenAdapter {

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

        skin = game.assets.get("skin/fright/fright.json");
        stage = new ControllerMenuStage(new ScreenViewport());          // we can use this even without controllers, although it doesn't seem to work with teavm + Chrome browser
        rebuild();
        input.setInputProcessor(stage);
        input.setCatchKey(Input.Keys.UP, true);
        input.setCatchKey(Input.Keys.DOWN, true);
        if(Settings.supportControllers)
            game.controllerToInputAdapter.setInputProcessor(stage); // forward controller input to stage

        background = new MenuBackground();
    }

    protected void playSelectNoise() {
        game.sounds.playSound(Sounds.MENU_CLICK);
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
        background.dispose();
    }
}
