package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class MusicManager implements Disposable {

    public Music music;
    private final Preferences preferences;
    private float musicVolume;
    private final Assets assets;

    public MusicManager(Assets assets) {
        this.assets = assets;
        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        musicVolume = preferences.getFloat("musicVolume", 0.8f);
    }



    public void startMusic(String name, boolean looping) {
//        if(music != null)
//            return;
        music = assets.get(name);
        music.setLooping(looping);
        music.setVolume(musicVolume);
        music.play();
    }

    public void stopMusic() {
        if(music == null)
            return;
        music.stop();
    }

    public void pause() {
        music.pause();
    }

    public void resume() {
        music.play();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
        music.setVolume(musicVolume);
    }


    @Override
    public void dispose() {
        // save sound settings for next time
        preferences.putFloat("musicVolume", musicVolume);   // save
        preferences.flush();
        stopMusic();
    }
}
