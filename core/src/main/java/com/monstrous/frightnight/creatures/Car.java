package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Car extends Creature {

    public static final float WIDTH = 2f;
    public static final float LENGTH= 6f;

    public Car(Vector3 position, Vector3 forward, float speed) {
        super("car", position);
        this.forward.set(forward);
        this.speed = speed;
    }

    public void move( float deltaTime, Array<Creature> creatures) {
        moveForward(deltaTime);


        if(position.len() > 200){       // wrap from 200 to -200
            position.scl(-1);
        }

        // kill any creature on its path
        for(Creature creature : creatures) {
            if(creature == this)
                continue;

            if(creature.isDead())
                continue;

            // assumes car is traveling on Z axis
            if(creature.position.x >= position.x-WIDTH/2f && creature.position.x <= position.x+WIDTH/2f &&
                creature.position.z >= position.z-LENGTH/2f && creature.position.z <= position.z+LENGTH/2f ) {
                creature.killedBy(this);
                Gdx.app.log("car kills:", creature.name+ " at "+position+" creature at "+creature.position);
            }
        }

    }
}