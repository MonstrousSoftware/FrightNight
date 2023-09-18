package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.frightnight.filters.TitlePostFilter;


// Exit screen, similar to TitleScreen

public class ExitScreen extends ScreenAdapter {

    private Main game;
    private SpriteBatch batch;
    private int width;
    private int height;
    private Texture titleTexture;
    private float titleWidth, titleHeight;
    private Viewport viewport;
    private TitlePostFilter filter;
    private FrameBuffer fbo;
    private Sound staticNoise;
    private float logoAlpha;
    private float bgColor;
    private boolean fadeOut;
    private boolean zoomOut;
    private float blackScreenTimer;
    private float pictureWidth, pictureHeight;
    private float timer;

    public ExitScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        titleTexture = new Texture( Gdx.files.internal("images/ferocious-dinosaur2.png"));
        titleWidth = titleTexture.getWidth();
        titleHeight = titleTexture.getHeight();
        viewport = new ScreenViewport();
        filter = new TitlePostFilter();
        staticNoise = Gdx.audio.newSound(Gdx.files.internal("sound/interference-radio-tv-data-computer-hard-drive-7122.mp3"));
        staticNoise.play();
        logoAlpha = 1f;
        bgColor = 0.5f;
        fadeOut = false;
        zoomOut = false;
        timer = 0;
    }


    @Override
    public void render(float delta) {
        timer += delta;
        if(timer > 3f || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
            zoomOut = true;
        }

        if(zoomOut) {
            if(pictureHeight > 0) {     // decrease the picture size
                pictureHeight -= 0.5f * delta * height / 2;
                pictureWidth -= 0.5f * delta * width / 2;
                if(pictureHeight <= 0) {
                    staticNoise.stop();
                    blackScreenTimer = 2f;
                }
            }
            else {

                blackScreenTimer -= delta;      // allow a silence black screen for a few seconds
                if (blackScreenTimer <= 0) {
                    Gdx.app.exit();
                    return;
                }
            }
        }


        fbo.begin();
            ScreenUtils.clear(bgColor, bgColor, bgColor, 1f);
            batch.begin();
            batch.setProjectionMatrix( viewport.getCamera().combined );

            float x = ((width - titleWidth)/2f);     // centred horizontally
            float y = ((height-titleHeight)/2f);        // align bottom of title to mid-height of screen

            batch.setColor(1, 1, 1, logoAlpha);
            //batch.draw(titleTexture,x,y);
            batch.draw(titleTexture, 0, 0, width, height);

            batch.end();
        fbo.end();
        ScreenUtils.clear(Color.BLACK);
        if(pictureHeight > 0)
            filter.render(fbo, (width-pictureWidth)/2f, (height-pictureHeight)/2f, pictureWidth, pictureHeight);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        this.width = width;
        this.height = height;
        //batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);
        viewport.update(width, height, true);
        filter.resize(width, height);
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        pictureWidth = width;
        pictureHeight = height;
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        batch.dispose();
        titleTexture.dispose();
        filter.dispose();
        staticNoise.stop();
        staticNoise.dispose();
    }
}
