package com.monstrous.frightnight.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.frightnight.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


public class KeysScreen extends MenuScreen {

    private String[][] keyBindings = {{ "W", "forward" }, {"A", "turn left"}, {"S", "back"}, {"D", "turn right"},
        { "Q", "strafe left" }, {"E", "strafe right"},
        { "Esc", "in-game menu" },
        { "F5", "quick-save" }, {"F9", "quick-load"},
        { "F1", "hint" }
    };

    private GameScreen gameScreen;


    public KeysScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       Label title = new Label("KEYS:", skin);

       TextButton continueButton = new TextButton("Continue", skin);

       float pad = 3f;
       for(int i = 0; i < keyBindings.length; i++) {
           screenTable.add(new Label(keyBindings[i][0], skin)).width(100).center().pad(pad);
           screenTable.add(new Label(keyBindings[i][1], skin)).pad(pad).left().row();
       }

       screenTable.add(continueButton).colspan(2).pad(20).row();


       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in


       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            ControllerMenuStage cStage = (ControllerMenuStage) stage;
            cStage.clearFocusableActors();
            cStage.addFocusableActor(continueButton);
            cStage.setFocusedActor(continueButton);
            cStage.setEscapeActor(continueButton);
        }

        continueButton.addListener(new ClickListener() {
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

}
