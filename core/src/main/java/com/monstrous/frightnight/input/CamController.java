package com.monstrous.frightnight.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.monstrous.frightnight.Settings;
import com.monstrous.frightnight.Sounds;

// derived from FirstPersonCameraController

public class CamController extends InputAdapter {
    final static float CAM_HEIGHT = 1.5f;       // meters

    final static float WALK_SPEED = 15f;
    final static float TURN_SPEED = 120f;
    final static float BOB_DURATION = 0.6f;     // seconds
    final static float BOB_HEIGHT = 0.04f;      // m
    final static float BOB_WIDTH = 0.03f;      // m

    protected final Camera camera;
    protected final IntIntMap keys = new IntIntMap();
    public int strafeLeftKey = Input.Keys.Q;
    public int strafeRightKey = Input.Keys.E;
    public int turnLeftKey = Input.Keys.A;
    public int turnRightKey = Input.Keys.D;
    public int forwardKey = Input.Keys.W;
    public int backwardKey = Input.Keys.S;
    public int jumpKey = Input.Keys.SPACE;
    public int crouchKey = Input.Keys.C;
    public boolean autoUpdate = true;
    //protected static float final SPEED = 5;                   // todo via Settings
    protected float degreesPerPixel = 0.1f;
    protected final Vector3 tmp = new Vector3();
    protected final Vector3 tmp2 = new Vector3();
    protected final Vector3 tmp3 = new Vector3();
    protected final Vector3 fwdHorizontal = new Vector3();
    private boolean isJumping;
    private boolean isCrouching;
    private float jumpH;
    private float jumpV;
    private float bobAngle;
    private float horizontalRotationSpeed;
    private float verticalRotationSpeed;
    private float walkSpeed;
    private float strafeSpeed;
    private Vector3 prevPosition;


    public CamController(Camera camera) {
        this.camera = camera;
        bobAngle = 0;
        isJumping = false;
        isCrouching = false;
        camera.position.y = CAM_HEIGHT;
        horizontalRotationSpeed = 0;
        verticalRotationSpeed = 0;
        walkSpeed = 0;
        strafeSpeed = 0;
        prevPosition = new Vector3();
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }



    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
        if(Settings.invertLook)
            deltaY = -deltaY;
        if(!Settings.freeLook) {    // keep camera movement in the horizontal plane
            deltaY = 0;
            camera.direction.y = 0;
        }
        camera.direction.rotate(camera.up, deltaX);

        // avoid gimbal lock when looking straight up or down
        Vector3 oldPitchAxis = tmp.set(camera.direction).crs(camera.up).nor();
        Vector3 newDirection = tmp2.set(camera.direction).rotate(tmp, deltaY);
        Vector3 newPitchAxis = tmp3.set(tmp2).crs(camera.up);
        if (!newPitchAxis.hasOppositeDirection(oldPitchAxis))
            camera.direction.set(newDirection);

        return true;
    }

    // Game controller interface
    //
    //

    // rotate view left/right
    // we only get events when the stick angle changes so once it is fully left or fully right we don't get events anymore until the stick is released.
    public void horizontalAxisMoved(float value) {       // -1 to 1
        horizontalRotationSpeed = value*2f;
    }

    public void verticalAxisMoved(float value) {       // -1 to 1
        if(Settings.invertLook)
            value = -value;
        if(!Settings.freeLook) {    // keep camera movement in the horizontal plane
            value = 0;
            camera.direction.y = 0;
        }
        verticalRotationSpeed = value;
    }

    public void setWalkSpeed(float value){
        walkSpeed = -value * 5f;
    }

    public void setStrafeSpeed(float value){
        strafeSpeed = -value * 5f;
    }
    ///////////////////

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        prevPosition.set(camera.position);

        fwdHorizontal.set(camera.direction).y = 0;
        fwdHorizontal.nor();
        float bobSpeed= 0;


        camera.direction.rotate(camera.up, -horizontalRotationSpeed);
        tmp.set(camera.direction).crs(camera.up).nor();
        camera.direction.rotate(tmp, -verticalRotationSpeed);

        tmp.set(fwdHorizontal).scl(deltaTime * walkSpeed);
        camera.position.add(tmp);
        tmp.set(fwdHorizontal).crs(camera.up).nor().scl(-deltaTime * strafeSpeed);
        camera.position.add(tmp);


        bobSpeed += Math.abs(walkSpeed);   // for head bobbing
        bobSpeed += Math.abs(strafeSpeed);


        if (keys.containsKey(forwardKey)) {
            tmp.set(fwdHorizontal).scl(deltaTime * WALK_SPEED);
            camera.position.add(tmp);
            bobSpeed = 1;
        }
        if (keys.containsKey(backwardKey)) {
            tmp.set(fwdHorizontal).scl(-deltaTime * WALK_SPEED);
            camera.position.add(tmp);
            bobSpeed = -1;
        }
        if (keys.containsKey(strafeLeftKey)) {
            tmp.set(fwdHorizontal).crs(camera.up).nor().scl(-deltaTime * WALK_SPEED);
            camera.position.add(tmp);
            bobSpeed = 1;
        }
        if (keys.containsKey(strafeRightKey)) {
            tmp.set(fwdHorizontal).crs(camera.up).nor().scl(deltaTime * WALK_SPEED);
            camera.position.add(tmp);
            bobSpeed = 1;
        }
        if (keys.containsKey(turnLeftKey)) {
            camera.direction.rotate(camera.up, deltaTime*TURN_SPEED);
        }
        if (keys.containsKey(turnRightKey)) {
            camera.direction.rotate(camera.up, -deltaTime*TURN_SPEED);
        }
        if (keys.containsKey(jumpKey) && !isJumping) {
            isJumping = true;

            jumpV = 6;
            jumpH = 0;
        }
        if (keys.containsKey(crouchKey) && !isJumping) {
            isCrouching = true;
            jumpH = -0.5f;
        }
        else if(isCrouching){
            isCrouching = false;
            jumpH = 0;
        }

        //camera.position.y = CAM_HEIGHT;

        if(isJumping){
            bobSpeed = 0;
            jumpV -= deltaTime*20;
            jumpH += jumpV*deltaTime;
            if(jumpH < 0){
                isJumping = false;
                jumpH = 0;
            }
        }


        // don't allow walking off world
        if(Math.abs(camera.position.x) > 180f || Math.abs(camera.position.z) > 180f)
            camera.position.set(prevPosition);

        camera.position.y = CAM_HEIGHT + jumpH + bobHeight( bobSpeed, deltaTime); // apply some head bob if we're moving
        if (autoUpdate) camera.update(true);
    }

    private boolean playing = false;

    private float bobHeight(float speed, float deltaTime ) {

        float bobHeight = 0;
        if(Math.abs(speed) > 0.1f ) {
            bobAngle += deltaTime * 2.0f * Math.PI / BOB_DURATION;
            bobAngle += MathUtils.random(0.4f) - 0.2f;  // add bit of noise to the angle


            // move the head up and down in a sine wave
            bobHeight = (float) (BOB_HEIGHT * Math.sin(bobAngle));

//            // move eye point side to side in a cosine wave
//            vTmp.set(forward).crs(Vector3.Y).nor(); // sideways vector
//            vTmp.scl( BOB_WIDTH * (float)Math.cos(bobAngle/2f) );   // half the rate of up/down bobbing
//            eyePosition.add(vTmp);
        }
        return bobHeight;
    }
}
