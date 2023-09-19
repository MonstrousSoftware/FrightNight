package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

// base class for moving entities (zombies, wolf, car)
// Position etc. are Vector3 even though movement is only in the horizontal plane. Just in case.

public class Creature {
    public String name;
    public Vector3 position;
    public Vector3 forward;         // forward unit vector
    public Matrix4 transform;
    public float speed = 0;
    private boolean dead;
    private Vector3 tmpVec = new Vector3();
    public Creature killedBy;

    public Creature( String name, Vector3 position ) {
        this.name = name;
        this.position = new Vector3(position);
        this.forward = new Vector3();
        this.speed = 0;
        transform = new Matrix4();
        turnForward();
        dead = false;
    }

    public void setDead( boolean dead ){
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }
    public void die() { // override for death animation etc.
        dead = true;
        Gdx.app.log("creature died", name+ " at "+position+position);
    }

    public void killedBy( Creature killer) {
        Gdx.app.log("creature killed", name+ " by "+killer.name);
        killedBy = killer;
        die();
    }
    // orient model to point to forward vector
    public void turnForward() {
        float degrees = (float)Math.toDegrees(Math.atan2(forward.x, forward.z));
        transform.setToRotation(Vector3.Y, degrees);
        transform.setTranslation(position);
    }

    public void moveForward( float deltaTime ) {
        tmpVec.set(forward).scl(speed*deltaTime);
        position.add(tmpVec);
        transform.setTranslation(position);
    }


}
