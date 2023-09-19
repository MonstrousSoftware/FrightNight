package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

// obsoleted by CamController

public class Player extends Creature {
    public Player(Vector3 position) {
        super("Player", position);
    }

    @Override
    public void die() {
        super.die();
        Gdx.app.log("Game Over", "player died");
    }
}
