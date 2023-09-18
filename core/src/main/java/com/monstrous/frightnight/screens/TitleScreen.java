package com.monstrous.frightnight.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.frightnight.filters.TitlePostFilter;


// this version loads the title from an image file, works on all platforms
// adds a shader postprocessing effect (tv noise) and fade-in, fade-out fo screen elements

public class TitleScreen extends ScreenAdapter {

    private Main game;
    private SpriteBatch batch;
    private int width;
    private int height;
    private Texture titleTexture;
    private float titleWidth, titleHeight;
    private Texture logoTexture;
    private Texture readyTexture;
    private Viewport viewport;
    private TitlePostFilter filter;
    private FrameBuffer fbo;
    private Music staticNoise;      // as it is so long, load as music instead of sound so that it can be buffered
    private float readyAlpha;
    private float logoAlpha;
    private float bgColor;
    private boolean fadeOut;
    private boolean zoomOut;
    private float blackScreenTimer;
    private float pictureWidth, pictureHeight;

    public TitleScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        titleTexture = game.assets.get("images/title.png"); //new Texture( Gdx.files.internal("images/title.png"));
        logoTexture = game.assets.get("images/libgdx-faded.png"); //new Texture( Gdx.files.internal("images/libgdx-faded.png"));  // desaturated version to stay on theme
        readyTexture = game.assets.get("images/areyouready.png"); //new Texture( Gdx.files.internal("images/areyouready.png"));
        titleWidth = titleTexture.getWidth();
        titleHeight = titleTexture.getHeight();
        viewport = new ScreenViewport();
        filter = new TitlePostFilter();
        staticNoise = game.assets.get("sound/interference-radio-tv-data-computer-hard-drive-7122.mp3"); //Gdx.audio.newSound(Gdx.files.internal("sound/interference-radio-tv-data-computer-hard-drive-7122.mp3"));
        staticNoise.play();
        readyAlpha = 0;
        logoAlpha = 1f;
        bgColor = 0.5f;
        fadeOut = false;
        zoomOut = false;
    }


    @Override
    public void render(float delta) {
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){

            fadeOut = true;
        }

        // fade in the 'are you ready?' text
        readyAlpha += delta / 10f;
        if(readyAlpha > 1.0f)
            readyAlpha = 1.0f;

        if(fadeOut){
            // fade out the title and logo
            bgColor -= delta;
            logoAlpha -= delta;
            if(logoAlpha < -1f){
                fadeOut = false;
                zoomOut = true;
            }
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
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }


        fbo.begin();
            ScreenUtils.clear(bgColor, bgColor, bgColor, 1f);
            batch.begin();
            batch.setProjectionMatrix( viewport.getCamera().combined );

            float x = ((width - titleWidth)/2f);     // centred horizontally
            float y = (height/2f);        // align bottom of title to mid-height of screen

            batch.setColor(1, 1, 1, logoAlpha);
            batch.draw(titleTexture,x,y);
            batch.draw(logoTexture,(width-logoTexture.getWidth())/2f,50);

            batch.setColor(1,1,1, readyAlpha);
            batch.draw(readyTexture,(width-readyTexture.getWidth())/2f,250);

            batch.end();
        fbo.end();
        ScreenUtils.clear(bgColor, bgColor, bgColor, 1f);
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
        //titleTexture.dispose();
        filter.dispose();
        staticNoise.stop();
        //staticNoise.dispose();
    }
}
