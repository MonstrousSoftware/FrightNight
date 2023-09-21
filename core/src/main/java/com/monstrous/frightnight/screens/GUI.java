package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.frightnight.Assets;

public class GUI implements Disposable {

    private Stage stage;
    private Skin skin;
    private Assets assets;

    public GUI( Assets assets) {
        this.assets = assets;

        stage = new Stage(new ScreenViewport());
        skin = assets.get("skin/fright/fright.json");
    }


    private void rebuild() {
        stage.clear();

    }

    public void showMessage( String text ){
        Table screenTable = new Table();
        screenTable.setFillParent(true);

        Label message = new Label(text, skin);

        screenTable.align(Align.bottom);
        screenTable.add(message).pad(50);
        screenTable.pack();

        screenTable.setColor(1,1,1,0);                   // set alpha to zero
        screenTable.addAction(sequence(fadeIn(3f), delay(2f), fadeOut(1f), removeActor()));           // fade in .. fade out, then remove this actor

        stage.addActor(screenTable);
    }

    public void render(float deltaTime) {
        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        Gdx.app.log("GUI resize", "gui " + width + " x " + height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }



    @Override
    public void dispose () {
        Gdx.app.log("GUI dispose()", "");
        stage.dispose();
    }
}
