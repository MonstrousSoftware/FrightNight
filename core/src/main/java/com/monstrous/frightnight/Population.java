package com.monstrous.frightnight;

// manages all moving creatures

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.creatures.*;

public class Population {
    public Array<Wolf> wolves;
    public Array<Zombie> zombies;
    public Array<Creature> creatures;      // generic class for wolves, zombies and car
    private Car car;
    private Player player;
    private boolean gameOver;

    public Population() {
        creatures = new Array<>();
        wolves = new Array<>();
        zombies = new Array<>();



        reset();
    }

    public Player getPlayer() {
        return player;
    }

    public Car getCar() {
        return car;
    }

    public Wolf getWolf() {
        return wolves.first();
    }

    public Zombie getZombie() {
        return zombies.first();
    }

    public void reset() {
        player = new Player(new Vector3(5, 0, -10));    // to track camera

        car = new Car(new Vector3(0, 0, -100), new Vector3(0,0,1), 8.0f);

        wolves.clear();
        //for(int x = 300; x <=  500; x += 50) {
            wolves.add( new Wolf(new Vector3(-4, 0, 28), Vector3.Z));
        wolves.add( new Wolf(new Vector3(-8, 0, 50), Vector3.Z));
        //}
        zombies.clear();
        zombies.add( new Zombie(Vector3.Zero, Vector3.Z));
        for(int x = 30; x <=  130; x += 8) {
            zombies.add( new Zombie(new Vector3(x, 0, 0), Vector3.Z));
        }
//        for(int x = 325; x <=  500; x += 100) {
//            zombies.add( new Zombie(x, 450, Zombie.WANDERING));
//        }
//        for(int x = 300; x <=  500; x += 100) {
//            zombies.add( new Zombie(x, 400, Zombie.WANDERING));
//        }

        // aggregate creatures list
        creatures.clear();
        creatures.add(player);
        creatures.add(car);
        creatures.addAll(wolves);
        creatures.addAll(zombies);

        gameOver = false;
    }


    private void moveWolves(float deltaTime) {
        for(Wolf wolf : wolves )
            wolf.move(deltaTime, player, wolves, zombies);
    }

    private void moveZombies(float deltaTime) {
        for(Zombie zombie : zombies )
            zombie.move(deltaTime, player, zombies);
    }



    public void update(Vector3 playerPosition, float deltaTime ) {
        //Gdx.app.log("population.update", "gameOver"+gameOver);

        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();

        player.position.set( playerPosition ); // let player entity follow FPS camera
        player.position.y = 0;  // set entity position at ground level, not eye level

        if(!gameOver) {
            float delta = 0.05f; //Gdx.graphics.getDeltaTime();

            moveWolves(delta);
            moveZombies(delta);
            car.move(delta, creatures);
//            if(player.isDead)
//                gameOver = true;
        }
    }



    public void dispose() {

    }
}
