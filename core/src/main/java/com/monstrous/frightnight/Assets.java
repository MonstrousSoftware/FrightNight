package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class Assets implements Disposable {

    public AssetManager assets;

    public Assets() {
        Gdx.app.log("Assets constructor", "");
        assets = new AssetManager();

        assets.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        assets.load( "models/frightnight.gltf", SceneAsset.class);


        assets.load("skin/fright/fright.json", Skin.class);


        assets.load("images/title.png", Texture.class);
        assets.load("images/libgdx-faded.png", Texture.class);
        assets.load("images/areyouready.png", Texture.class);
        assets.load("images/ferocious-dinosaur2.png", Texture.class);
        assets.load("images/cornstalk-billboard.png", Texture.class);


        assets.load("sound/click_002.ogg", Sound.class);
        assets.load("sound/thunder-25689.mp3", Sound.class);

        assets.load("sound/interference-radio-tv-data-computer-hard-drive-7122.mp3", Music.class);

    }

    public boolean update() {
        return assets.update();
    }

    public void finishLoading() {
        assets.finishLoading();
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
