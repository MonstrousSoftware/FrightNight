package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controllers;
import com.monstrous.frightnight.Assets;
import com.monstrous.frightnight.MusicManager;
import com.monstrous.frightnight.Settings;
import com.monstrous.frightnight.Sounds;
import com.monstrous.frightnight.input.MyControllerMappings;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;


public class Main extends Game {
    public static final boolean RELEASE = true;

    public Assets assets;
    public ControllerToInputAdapter controllerToInputAdapter;
    public Sounds sounds;
    public MusicManager musicManager;


    @Override
    public void create() {

        Gdx.app.log("Gdx version", com.badlogic.gdx.Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));

        // load assets
        assets = new Assets();
       // assets.finishLoading();         // todo asynch

        if (Settings.supportControllers) {
            controllerToInputAdapter = new ControllerToInputAdapter(new MyControllerMappings());
            // bind controller events to keyboard keys
            controllerToInputAdapter.addButtonMapping(MyControllerMappings.BUTTON_FIRE, Input.Keys.ENTER);
            controllerToInputAdapter.addAxisMapping(MyControllerMappings.AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN);
            Controllers.addListener(controllerToInputAdapter);
        }

        setScreen(new StartScreen(this));
    }

    public void onLoadingComplete() {
        sounds = new Sounds(assets);
        musicManager = new MusicManager(assets);

        if(!Settings.skipTitleScreen)
            setScreen(new TitleScreen(this));
        else
            setScreen(new MainMenuScreen(this));
    }


    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        musicManager.dispose();
    }
}
