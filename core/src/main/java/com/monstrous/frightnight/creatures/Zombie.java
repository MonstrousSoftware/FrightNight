package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;
import com.monstrous.frightnight.Sounds;

public class Zombie extends Creature {
    public static final int WANDERING = 0;
    public static final int ATTACKING = 1;

    public static final int FORGET_DISTANCE = 20;
    public static final int ATTACK_DISTANCE = 10;
    public static final int KILL_DISTANCE = 1;

    public static final float MINIMUM_SEPARATION = 3;
    public static final float SPEED = 0.8f;

    private float wanderTimer;
    private static boolean firstZombie = true;

    public Zombie() {
    }

    public Zombie(Vector3 position, Vector3 forward) {
        super("zombie", position);
        setForward(forward);
        this.mode = WANDERING;
        speed = SPEED;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("wanderTimer", id);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        wanderTimer = json.readValue("wanderTimer", Float.class, jsonData);
    }

    public void move(float delta, Sounds sounds, HintQueue hintQueue, Player player, Array<Creature> creatures ) {
        if(isDead())
            return;


        // change mode based on distance of player

        float distance = position.dst(player.position);

        if(firstZombie && distance < 50 ){
            Vector3 view = new Vector3(position).sub(player.position);  // view vector from player to zombie
            float dot = view.dot(player.getForward());  // angle with player's forward direction
            if(dot > 0.3f) {  // player is able to see zombie
                firstZombie = false;
                hintQueue.showMessage(0f, HintMessage.UNDEAD);
            }
        }

        if( distance < ATTACK_DISTANCE ){    // player gets too close, attack mode
            mode = ATTACKING;
            //speed = SPEED;
        }
        if(distance > FORGET_DISTANCE ){    // out of sight
            mode = WANDERING;
        }
        // movement logic
        if(mode == WANDERING){
           // float delta = Gdx.graphics.getDeltaTime();
            wanderTimer -= delta;
            if(wanderTimer <= 0) {
                turn( MathUtils.random(360));
                wanderTimer = 3 + MathUtils.random(6f);
            }
            update(delta);
        }
        if(mode == ATTACKING){
            // move towards player
            faceTowards(player.position);
            update(delta);
            if(distance < KILL_DISTANCE) {   // on top of player, kills player
                player.killedBy(this);
                mode = WANDERING;
            }

            // separation from other zombies
            repelVelocity.set(0,0,0);
            for(Creature otherZombie : creatures){
                if( otherZombie == this)
                    continue;
                if (!(otherZombie instanceof Zombie))
                    continue;
                float d = otherZombie.position.dst(position);
                if( d < MINIMUM_SEPARATION ){   // too close to other zonmbie
                    tmpVec.set(position).sub(otherZombie.position); // vector away from other
                    repelVelocity.add(tmpVec);
                }
            }
        }
    }

}
