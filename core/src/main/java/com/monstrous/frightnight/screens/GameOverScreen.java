package com.monstrous.frightnight.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.frightnight.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;



public class GameOverScreen extends MenuScreen {

    private GameScreen gameScreen;
    private String killer;


    public GameOverScreen(Main game, GameScreen gameScreen, String killer) {
        super(game);
        this.killer = killer;
        this.gameScreen = gameScreen;
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       Label gameOver = new Label("GAME OVER", skin);

       Label killedBy = new Label("You were killed by a "+killer, skin);
       TextButton quickLoad = new TextButton("Return to last save", skin);
       TextButton continueButton = new TextButton("Continue", skin);

       float pad = 20f;
       screenTable.add(gameOver).pad(pad).row();
       screenTable.add(killedBy).pad(pad).row();
       screenTable.add(quickLoad).pad(pad).row();
       screenTable.add(continueButton).pad(3*pad).row();


       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in


       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            ControllerMenuStage cStage = (ControllerMenuStage) stage;
            cStage.clearFocusableActors();
            cStage.addFocusableActor(quickLoad);
            cStage.addFocusableActor(continueButton);
            cStage.setFocusedActor(quickLoad);
            cStage.setEscapeActor(continueButton);
        }

        quickLoad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                // quick load
                gameScreen.getWorld().quickLoad();
                game.setScreen( gameScreen );
            }
        });

        continueButton.addListener(new ClickListener() {
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
