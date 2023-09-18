package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.monstrous.frightnight.Settings;
import com.monstrous.frightnight.input.MyControllerMappings;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;


public class Main extends Game {
    public static final boolean RELEASE = true;

    public ControllerToInputAdapter controllerToInputAdapter;
    private Sound selectSound;

    @Override
    public void create() {

        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));

        if (Settings.supportControllers) {
            controllerToInputAdapter = new ControllerToInputAdapter(new MyControllerMappings());
            // bind controller events to keyboard keys
            controllerToInputAdapter.addButtonMapping(MyControllerMappings.BUTTON_FIRE, Input.Keys.ENTER);
            controllerToInputAdapter.addAxisMapping(MyControllerMappings.AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN);
            Controllers.addListener(controllerToInputAdapter);
        }

        selectSound = Gdx.audio.newSound(Gdx.files.internal("sound/click_002.ogg"));

        onLoadingComplete();
        //setScreen(new StartScreen(this));
    }

    public void onLoadingComplete() {
        if(!Settings.skipTitleScreen)
            setScreen(new TitleScreen(this));
        else
            setScreen(new MainMenuScreen(this));
    }

    // we put this in main so the sound can keep playing during a screen switch
    public void playSelectSound() {
        selectSound.play();
    }

    @Override
    public void dispose() {
        super.dispose();
        selectSound.dispose();
    }
}
