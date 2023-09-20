package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Zombie extends Creature {
    public static final int WANDERING = 0;
    public static final int ATTACKING = 1;

    public static final int FORGET_DISTANCE = 20;
    public static final int ATTACK_DISTANCE = 10;
    public static final int KILL_DISTANCE = 1;

    public static float MINIMUM_SEPARATION = 5;
    public static float SPEED = 0.7f;

    public int mode;
    private Vector3 tmpVec = new Vector3();
    private float wanderTimer;

    public Zombie(Vector3 position, Vector3 forward) {
        super("zombie", position);
        setForward(forward);
        this.mode = WANDERING;
        speed = SPEED;
    }
    public void move(float delta, Player player, Array<Zombie> zombies ) {
        if(isDead())
            return;


        // change mode based on distance of player

        float distance = position.dst(player.position);
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
            moveForward(delta);
        }
        if(mode == ATTACKING){
            // move towards player
            faceTowards(player.position);
            moveForward(delta);
            if(distance < KILL_DISTANCE) {   // on top of player, kills player
                player.killedBy(this);
                mode = WANDERING;
            }


            // separation from other zombies
            for(int i = 0; i < zombies.size; i++) {
                Zombie other = zombies.get(i);
                if(other == this)
                    continue;
                float d = other.position.dst(position);
                if( d < MINIMUM_SEPARATION ){   // too close to other one
                    tmpVec.set(position).sub(other.position).nor().scl(MINIMUM_SEPARATION);
                    position.add(tmpVec);
                }
            }
        }
    }

}
