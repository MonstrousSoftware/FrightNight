package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Wolf extends Creature {
    public static final int SLEEPING = 0;
    public static final int ALERT = 1;
    public static final int FOLLOWING = 2;
    public static final int ATTACKING = 3;

    public static final int FOLLOW_DISTANCE = 30;
    public static final int ALERT_DISTANCE = 20;
    public static final int ATTACK_DISTANCE = 10;
    public static final int KILL_DISTANCE = 2;

    public static float MINIMUM_SEPARATION = 12f;
    public static float SPEED = 4f;
    public static float ATTACK_SPEED = 6f;

    public int mode;
    private Creature target;
    private Vector3 tmpVec = new Vector3();


    public Wolf(Vector3 position, Vector3 forward) {
        super("hellhound", position);
        this.forward.set(forward).nor();
        this.mode = SLEEPING;
    }

    public void move(float deltaTime, Player player, Array<Wolf> wolves, Array<Zombie> zombies ) {
        if(isDead())
            return;

        // change mode based on distance of player or zombie
        float smallestDistance = 99999f;

        float distance = position.dst(player.position);
        //Gdx.app.log("Wolf player distance", ""+distance);
        if(  mode != ALERT && distance < ALERT_DISTANCE ){   // player gets close, wolf is on alert but does not move yet
            Gdx.app.log("Wolf goes ALERT", "");
            mode = Wolf.ALERT;
            target = player;
            speed = 0;
            forward.set(player.position).sub(position).nor();
            turnForward();
        }
        if(mode == Wolf.ALERT && target == player && distance > FOLLOW_DISTANCE ){ // player moves away, wolf starts following
            Gdx.app.log("Wolf goes FOLLOWING", target.name);
            mode = Wolf.FOLLOWING;
            speed = SPEED;
            forward.set(player.position).sub(position).nor();
            turnForward();
        }
        if( mode != ATTACKING && distance < ATTACK_DISTANCE ){    // player gets too close, attack mode
            mode = Wolf.ATTACKING;
            target = player;
            smallestDistance = distance;
        }

        for(Zombie zombie : zombies){
            if(zombie.isDead())
                continue;
            distance = position.dst(zombie.position);
            if( distance < ATTACK_DISTANCE ){    // zombie gets too close, attack mode
                mode = Wolf.ATTACKING;
                if(distance < smallestDistance) {
                    target = zombie;
                    smallestDistance = distance;
                }
            }
        }
        if(mode == Wolf.ATTACKING)
            Gdx.app.log("Wolf goes ATTACKING", target.name);

        // movement logic
        if(mode == Wolf.FOLLOWING){

            forward.set(player.position).sub(position).nor();
            speed = SPEED;

            // separation from other wolves
            for(int i = 0; i < wolves.size; i++) {
                Wolf other = wolves.get(i);
                if(other == this)
                    continue;
                if(other.mode == ATTACKING)     // wolf pack mentality
                    mode = ATTACKING;
                float d = other.position.dst(position);
                if( d < MINIMUM_SEPARATION ){   // too close to other wolf
                    tmpVec.set(other.position).sub(position).nor().scl(-1f);
                    forward.add(tmpVec).nor();
                    turnForward();
                }
            }
            moveForward(deltaTime);
        }
        if(mode == Wolf.ATTACKING){
            // move quickly towards target
            forward.set(target.position).sub(position).nor();
            turnForward();
            speed = ATTACK_SPEED;
            distance = position.dst(target.position);
            moveForward(deltaTime);

            if(distance < KILL_DISTANCE && !target.isDead()) {  // on top of target, kills target
                Gdx.app.log("Wolf KILLS", target.name);
                target.killedBy(this);
                mode = ALERT;
            }
        }
    }

}
