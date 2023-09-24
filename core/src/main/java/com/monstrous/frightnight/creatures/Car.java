package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;
import net.mgsx.gltf.scene3d.scene.Scene;

public class Car extends Creature {
    public static final int DRIVING = 0;
    public static final int TO_PICKUP = 1;
    public static final int SLOW_DOWN = 2;
    public static final int STOPPED = 3;
    public static final int PICKED_UP = 4;

    public static final float SPEED = 16f;

    public static final float WIDTH = 2f;
    public static final float LENGTH= 6f;

    private static boolean firstView = true;
    public Scene altScene;                      // alternative scene with door open

    public Car() {
    }

    public Car(Vector3 position, Vector3 forward) {
        super("car", position);
        setForward(forward);
        this.speed = SPEED;
        mode = DRIVING;
    }



    public void move(float deltaTime, HintQueue hintQueue,  Player player, Array<Creature> creatures) {
        update(deltaTime);

        float distance = position.dst(player.position);
        if(firstView && distance < 10 ){
            Vector3 view = new Vector3(position).sub(player.position);  // view vector from player to zombie
            float dot = view.dot(player.getForward());  // angle with player's forward direction
            if(dot > 0.3f) {  // player is able to see car
                firstView = false;
                hintQueue.addHint(0f, HintMessage.CAR);
            }
        }
        if(mode == STOPPED && distance < 3){
            hintQueue.flush();
            hintQueue.addHint(0f, HintMessage.GLORY);
            mode = PICKED_UP;
        }

        if(mode == PICKED_UP){
            speed += deltaTime;
        }


        if(position.len() > 200){       // wrap from 200 to -200
            position.scl(-1);
        }

        if(mode == TO_PICKUP && speed == SPEED) {
            mode = SLOW_DOWN;
        }
        if(mode == SLOW_DOWN ){
            if(speed > 0.1f)
                speed -= deltaTime * SPEED * 0.3f;
            else {
                speed = 0;
                mode = STOPPED;
                hintQueue.addHint(1, HintMessage.CARRIAGE);
            }
        }

        if(speed < 0.1f || mode == PICKED_UP)    // harmless when stood still
            return;

        // kill any creature on its path
        for(Creature creature : creatures) {
            if(creature == this)
                continue;

            if(creature.isDead())
                continue;

            // assumes car is traveling on Z axis
            // rectangle vs. circle overlap test
            if(creature.position.x+creature.radius >= position.x-WIDTH/2f && creature.position.x-creature.radius <= position.x+WIDTH/2f &&
                creature.position.z+creature.radius >= position.z-LENGTH/2f && creature.position.z-creature.radius <= position.z+LENGTH/2f ) {
                creature.killedBy(this);
                Gdx.app.log("car kills:", creature.name+ " at "+position+" creature at "+creature.position);
            }
        }
    }

    public void makePickup() {  // signals end game sequence
        if(mode == DRIVING)
            mode = TO_PICKUP;
    }

    public boolean isWaitingForPlayer() {
        return mode == STOPPED;
    }

    public boolean hasPickedUp() {
        return mode == PICKED_UP;
    }
}
