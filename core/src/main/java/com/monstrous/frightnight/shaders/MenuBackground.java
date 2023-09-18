package com.monstrous.frightnight.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;


// shader effect as background for menu screens

public class MenuBackground implements Disposable {

    private SpriteBatch batch;
    private ShaderProgram program;
    private static float time;
    private float[] resolution = { 640, 480 };      // will be fixed by resize()
    private int u_time; // shader uniform id
    private int u_resolution; // shader uniform id
    Texture texture;
    TextureRegion textureRegion;

    public MenuBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.drawPixel(0,0);

        texture = new Texture(pixmap);
        textureRegion = new TextureRegion(texture, 0,0,1,1);




        // full screen post processing shader
        program = new ShaderProgram(
            Gdx.files.internal("shaders\\worley.vertex.glsl"),
            Gdx.files.internal("shaders\\worley.fragment.glsl"));
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        ShaderProgram.pedantic = false;

        u_time = program.getUniformLocation("u_time");
        u_resolution = program.getUniformLocation("u_resolution");

        batch = new SpriteBatch();
    }

    public void resize (int width, int height) {
        resolution[0] = width;
        resolution[1] = height;
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);  // to ensure the fbo is rendered to the full window after a resize

    }

    public void render() {
        time += Gdx.graphics.getDeltaTime();


        batch.begin();
        batch.setShader(program);                        // post-processing shader
        program.setUniformf(u_time, time);
        program.setUniform2fv(u_resolution, resolution, 0, 2);
        batch.draw(textureRegion, 0, 0, resolution[0], resolution[1]);    // draw frame buffer as screen filling texture
        batch.end();
        batch.setShader(null);
    }


    @Override
    public void dispose() {
        batch.dispose();
        program.dispose();
        texture.dispose();
    }
}
