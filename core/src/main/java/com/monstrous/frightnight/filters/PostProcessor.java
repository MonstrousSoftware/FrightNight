package com.monstrous.frightnight.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

public class PostProcessor implements Disposable {

    private FrameBuffer fboBlur;
    private BrightFilter brightFilter;
    private BlurFilter blurFilter;
    private CombineFilter combineFilter;
    private int width;
    private int height;

    public PostProcessor() {
        brightFilter = new BrightFilter();
        blurFilter = new BlurFilter( 4);
        combineFilter = new CombineFilter();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }


    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        // the buffers used for blurring have a lower resolution
        // this saves processing and gives a more blurry effect
        fboBlur = new FrameBuffer(Pixmap.Format.RGBA8888, width/8, height/8, false);

        brightFilter.resize(width, height);
        blurFilter.resize(width, height);
        combineFilter.resize(width, height);
    }

    public void render ( FrameBuffer fbo ) {
        render(fbo, 0, 0, width, height);
    }

    public void render ( FrameBuffer fbo, int x, int y, int w, int h ) {

        // brightness filter fbo -> fbo2
       brightFilter.renderToBuffer(fboBlur, fbo);
       //brightFilter.render(fbo, x, y, w, h);

        blurFilter.renderToBuffer(fboBlur, fboBlur);
        // latest blurred image is now in fbo2

        // combine original render with blurred highlights
        combineFilter.setHighlightTexture(fboBlur);
        combineFilter.render(fbo,  x, y, w, h);
    }

    @Override
    public void dispose () {
        fboBlur.dispose();
        brightFilter.dispose();
        blurFilter.dispose();
        combineFilter.dispose();
    }
}
