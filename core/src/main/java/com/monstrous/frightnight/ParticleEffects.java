package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class ParticleEffects implements Disposable {


    private ParticleSystem particleSystem;
    private Array<ParticleEffect> activeEffects;
    private Array<ParticleEffect> deleteList;
    private ParticleEffect rainEffectTemplate;
    private ParticleEffect rainEffect;



    public ParticleEffects(Camera cam) {
        // create a particle system
        particleSystem = new ParticleSystem();

        // create a point sprite batch and add it to the particle system
        PointSpriteParticleBatch  pointSpriteBatch = new PointSpriteParticleBatch(1000,  new ParticleShader.Config(ParticleShader.ParticleType.Point),
                                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1f), null );
        pointSpriteBatch.setCamera(cam);
        particleSystem.add(pointSpriteBatch);

        // load particle effect from file
        // we should ideally move this to the Assets class to be loaded asynchronously, but there we have no access to 'particleSystem.getBatches()'
        //
        AssetManager assets = new AssetManager();
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        assets.load("particle/rain-ps.pfx", ParticleEffect.class, loadParam);
        assets.finishLoading();
        rainEffectTemplate = assets.get("particle/rain-ps.pfx");

        activeEffects = new Array<>();
        deleteList = new Array<>();
    }


    public void addRain(Matrix4 transform) {
        // we cannot use the originalEffect, we must make a copy each time we create new particle effect
        rainEffect = rainEffectTemplate.copy();
        addEffect( rainEffect, transform );
    }

    // change location of rain effect (we want it to hover over the camera at all times)
    public void moveRain(Matrix4 transform) {
        // we cannot use the originalEffect, we must make a copy each time we create new particle effect
        rainEffect.setTransform(transform);
    }




    // add effect
    // we use a transform rather than only a position because some effects may need to be oriented
    // e.g. dust trail behind the player
    private void addEffect(ParticleEffect effect, Matrix4 transform) {
        // add loaded effect to particle system
        effect.setTransform(transform);
        effect.init();
        effect.start();  // optional: particle will begin playing immediately
        particleSystem.add(effect);
        activeEffects.add(effect);
    }

    public void update( float deltaTime ) {
        particleSystem.update(deltaTime);

        // remove effects that have finished
        deleteList.clear();
        for(ParticleEffect effect : activeEffects) {
            if(effect.isComplete()) {
                Gdx.app.debug("particle effect completed", "");
                particleSystem.remove(effect);
                effect.dispose();
                deleteList.add(effect);
            }
        }
        activeEffects.removeAll(deleteList, true);
    }


    public void render(ModelBatch modelBatch) {
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        modelBatch.render(particleSystem);
    }


    @Override
    public void dispose() {
        particleSystem.removeAll();
        for(ParticleEffect effect : activeEffects)
            effect.dispose();
    }
}
