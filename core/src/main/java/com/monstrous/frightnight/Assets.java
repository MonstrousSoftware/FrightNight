package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Assets implements Disposable {

    public AssetManager assets;

    public Assets() {
        Gdx.app.log("Assets constructor", "");
        assets = new AssetManager();

        assets.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        assets.load( "models/frightnight.gltf", SceneAsset.class);
        //assets.load( "models/zombie.gltf", SceneAsset.class);


        assets.load("skin/fright/fright.json", Skin.class);


        assets.load("images/title.png", Texture.class);
        assets.load("images/libgdx-faded.png", Texture.class);
        assets.load("images/areyouready.png", Texture.class);
        assets.load("images/ferocious-dinosaur2.png", Texture.class);
        assets.load("images/cornstalk-billboard.png", Texture.class);


        assets.load("sound/click_002.ogg", Sound.class);
        assets.load("sound/aargh0.wav", Sound.class);
        assets.load("sound/Dog Bark 2.wav", Sound.class);
        assets.load("sound/dog-frieda-grunt-96khz-01.wav", Sound.class);
        assets.load("sound/thunder-25689.mp3", Sound.class);
        assets.load("sound/footstep_concrete_001.ogg", Sound.class);

        assets.load("voicelines/braveknight.mp3", Sound.class);
        assets.load("voicelines/car.mp3", Sound.class);
        assets.load("voicelines/carriage.mp3", Sound.class);
        assets.load("voicelines/corn.mp3", Sound.class);
        assets.load("voicelines/glory.mp3", Sound.class);
        assets.load("voicelines/hounds.mp3", Sound.class);
        assets.load("voicelines/nocreature.mp3", Sound.class);
        assets.load("voicelines/undead.mp3", Sound.class);
        assets.load("voicelines/void.mp3", Sound.class);
        assets.load("voicelines/weather.mp3", Sound.class);

        assets.load("sound/squelch.ogg", Sound.class);
        assets.load("sound/dog-yelp.ogg", Sound.class);
        assets.load("sound/grunt.ogg", Sound.class);


        assets.load("sound/interference-radio-tv-data-computer-hard-drive-7122.mp3", Music.class);      // not music, but a very long sound effect
        assets.load("music/dark-ambient-121126.mp3", Music.class);
        assets.load("music/spooky-music-theme-121127.mp3", Music.class);
        assets.load("music/happy-quirky-theme-160995.mp3", Music.class);
    }

    public boolean update() {
        return assets.update();
    }

    public void finishLoading() {
        assets.finishLoading();
    }

    public float getProgress() {
        return assets.getProgress();
    }

    public <T> T get(String name ) {
        return assets.get(name);
    }


    @Override
    public void dispose() {
        Gdx.app.log("Assets dispose()", "");
        assets.dispose();
        assets = null;
    }
}
