package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.screens.GUI;

// todo save load messages should get priority
public class HintQueue {

    private static final float MESSAGE_DURATION = 8f;   // must be in line with gui.showMessage()

    private float time;
    private float endTime;			// time when on-screen message ends, < time when there is nothing on screen
    private Array<Notification> queue;


    static class Notification {
        float startTime;
        boolean priority;
        HintMessage message;
    }


    public HintQueue() {

        queue = new Array<>();
    }


    public void reset() {
        time = 0;
        endTime = -1;
        queue.clear();
    }

    public void update(float deltaTime, GUI gui, Sounds sounds ) {
        time += deltaTime;

        if(queue.size == 0)
            return;
        Notification note = queue.first();

        if( time < endTime && !note.priority)	// wait till earlier message has gone
            return;

        if(time >= note.startTime) {
            Gdx.app.log("Hint Message", note.message.text);
            if(Settings.enableHints || note.message == HintMessage.GLORY)
                gui.showMessage( note.message.text, note.priority );
            if(Settings.enableNarrator)
                sounds.playSound(note.message.soundId);
            endTime = time + MESSAGE_DURATION;
            queue.removeIndex(0);
        }


    }

    public void addHint(float delay, HintMessage msg ){
        Notification note = new Notification();
        note.startTime = time+delay;
        note.priority = (delay < 0);
        note.message = msg;
        queue.add(note);
        // sort if we think notes will arrive out of sequence
    }

}