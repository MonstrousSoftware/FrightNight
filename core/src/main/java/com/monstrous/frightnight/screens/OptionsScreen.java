package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerPowerLevel;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.frightnight.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


public class OptionsScreen extends MenuScreen {
    private GameScreen gameScreen;    // to keep track where we were called from
    private Controller controller;
    private Label controllerLabel;
    //private Table screenTable;


    public OptionsScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        super.show();

        if(!Settings.supportControllers)
            return;

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("Controllers", controller.getName());
        }


        controller = Controllers.getCurrent();
        if(controller != null ) {

            Gdx.app.log("current controller", controller.getName());
            Gdx.app.log("unique id", controller.getUniqueId());
            Gdx.app.log("is connected", "" + controller.isConnected());
            ControllerPowerLevel powerLevel = controller.getPowerLevel();
            Gdx.app.log("power level", "" + powerLevel.toString());
            Gdx.app.log("can vibrate", "" + controller.canVibrate());
            if (controller.canVibrate()) {
                controller.startVibration(500, 1f);
            }
        }
        else
            Gdx.app.log("current controller", "none");
   }

   private void checkControllerChanges() {
       Controller currentController = Controllers.getCurrent();
       if(currentController != controller ) {
           controller = currentController;
           if (controller != null) {

               Gdx.app.log("current controller", controller.getName());
               Gdx.app.log("unique id", controller.getUniqueId());
               Gdx.app.log("is connected", "" + controller.isConnected());
               ControllerPowerLevel powerLevel = controller.getPowerLevel();
               Gdx.app.log("power level", "" + powerLevel.toString());
               Gdx.app.log("can vibrate", "" + controller.canVibrate());
               if (controller.canVibrate()) {
                   controller.startVibration(500, 1f);
               }
           } else
               Gdx.app.log("current controller", "none");

           if(controller != null)
               controllerLabel.setText(controller.getName());
           else
               controllerLabel.setText("None");
       }
   }

   @Override
   protected void rebuild() {
       stage.clear();

       Table screenTable = new Table();
       screenTable.setFillParent(true);


       CheckBox fullScreen = new CheckBox("Full Screen", skin);
       fullScreen.setChecked(Settings.fullScreen);

       CheckBox invertLook = new CheckBox("Invert Look", skin);
       invertLook.setChecked(Settings.invertLook);

       CheckBox freeLook = new CheckBox("Free Look", skin);
       freeLook.setChecked(Settings.freeLook);

       controllerLabel = new Label("None", skin);
       if(controller != null)
           controllerLabel.setText(controller.getName());

       TextButton done = new TextButton("Done", skin);

       // todo improve left alignment


       int pad = 10;
       screenTable.add(fullScreen).pad(pad).row();
       screenTable.add(invertLook).pad(pad).row();
       screenTable.add(freeLook).pad(pad).row();
       screenTable.add(new Label("Controller", skin)).pad(pad);     // todo ':' is not in font
       screenTable.add(controllerLabel).row();
       screenTable.add(done).pad(20).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in

       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
       if(Settings.supportControllers) {
           ControllerMenuStage cStage = (ControllerMenuStage) stage;
           cStage.clearFocusableActors();
           cStage.addFocusableActor(fullScreen);
           cStage.addFocusableActor(invertLook);
           cStage.addFocusableActor(freeLook);
           //stage.addFocusableActor(controllerLabel);
           cStage.addFocusableActor(done);
           cStage.setFocusedActor(fullScreen);
           cStage.setEscapeActor(done);
       }

       fullScreen.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.fullScreen = fullScreen.isChecked();
               Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
               if(Settings.fullScreen)
                   Gdx.graphics.setFullscreenMode(currentMode);
               else
                   Gdx.graphics.setWindowedMode(1200, 800);         // todo
           }
       });

       invertLook.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.invertLook = invertLook.isChecked();
           }
       });
       freeLook.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.freeLook = freeLook.isChecked();
           }
       });

       done.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               if(gameScreen != null)
                game.setScreen(new PauseMenuScreen( game, gameScreen ));
               else
                game.setScreen(new MainMenuScreen( game ));
           }
       });

   }


    @Override
    public void render(float delta) {
        if(Settings.supportControllers)
            checkControllerChanges();
        super.render(delta);
    }


}
