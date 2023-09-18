package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

// obsoleted by CamController

public class Player extends Creature {

    final static float CAM_HEIGHT = 1.5f;       // meters
    final static float BOB_DURATION = 0.6f;     // seconds
    final static float BOB_HEIGHT = 0.02f;      // m
    final static float BOB_WIDTH = 0.03f;      // m

    public Vector3 eyePosition;
    private Vector3 vTmp = new Vector3();
    private float bobAngle;


    public Player(float x, float y, float z) {

        super(x, y, z);
        eyePosition = new Vector3(x, y, z);
        forward.set(-1,0, 0);
        bobAngle = 0;
    }


    public void move(float deltaTime) {
        float step = 1f;
        //v.set(0,0);
        if(Gdx.input.isKeyPressed(Input.Keys.A))
            forward.rotate(Vector3.Y, 90f*deltaTime);
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            forward.rotate(Vector3.Y, -90f*deltaTime);
        speed = 0f;
        if(Gdx.input.isKeyPressed(Input.Keys.W))
            speed = 3f;
        if(Gdx.input.isKeyPressed(Input.Keys.S))
            speed = -3f;
        moveForward(deltaTime);
        headBob(speed, deltaTime);
    }

    private void headBob(float speed, float deltaTime ) {

        if(Math.abs(speed) > 0.1f ) {
            bobAngle += deltaTime * 2.0f * Math.PI / BOB_DURATION;
            bobAngle += MathUtils.random(0.4f) - 0.2f;  // add bit of noise to the angle
            // move the head up and down in a sine wave
            float bobHeight = (float) (BOB_HEIGHT * Math.sin(bobAngle));
            eyePosition.set(position);
            eyePosition.y = position.y + bobHeight;
            // move eye point side to side in a cosine wave
            vTmp.set(forward).crs(Vector3.Y).nor(); // sideways vector
            vTmp.scl( BOB_WIDTH * (float)Math.cos(bobAngle/2f) );   // half the rate of up/down bobbing
            eyePosition.add(vTmp);
        }
    }

}
