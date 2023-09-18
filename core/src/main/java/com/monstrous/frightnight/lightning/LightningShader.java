package com.monstrous.frightnight.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;


public class LightningShader extends DefaultShader {

    public static final Color SPECIAL_COLOUR = Color.CYAN;

    float time;

    // uniform locations

    int u_time;

    public LightningShader(Renderable renderable) {
        super(renderable, new Config(Gdx.files.internal("shaders/lightning.vertex.glsl").readString(),
                Gdx.files.internal("shaders/lightning.fragment.glsl").readString()));
        program.pedantic = false;
    }

    @Override
    public void init() {
        super.init();

        u_time = program.getUniformLocation("u_time");
    }

    @Override
    public boolean canRender(Renderable renderable) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        time += Gdx.graphics.getDeltaTime();

        program.setUniformf(u_time, time);
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}

