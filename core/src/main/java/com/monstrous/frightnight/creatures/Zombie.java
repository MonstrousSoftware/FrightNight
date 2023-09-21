package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;
import com.monstrous.frightnight.Sounds;

public class Zombie extends Creature {
    public static final int WANDERING = 0;
    public static final int ATTACKING = 1;

    public static final int FORGET_DISTANCE = 20;
    public static final int ATTACK_DISTANCE = 10;
    public static final int KILL_DISTANCE = 1;

    public static float MINIMUM_SEPARATION = 5;
    public static float SPEED = 0.2f;

    public int mode;
    private Vector3 tmpVec = new Vector3();
    private float wanderTimer;
    private static boolean firstZombie = true;

    public Zombie(Vector3 position, Vector3 forward) {
        super("zombie", position);
        setForward(forward);
        this.mode = WANDERING;
        speed = SPEED;
    }
    public void move(float delta, Sounds sounds, HintQueue hintQueue, Player player, Array<Zombie> zombies ) {
        if(isDead())
            return;


        // change mode based on distance of player

        float distance = position.dst(player.position);

        if(firstZombie && distance < 50 ){
            Vector3 view = new Vector3(position).sub(player.position);  // view vector from player to zombie
            float dot = view.dot(player.getForward());  // angle with player's forward direction
            if(dot > 0.3f) {  // player is able to see zombie
                firstZombie = false;
                hintQueue.addHint(0f, HintMessage.UNDEAD);
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
