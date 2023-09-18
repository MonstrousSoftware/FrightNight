package com.monstrous.frightnight.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;


// to handle game controllers
// relays events to camera controller


public class MyControllerAdapter extends ControllerAdapter {
    private CamController camController;

    public MyControllerAdapter(CamController camController) {
        super();
        this.camController = camController;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        Gdx.app.log("controller", "button down: "+buttonIndex);

        // map Dpad to WASD
        if(buttonIndex == controller.getMapping().buttonDpadUp)
            camController.keyDown(camController.forwardKey);
        if(buttonIndex == controller.getMapping().buttonDpadDown)
            camController.keyDown(camController.backwardKey);
        if(buttonIndex == controller.getMapping().buttonDpadLeft)
            camController.keyDown(camController.strafeLeftKey);
        if(buttonIndex == controller.getMapping().buttonDpadRight)
            camController.keyDown(camController.strafeRightKey);

        if(buttonIndex == controller.getMapping().buttonL1) // jump
            camController.keyDown(camController.jumpKey);
        if(buttonIndex == controller.getMapping().buttonR1) // crouch
            camController.keyDown(camController.crouchKey);
        return super.buttonDown(controller, buttonIndex);
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        //Gdx.app.log("controller", "button up: "+buttonIndex);

        // map Dpad to WASD
        if(buttonIndex == controller.getMapping().buttonDpadUp)
            camController.keyUp(camController.forwardKey);
        if(buttonIndex == controller.getMapping().buttonDpadDown)
            camController.keyUp(camController.backwardKey);
        if(buttonIndex == controller.getMapping().buttonDpadLeft)
            camController.keyUp(camController.strafeLeftKey);
        if(buttonIndex == controller.getMapping().buttonDpadRight)
            camController.keyUp(camController.strafeRightKey);

        if(buttonIndex == controller.getMapping().buttonL1)
            camController.keyUp(camController.jumpKey);
        if(buttonIndex == controller.getMapping().buttonR1)
            camController.keyUp(camController.crouchKey);
        return super.buttonUp(controller, buttonIndex);
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        //Gdx.app.log("controller", "axis moved: "+axisIndex+" : "+value);

        if(axisIndex == controller.getMapping().axisRightX)     // right stick for looking around (X-axis)
            camController.horizontalAxisMoved(value);           // rotate view left/right
        if(axisIndex == controller.getMapping().axisRightY)     // right stick for looking around (Y-axis)
            camController.verticalAxisMoved(value);           // rotate view left/right

        if(axisIndex == controller.getMapping().axisLeftX)     // left stick for strafing (X-axis)
            camController.setStrafeSpeed(value);
        if(axisIndex == controller.getMapping().axisLeftY)     // right stick for forward/backwards (Y-axis)
            camController.setWalkSpeed(value);
        return super.axisMoved(controller, axisIndex, value);
    }

    @Override
    public void connected(Controller controller) {
        Gdx.app.log("controller", "connected");
        super.connected(controller);
    }

    @Override
    public void disconnected(Controller controller) {
        Gdx.app.log("controller", "disconnected");
        super.disconnected(controller);
    }
}
