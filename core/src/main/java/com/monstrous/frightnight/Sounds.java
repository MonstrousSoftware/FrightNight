package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Sounds implements Disposable {

    // use constants to identify sound effects
    public static int MENU_CLICK = 0;
    public static int AARGH = 1;
    public static int BARK = 2;
    public static int GROWL = 3;
    public static int THUNDER = 4;
    public static int FOOT_STEP = 5;

    private static Array<Sound> sounds;
    private final Preferences preferences;
    public static float soundVolume;


    public Sounds(Assets assets) {
        sounds = new Array<>();


        // must be in line with constants defined above
        sounds.add( assets.get("sound/click_002.ogg"));
        sounds.add( assets.get("sound/aargh0.wav"));
        sounds.add( assets.get("sound/Dog Bark 2.wav"));
        sounds.add( assets.get("sound/dog-frieda-grunt-96khz-01.wav"));
        sounds.add( assets.get("sound/thunder-25689.mp3"));
        sounds.add( assets.get("sound/footstep_concrete_001.ogg"));


        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        soundVolume = preferences.getFloat("soundVolume", 1.0f);
    }

    public static Sound playSound(int code) {
        Sound s = sounds.get(code);
        s.play(soundVolume);
        return s;
    }

    public static void stopSound(int code) {
        sounds.get(code).stop();
    }

    public static float getSoundVolume() {
        return soundVolume;
    }

    public static void setSoundVolume(float vol) {
        soundVolume = vol;
    }

    @Override
    public void dispose() {
        sounds.clear();
        // save sound settings for next time
        preferences.putFloat("soundVolume", soundVolume);   // save
        preferences.flush();
    }
}
