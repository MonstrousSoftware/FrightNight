package com.monstrous.frightnight.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.frightnight.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


public class CreditsScreen extends MenuScreen {

    public static final String credits = "Fright Night, a game made for the LibGDX jam 26 by Monstrous Software\n\n"+
                    "Made using:\n- libGDX v1.12.0\n- gdx-teavm\n- gdx-gltf\n- gdx-controllers\n- gdx-liftoff\n\n"+
                    "Models created in Blender\n"+
                    "- using tutorials from Imphenzia\n- spooky tree tutorial by Grant Abbitt\n"+
                    "- zombie model adapted from Kenney's assets\n\n"+
                    "Sky box from cleanpng.com\n\n"+
                    "Fonts from fontmeme.com:\n- Fluxisch Else by OSP Foundry\n- Halloween Fright by Brithos Type\n- OpenSans by Steve Matteson\n\n"+
                    "Voice synthesis:\n-ttsmaker.com\n\n"+
                    "Music from Pixabay.com:\n- Dark Ambient, Spooky Music Theme, Happy Quirky Theme by TheoJT\n\n\n\n"+
                    "Thank you for playing";

    public CreditsScreen(Main game ) {
        super(game);
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       Label title = new Label("CREDITS", skin);

       Label creds = new Label(credits, skin, "small");

       TextButton continueButton = new TextButton("Continue", skin);

       float pad = 10f;
       screenTable.add(title).pad(pad).row();
       screenTable.add(creds).pad(pad).row();
       screenTable.add(continueButton).bottom();


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
               game.setScreen(new MainMenuScreen( game ));
           }
       });

   }

}
