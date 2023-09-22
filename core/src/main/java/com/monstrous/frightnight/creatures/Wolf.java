package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.HintMessage;
import com.monstrous.frightnight.HintQueue;
import com.monstrous.frightnight.Sounds;

public class Wolf extends Creature {
    public static final int SLEEPING = 0;
    public static final int ALERT = 1;
    public static final int FOLLOWING = 2;
    public static final int ATTACKING = 3;

    public static final int FOLLOW_DISTANCE = 22;
    public static final int ALERT_DISTANCE = 20;
    public static final int FOLLOW_CLOSE_DISTANCE = 12;
    public static final int ATTACK_DISTANCE = 10;
    public static final int KILL_DISTANCE = 1;

    public static float MINIMUM_SEPARATION = 12f;
    public static float SPEED = 4f;
    public static float ATTACK_SPEED = 10f;
    public static boolean firstBark = true;

    public int mode;
    private Creature target;
    private Vector3 tmpVec = new Vector3();

    public Wolf() {

    }
    public Wolf(Vector3 position, Vector3 forward) {
        super("hellhound", position);
        setForward(forward);
        this.mode = SLEEPING;
    }

    public void move(float deltaTime, Sounds sounds, HintQueue hintQueue, Player player, Array<Wolf> wolves, Array<Zombie> zombies ) {
        if(isDead())
            return;

        // change mode based on distance of player or zombie

        // which is closest by?

        float distance = position.dst(player.position);
        Creature closest = player;
        for(Zombie zombie : zombies){
            if(zombie.isDead())
                continue;
            float d = position.dst(zombie.position);
            if(d < distance) {
                closest = zombie;
                distance = d;
            }
        }
        // 'closest' is the closest entity at distance 'distance'


        if( mode != ATTACKING && distance < ATTACK_DISTANCE ){    // player gets too close, attack mode
            mode = Wolf.ATTACKING;
            target = closest;
            sounds.playSound(Sounds.GROWL);
            Gdx.app.log("Wolf goes ATTACKING", target.name);
        }

        if(  mode == SLEEPING && distance < ALERT_DISTANCE ){   // player gets close, wolf is on alert but does not move yet
            sounds.playSound(Sounds.BARK);
            mode = Wolf.ALERT;
            target = closest;
            Gdx.app.log("Wolf goes ALERT", target.name);
            if(firstBark){
                firstBark = false;
                hintQueue.addHint(0.5f, HintMessage.HELL_HOUND);
            }
        }
        if(mode == Wolf.ALERT && position.dst(target.position) > FOLLOW_DISTANCE ){ // player moves away, wolf starts following
            mode = Wolf.FOLLOWING;
            Gdx.app.log("Wolf goes FOLLOWING", target.name);
        }





        // movement logic
        if(mode == Wolf.FOLLOWING){

            faceTowards(target.position);

            speed = SPEED;
            distance = position.dst(target.position);
            if(distance <= FOLLOW_CLOSE_DISTANCE & speed > 0) {
                speed = 0;
                Gdx.app.log("Wolf is FOLLOWING but keeps distance", target.name);
            }

            // separation from other wolves
//            for(int i = 0; i < wolves.size; i++) {
//                Wolf other = wolves.get(i);
//                if(other == this)
//                    continue;
//                if(other.mode == ATTACKING)     // wolf pack mentality
//                    mode = ATTACKING;
//                float d = other.position.dst(position);
//                if( d < MINIMUM_SEPARATION ){   // too close to other wolf
//                    tmpVec.set(other.position).sub(position).nor().scl(-1f);
//                    setForward(tmpVec); // face away from the other one
//                }
//            }
            update(deltaTime);
        }
        if(mode == Wolf.ATTACKING){
            // move quickly towards target
            faceTowards(target.position);
            speed = ATTACK_SPEED;
            update(deltaTime);
            distance = position.dst(target.position);
            if(distance < KILL_DISTANCE && !target.isDead()) {  // on top of target, kills target
                Gdx.app.log("Wolf KILLS", target.name);
                target.killedBy(this);
                mode = SLEEPING;
            }
        }
        if(mode == ALERT) {
            speed = 0;
            faceTowards(target.position);
            update(deltaTime);  // rotate to follow target, but don't move
        }
    }

}
