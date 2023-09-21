package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;
import com.monstrous.frightnight.Sounds;

// obsoleted by CamController

public class Player extends Creature {
    private boolean firstVoid = true;

    public Player(Vector3 position) {
        super("Player", position);
        this.radius = 0.5f;     // give a bit of an advantage
    }

    public void move( HintQueue hintQueue ) {
        // getting close to world's edge
        if(firstVoid && (Math.abs(position.x) > 175f || (Math.abs(position.z) > 175f)) ) {
            hintQueue.addHint(0, HintMessage.VOID);
            firstVoid = false;
        }
    }


        @Override
    public void die() {
        super.die();
        Gdx.app.log("Game Over", "player died");
    }
}
