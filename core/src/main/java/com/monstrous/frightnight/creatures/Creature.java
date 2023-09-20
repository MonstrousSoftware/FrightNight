package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.scene.Scene;

// base class for moving entities (zombies, wolf, car)
// Position etc. are Vector3 even though movement is only in the horizontal plane. Just in case.

public class Creature {
    public String name;
    public Vector3 position;
    private Vector3 forward;         // forward unit vector
    public Matrix4 transform;
    public float radius;            // for collision testing
    public float speed = 0;
    private boolean dead;
    private Vector3 tmpVec = new Vector3();
    public Creature killedBy;
    public Scene scene;     // for rendering, gdx-gltf equivalent of model instance

    public Creature( String name, Vector3 position ) {
        this.name = name;
        this.position = new Vector3(position);
        this.forward = new Vector3(0,0,1);
        this.speed = 0;
        radius = 1f;
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


    public void faceTowards( Vector3 target ){
        forward.set(target).sub(position).nor();
        turnForward();
    }

    public void setForward( Vector3 fwd ){
        forward.set(fwd).nor();
        turnForward();
    }

    public Vector3 getForward() {
        return forward;
    }

    public void turn( float degrees ) {
        forward.rotate(Vector3.Y, degrees);
        turnForward();
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
