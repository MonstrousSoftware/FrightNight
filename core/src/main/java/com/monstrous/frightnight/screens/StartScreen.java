package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;


// on web this screen is to get a user key press so we can start playing sound
// todo add logo
// todo nicer font

public class StartScreen extends StdScreenAdapter {

    private Main game;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera cam;
    private int width, height;

    public StartScreen(Main game) {
        Gdx.app.log("StartScreen constructor", "");
        this.game = game;
    }


    @Override
    public void show() {
        Gdx.app.log("StartScreen show()", "");

        batch = new SpriteBatch();
        font = new BitmapFont();
        cam = new OrthographicCamera();
    }


    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            game.onLoadingComplete();
            return;
        }

//        // load assets asynchronously
//        if(game.assets.update()) {
//
//        }


        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, "Press any key to start....", width/2f, height/8f);
        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        Gdx.app.log("StartScreen resize()", "");
        cam.setToOrtho(false, width, height);
        cam.update();
    }

    @Override
    public void hide() {
        Gdx.app.log("StartScreen hide()", "");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("StartScreen dispose()", "");
        batch.dispose();
        font.dispose();
    }

}
