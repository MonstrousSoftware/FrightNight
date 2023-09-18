package com.monstrous.frightnight.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.frightnight.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// pause menu (called from game screen on Escape key)

public class PauseMenuScreen extends MenuScreen {

    private GameScreen gameScreen;


    public PauseMenuScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       TextButton options = new TextButton("Options", skin);
       TextButton resume = new TextButton("Resume", skin);
       TextButton stop = new TextButton("Stop", skin);

       float pad = 10f;
       screenTable.add(options).pad(pad).row();
       screenTable.add(resume).pad(pad).row();
       screenTable.add(stop).pad(pad).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in


       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            ControllerMenuStage cStage = (ControllerMenuStage) stage;
            cStage.clearFocusableActors();
            cStage.addFocusableActor(options);
            cStage.addFocusableActor(resume);
            cStage.addFocusableActor(stop);
            cStage.setFocusedActor(resume);
            cStage.setEscapeActor(resume);
        }


       options.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen(new OptionsScreen( game, gameScreen ));
           }
       });

       resume.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen( gameScreen );
           }
       });

       stop.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               gameScreen.dispose();
               game.setScreen(new MainMenuScreen( game ));
           }
       });

   }

}
