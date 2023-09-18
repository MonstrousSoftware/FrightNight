package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.frightnight.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// main menu

public class MainMenuScreen extends MenuScreen {




    public MainMenuScreen(Main game) {
        super(game);
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       Image title = new Image(new Texture( Gdx.files.internal("images/title.png")));

       TextButton play = new TextButton("Play Game", skin);
       TextButton options = new TextButton("Options", skin);
       TextButton quit = new TextButton("Quit", skin);

       float pad = 10f;
       screenTable.add(title).pad(50).row();
       screenTable.add(play).pad(pad).row();
       screenTable.add(options).pad(pad).row();
       screenTable.add(quit).pad(pad).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in
       stage.addActor(screenTable);

       options.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen(new OptionsScreen( game, null ));
           }
       });

       play.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               //game.setScreen(new GameScreen( game ));
           }
       });

       quit.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               if(!Settings.skipTitleScreen)
                    game.setScreen(new ExitScreen( game ));
               else
                    Gdx.app.exit();
           }
       });

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            ControllerMenuStage cStage = (ControllerMenuStage)stage;
            cStage.clearFocusableActors();
            cStage.addFocusableActor(play);
            cStage.addFocusableActor(options);
            cStage.addFocusableActor(quit);
            cStage.setFocusedActor(play);
        }
   }

}
