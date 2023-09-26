package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.math.Vector3;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;

// obsoleted by CamController

public class Player extends Creature {
    private boolean firstVoid = true;

    public Player() {

    }

    public Player(Vector3 position, Vector3 forward) {
        super("Player", position);
        setForward(forward);
        this.radius = 0.5f;     // give a bit of an advantage
    }

    public void move( HintQueue hintQueue ) {
        // getting close to world's edge
        if(firstVoid && (Math.abs(position.x) > 175f || (Math.abs(position.z) > 175f)) ) {
            hintQueue.showMessage(0, HintMessage.VOID);
            firstVoid = false;
        }
    }

}
