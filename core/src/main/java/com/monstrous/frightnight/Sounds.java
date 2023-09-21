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

    public static int VOICE_BRAVE_KNIGHT = 6;
    public static int VOICE_CAR = 7;
    public static int VOICE_CARRIAGE = 8;
    public static int VOICE_CORN = 9;
    public static int VOICE_GLORY = 10;
    public static int VOICE_HOUNDS = 11;
    public static int VOICE_NO_CREATURE = 12;
    public static int VOICE_UNDEAD = 13;
    public static int VOICE_VOID = 14;
    public static int VOICE_WEATHER = 15;


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


        sounds.add( assets.get("voicelines/braveknight.mp3"));
        sounds.add( assets.get("voicelines/car.mp3"));
        sounds.add( assets.get("voicelines/carriage.mp3"));
        sounds.add( assets.get("voicelines/corn.mp3"));
        sounds.add( assets.get("voicelines/glory.mp3"));
        sounds.add( assets.get("voicelines/hounds.mp3"));
        sounds.add( assets.get("voicelines/nocreature.mp3"));
        sounds.add( assets.get("voicelines/undead.mp3"));
        sounds.add( assets.get("voicelines/void.mp3"));
        sounds.add( assets.get("voicelines/weather.mp3"));


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
