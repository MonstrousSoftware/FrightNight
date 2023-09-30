package com.monstrous.frightnight.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import net.mgsx.gltf.scene3d.scene.Scene;

// base class for moving entities (zombies, wolf, car)
// Position etc. are Vector3 even though movement is only in the horizontal plane. Just in case.

public class Creature implements Json.Serializable {
    public int id;
    public String name;
    public Vector3 position;
    private Vector3 forward;         // actual forward unit vector
    public Matrix4 transform;
    private Vector3 targetDir;       // desired forward direction
    private Vector3 turnStart;       // direction at start or turning
    public float speed = 0;
    protected int mode;
    private boolean dead;
    protected Vector3 tmpVec = new Vector3();
    public Creature killedBy;
    public Scene scene;     // for rendering, gdx-gltf equivalent of model instance
    private float turnFraction;
    public float radius;            // for collision testing
    protected Vector3 repelVelocity;    // for boid separation

    public Creature() {
        this.speed = 0;
        this.position = new Vector3();
        this.forward = new Vector3(0,0,1);
        repelVelocity = new Vector3();
        this.targetDir = new Vector3(forward);
        radius = 1f;
        transform = new Matrix4();
        turnForward();
        dead = false;
        turnFraction = 1.0f;
        turnStart = new Vector3();
    }

    public Creature( String name, Vector3 position ) {
        this();
        this.name = name;
        this.position.set(position);
        turnForward();
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("name", name);
        json.writeValue("position", position);
        json.writeValue("forward", forward);
        json.writeValue("transform", transform);
        json.writeValue("targetDir", targetDir);
        json.writeValue("turnStart", turnStart);
        json.writeValue("speed", speed);
        json.writeValue("dead", dead);
        json.writeValue("turnFraction", turnFraction);
        json.writeValue("mode", mode);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        id = json.readValue("id", Integer.class, jsonData);
        name = json.readValue("name", String.class, jsonData);
        position = json.readValue("position", Vector3.class, jsonData);
        forward = json.readValue("forward", Vector3.class, jsonData);
        transform = json.readValue("transform", Matrix4.class, jsonData);
        targetDir = json.readValue("targetDir", Vector3.class, jsonData);
        turnStart = json.readValue("turnStart", Vector3.class, jsonData);
        speed = json.readValue("speed", Float.class, jsonData);
        dead = json.readValue("dead", Boolean.class, jsonData);
        turnFraction = json.readValue("turnFraction", Float.class, jsonData);
        mode = json.readValue("mode", Integer.class, jsonData);
        turnForward(); // update matrix
    }





    public void setDead( boolean dead ){
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    protected void die() { // override for death animation etc.
        dead = true;
        Gdx.app.log("creature died", name+ " at "+position+position);

        forward.set(0,1,0); // float up
        speed = 1;
        turnFraction = 1.1f;
    }

    public void killedBy( Creature killer) {
        Gdx.app.log("creature killed", name+ " by "+killer.name);
        killedBy = killer;
        die();
    }


    public void faceTowards( Vector3 target ){
        targetDir.set(target).sub(position).nor();
        turnFraction = 0;
        turnStart.set(forward);
    }

    public void setForward( Vector3 fwd ){
        targetDir.set(fwd).nor();
        turnFraction = 0;
        turnStart.set(forward);
    }

    public Vector3 getForward() {
        return forward;
    }

    public void turn( float degrees ) {
        targetDir.set(forward);
        targetDir.rotate(Vector3.Y, degrees);
        turnFraction = 0;
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
        if(turnFraction < 1.0f) {
            float turnSpeed = Math.max(speed, 1f);  // turn faster if speed is high to avoid overshoot, also allow turn if not moving
            turnFraction += deltaTime*turnSpeed;
            forward.set(turnStart);
            forward.slerp(targetDir, turnFraction);
        }
        turnForward();

        tmpVec.set(forward).scl(speed);
        tmpVec.add(repelVelocity);
        tmpVec.scl(deltaTime);
        position.add(tmpVec);
        if(isDead())
            transform.rotate(Vector3.X, -90);    // turn to face up
        transform.setTranslation(position);

    }



}
