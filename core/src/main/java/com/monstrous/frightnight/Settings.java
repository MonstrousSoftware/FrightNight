package com.monstrous.frightnight;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    static public String title = "Fright Night";    // e.g. HTML window title

    static public boolean skipTitleScreen = true;
    static public boolean skipExitScreen = false;

    static public boolean supportControllers = false;       // in case it causes issues

    static public float eyeHeight = 1.5f;   // meters

    static public boolean fullScreen = false;
    static public boolean invertLook = false;
    static public boolean freeLook = true;

    static public float directionalLightIntensity = 0.31f;
    static public float ambientLightLevel = 0.303f;
    static public Color fogColour = new Color(.1f, 0.1f, 0.2f, 1f);


    static public boolean enableWeather = true;
    static public float lightningPeriod = 24f;          // period of lightning (randomized: average will be half this)

    static public boolean enableHints = true;
    static public boolean enableNarrator = true;


    static public String preferencesName = "frightnight";
}
