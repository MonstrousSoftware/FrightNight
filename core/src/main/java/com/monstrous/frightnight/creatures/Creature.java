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
    private Vector3 forward;         // actual forward unit vector
    private Vector3 targetDir;       // desired forward direction
    private Vector3 turnStart;       // direction at start or turning
    public Matrix4 transform;
    public float radius;            // for collision testing
    public float speed = 0;
    private boolean dead;
    private Vector3 tmpVec = new Vector3();
    public Creature killedBy;
    public Scene scene;     // for rendering, gdx-gltf equivalent of model instance
    private float alpha;

    public Creature( String name, Vector3 position ) {
        this.name = name;
        this.position = new Vector3(position);
        this.forward = new Vector3(0,0,1);
        this.targetDir = new Vector3(forward);
        turnStart = new Vector3();
        this.speed = 0;
        radius = 1f;
        transform = new Matrix4();
        turnForward();
        dead = false;
        alpha = 1.0f;
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
        targetDir.set(target).sub(position).nor();
        alpha = 0;
        turnStart.set(forward);
    }

    public void setForward( Vector3 fwd ){
        targetDir.set(fwd).nor();
        alpha = 0;
        turnStart.set(forward);
    }

    public Vector3 getForward() {
        return forward;
    }

    public void turn( float degrees ) {
        targetDir.set(forward);
        targetDir.rotate(Vector3.Y, degrees);
        alpha = 0;
        turnStart.set(forward);
    }

    // orient model to point to forward vector
    private void turnForward() {
        float degrees = (float)Math.toDegrees(Math.atan2(forward.x, forward.z));
        transform.setToRotation(Vector3.Y, degrees);
        transform.setTranslation(position);
    }

    // move creature according to orientation and speed
    public void update( float deltaTime ) {
        if(alpha < 1.0f) {
            float turnSpeed = Math.max(speed, 1f);  // turn faster if speed is high to avoid overshoot, also allow turn if not moving
            alpha += deltaTime*turnSpeed;
            forward.set(turnStart);
            forward.slerp(targetDir, alpha);
        }
        turnForward();

        tmpVec.set(forward).scl(speed*deltaTime);
        position.add(tmpVec);
        transform.setTranslation(position);
    }



}
