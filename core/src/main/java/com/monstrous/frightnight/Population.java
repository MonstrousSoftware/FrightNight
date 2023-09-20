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
    private PopulationScenes populationScenes;

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

    // wolf or zombie
    public void addCreature(Vector3 pos, Vector3 dir, boolean isWolf ){
        if(isWolf) {
            Wolf wolf = new Wolf(pos, dir);
            wolves.add(wolf);
            creatures.add(wolf);
        } else {
            Zombie zombie = new Zombie(pos, dir);
            zombies.add(zombie);
            creatures.add(zombie);
        }
    }

    public void reset(  ) {
        player = new Player(new Vector3(5, 0, -10));    // to track camera

        car = new Car(new Vector3(0, 0, -100), new Vector3(0,0,1), 8.0f);

        wolves.clear();
        zombies.clear();


        // aggregate creatures list
        creatures.clear();
        creatures.add(player);
        creatures.add(car);
//        creatures.addAll(wolves);
//        creatures.addAll(zombies);

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
