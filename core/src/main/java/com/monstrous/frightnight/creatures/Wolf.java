package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.Sounds;

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
        setForward(forward);
        this.mode = SLEEPING;
    }

    public void move(float deltaTime, Sounds sounds, Player player, Array<Wolf> wolves, Array<Zombie> zombies ) {
        if(isDead())
            return;

        // change mode based on distance of player or zombie
        float smallestDistance = 99999f;

        float distance = position.dst(player.position);
        //Gdx.app.log("Wolf player distance", ""+distance);
        if(  mode != ALERT && mode != ATTACKING && distance < ALERT_DISTANCE ){   // player gets close, wolf is on alert but does not move yet
            Gdx.app.log("Wolf goes ALERT", "");
            sounds.playSound(Sounds.BARK);
            mode = Wolf.ALERT;
            target = player;
            speed = 0;
            faceTowards(player.position);
        }
        if(mode == Wolf.ALERT && target == player && distance > FOLLOW_DISTANCE ){ // player moves away, wolf starts following
            Gdx.app.log("Wolf goes FOLLOWING", target.name);
            mode = Wolf.FOLLOWING;
            speed = SPEED;
            faceTowards(player.position);
        }
        if( mode != ATTACKING && distance < ATTACK_DISTANCE ){    // player gets too close, attack mode
            mode = Wolf.ATTACKING;
            sounds.playSound(Sounds.GROWL);
            target = player;
            Gdx.app.log("Wolf goes ATTACKING", target.name);
            smallestDistance = distance;
        }

        for(Zombie zombie : zombies){
            if(zombie.isDead())
                continue;
            distance = position.dst(zombie.position);
            if( distance < ATTACK_DISTANCE ){    // zombie gets too close, attack mode
                if(mode != ATTACKING) {
                    sounds.playSound(Sounds.GROWL);
                    mode = Wolf.ATTACKING;
                    Gdx.app.log("Wolf goes ATTACKING", target.name);
                }
                if(distance < smallestDistance) {
                    target = zombie;
                    smallestDistance = distance;
                }
            }
        }


        // movement logic
        if(mode == Wolf.FOLLOWING){

            faceTowards(player.position);

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
                    setForward(tmpVec); // face away from the other one
                }
            }
            update(deltaTime);
        }
        if(mode == Wolf.ATTACKING){
            // move quickly towards target
            faceTowards(player.position);
            speed = ATTACK_SPEED;
            distance = position.dst(target.position);
            update(deltaTime);

            if(distance < KILL_DISTANCE && !target.isDead()) {  // on top of target, kills target
                Gdx.app.log("Wolf KILLS", target.name);
                target.killedBy(this);
                mode = ALERT;
            }
        }
        if(mode == ALERT) {
            faceTowards(player.position);
            update(deltaTime);  // rotate to follow target, but don't move
        }
    }

}
