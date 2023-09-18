package com.monstrous.frightnight;

public class Car extends Creature {

    public static final float WIDTH = 2f;
    public static final float LENGTH= 6f;

    public Car(float x, float y, float z) {
        super(x, y, z);
        forward.set(0, 0, 1);
        speed = 8f;
    }

    public void move( float deltaTime ) { //}, Array<Creature> creatures) {
        moveForward(deltaTime);
        if(position.len() > 100){
            position.scl(-1);
        }
//        for(Creature creature : creatures) {
//            if(creature == this)
//                continue;
//            if(creature.position.x >= position.x-10 && creature.position.x <= position.x+LENGTH+10 &&
//                creature.position.y >= position.y-10 && creature.position.y <= position.y+WIDTH+10 ) {
//                creature.isDead = true;
//            }
//        }

    }
}
