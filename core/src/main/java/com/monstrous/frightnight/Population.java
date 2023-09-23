package com.monstrous.frightnight;

// manages all moving creatures

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.monstrous.frightnight.creatures.*;

import java.io.StringWriter;

public class Population {
    public Array<Wolf> wolves;
    public Array<Zombie> zombies;
    public Array<Creature> creatures;      // generic class for wolves, zombies and car
    private Car car;
    private Player player;
    private boolean gameOver;
    private PopulationScenes populationScenes;
    private Sounds sounds;
    private boolean zombiesDead;

    public Population(Sounds sounds) {
        this.sounds = sounds;

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

        car = new Car(new Vector3(0, 0, -100), new Vector3(0,0,1));

        wolves.clear();
        zombies.clear();


        // aggregate creatures list
        creatures.clear();
        creatures.add(player);
        creatures.add(car);

        gameOver = false;
        zombiesDead = false;
    }


    private void moveWolves(float deltaTime, HintQueue hintQueue) {
        for(Wolf wolf : wolves )
            wolf.move(deltaTime, sounds, hintQueue, creatures); //player, wolves, zombies);
    }

    private void moveZombies(float deltaTime, HintQueue hintQueue) {
        for(Zombie zombie : zombies )
            zombie.move(deltaTime, sounds, hintQueue, player, creatures);
    }



    public void update(Vector3 playerPosition, HintQueue hintQueue, float deltaTime ) {
        //Gdx.app.log("population.update", "gameOver"+gameOver);

        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();

        player.position.set( playerPosition ); // let player entity follow FPS camera
        player.position.y = 0;  // set entity position at ground level, not eye level

        if(!gameOver) {
            float delta = deltaTime; //0.05f; //Gdx.graphics.getDeltaTime();

            moveWolves(delta, hintQueue);
            moveZombies(delta, hintQueue);
            car.move(delta, hintQueue, player, creatures);
            player.move(hintQueue);

            int zombieCount = 0;
            for(Zombie zombie : zombies)
                if(!zombie.isDead())
                    zombieCount++;
            int wolfCount = 0;
            for(Wolf wolf : wolves)
                if(!wolf.isDead())
                    wolfCount++;

            if(!zombiesDead && zombieCount == 0){
                zombiesDead = true;
                if(wolfCount > 0)
                    hintQueue.addHint(2f, HintMessage.NO_CREATURE); // all zombies are dead, now kill the wolves
            }

            if(zombieCount == 0 && wolfCount == 0)
                car.makePickup();

//            if(player.isDead)
//                gameOver = true;
        }
    }

    public void save(String filename) {
        Gdx.app.log("quick save", filename);

        Json json = new Json(JsonWriter.OutputType.json);
        JsonWriter writer = new JsonWriter( new StringWriter() );
        json.setWriter(writer);

        FileHandle file = Gdx.files.local(filename);
        file.writeString("", false);    // overwrite

        String s = json.prettyPrint(creatures);
        file.writeString(s, true); // append
    }

    public void load(String filename) {
        Gdx.app.log("quick load", filename);

        Json json = new Json();
        FileHandle file = Gdx.files.local(filename);
        json.addClassTag("Creature", Creature.class);
        creatures = json.fromJson(Array.class, Creature.class, file);
        for(Creature creature : creatures){
            if(creature instanceof Player)
                player = (Player)creature;
            if(creature instanceof Car)
                car = (Car) creature;
            if(creature instanceof Wolf)
                wolves.add((Wolf)creature);
            if(creature instanceof Zombie)
                zombies.add((Zombie)creature);
        }

    }

    public void dispose() {

    }
}
