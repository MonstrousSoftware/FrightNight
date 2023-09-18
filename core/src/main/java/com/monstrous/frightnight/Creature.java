package com.monstrous.frightnight;

import com.badlogic.gdx.math.Vector3;

public class Creature {
    public Vector3 position;
    public Vector3 forward;
    public float speed = 0;
    public boolean isDead;
    private Vector3 tmpVec = new Vector3();

    public Creature(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
        this.forward = new Vector3(0,0, 0);
        this.speed = 0;
        isDead = false;
    }

    public void moveForward(float deltaTime ) {
        tmpVec.set(forward).scl(speed*deltaTime);
        position.add(tmpVec);
    }


}
