package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
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
    public static final int ATTACK_DISTANCE = 8;
    public static final int KILL_DISTANCE = 1;

    public static final float MINIMUM_SEPARATION = 1f;
    public static final float SPEED = 4f;
    public static final float ATTACK_SPEED = 16f;
    public static boolean firstBark = true;

    public Creature target;
    public int targetId;       // for serialization


    public Wolf() {
    }

    public Wolf(Vector3 position, Vector3 forward) {
        super("hellhound", position);
        setForward(forward);
        this.mode = SLEEPING;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        targetId = -1;
        if(target != null)
            targetId = target.id;
        json.writeValue("targetId", targetId);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        targetId = json.readValue("targetId", Integer.class, jsonData); // to recover 'target'
    }

    public void move(float deltaTime, Sounds sounds, HintQueue hintQueue, Array<Creature> creatures) { //Player player, Array<Wolf> wolves, Array<Zombie> zombies ) {
        if(isDead())
            return;

        // change mode based on distance of player or zombie

        // which is closest by?

        float distance = 999999f;
        Creature closest = null;
        for(Creature creature : creatures){
            if(creature == this || creature.isDead() )
                continue;
            if(! (creature instanceof Zombie || creature instanceof Player ))
                continue;
            float d = position.dst(creature.position);
            if(d < distance) {
                closest = creature;
                distance = d;
            }
        }
        // 'closest' is the closest entity at distance 'distance'


        if( mode != ATTACKING && distance < ATTACK_DISTANCE ){    // player gets too close, attack mode
            mode = Wolf.ATTACKING;
            target = closest;
            sounds.playSound(Sounds.GROWL);
            //Gdx.app.log("Wolf goes ATTACKING", target.name);
            //scene.animationController.setAnimation("WolfAttack", -1);
        }

        if(  mode == SLEEPING && distance < ALERT_DISTANCE ){   // player gets close, wolf is on alert but does not move yet
            sounds.playSound(Sounds.BARK);
            mode = Wolf.ALERT;
            target = closest;
            //Gdx.app.log("Wolf goes ALERT", target.name);
            if(firstBark){
                firstBark = false;
                hintQueue.showMessage(0.5f, HintMessage.HELL_HOUND);
            }
            //scene.animationController.setAnimation("WolfAlert", -1);
        }
        if(mode == Wolf.ALERT && position.dst(target.position) > FOLLOW_DISTANCE ){ // player moves away, wolf starts following
            mode = Wolf.FOLLOWING;
            //Gdx.app.log("Wolf goes FOLLOWING", target.name);
            scene.animationController.setAnimation("WolfWalk", -1);
        }

        // movement logic
        if(mode == Wolf.FOLLOWING){

            if(target.isDead()) {
                mode = SLEEPING;
                scene.animationController.setAnimation("WolfRestPose", -1);
            } else {
                faceTowards(target.position);

                speed = SPEED;
                distance = position.dst(target.position);
                if (distance <= FOLLOW_CLOSE_DISTANCE & speed > 0) {
                    speed = 0;
                    //Gdx.app.log("Wolf is FOLLOWING but keeps distance", target.name);
                }

                // separation from other wolves
                repelVelocity.set(0, 0, 0);
                for (Creature other : creatures) {
                    if (other == this)
                        continue;
                    if (!(other instanceof Wolf))
                        continue;
                    float d = other.position.dst(position);
                    if (d < MINIMUM_SEPARATION) {   // too close to other wolf
                        tmpVec.set(position).sub(other.position); // vector away from other
                        tmpVec.scl(0.5f);
                        repelVelocity.add(tmpVec);
                    }
                }
                update(deltaTime);
            }
        }
        if(mode == Wolf.ATTACKING){
            // move quickly towards target
            if(target.isDead()) {
                mode = SLEEPING;
                scene.animationController.setAnimation("WolfRestPose", -1);
            } else {
                faceTowards(target.position);
                speed = ATTACK_SPEED;
                update(deltaTime);
                distance = position.dst(target.position);
                if (distance < KILL_DISTANCE && !target.isDead()) {  // on top of target, kills target
                    Gdx.app.log("Wolf KILLS", target.name);
                    target.killedBy(this);
                    mode = SLEEPING;
                    scene.animationController.setAnimation("WolfRestPose", -1);
                }
            }
        }
        if(mode == ALERT) {
            speed = 0;
            if(target.isDead()) {
                mode = SLEEPING;
                scene.animationController.setAnimation("WolfRestPose", -1);
            } else
                faceTowards(target.position);
            update(deltaTime);  // rotate to follow target, but don't move
        }
    }

}
