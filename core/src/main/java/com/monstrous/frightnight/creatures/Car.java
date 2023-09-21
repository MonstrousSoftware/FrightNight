package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;

public class Car extends Creature {

    public static final float SPEED = 16f;

    public static final float WIDTH = 2f;
    public static final float LENGTH= 6f;

    private static boolean firstView = true;
    private boolean toPickup;
    public boolean pickedUp;
    public boolean slowDown;
    public boolean stopped;

    public Car(Vector3 position, Vector3 forward) {
        super("car", position);
        setForward(forward);
        this.speed = SPEED;
        toPickup = false;
        pickedUp = false;
        slowDown = false;
        stopped = false;
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
        if(!pickedUp && toPickup && stopped && distance < 4){
            hintQueue.addHint(0f, HintMessage.GLORY);
            pickedUp = true;
        }


        if(position.len() > 200){       // wrap from 200 to -200
            position.scl(-1);
        }

        if(toPickup && position.len() < 2 && speed == SPEED) { // near map centre
            slowDown = true;
        }
        if(slowDown && !stopped){
            if(speed > 0.1f)
                speed -= deltaTime * SPEED;
            else {
                speed = 0;
                stopped = true;
                hintQueue.addHint(1, HintMessage.CARRIAGE);
            }
        }

        if(speed < 0.1f)    // harmless when stood still
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

    public void makePickup() {
        toPickup = true;
    }
}
