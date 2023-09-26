package com.monstrous.frightnight;

// manages all moving creatures

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
    private boolean allZombiesDead;
    public int zombieKills;
    public int wolfKills;

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

    // wolf or zombie
    public void addCreature(Vector3 pos, Vector3 dir, boolean isWolf, boolean isPlayer ){
        if(isWolf) {
            Wolf wolf = new Wolf(pos, dir);
            wolf.id = creatures.size;
            wolves.add(wolf);
            creatures.add(wolf);
        } else if (!isPlayer){
            Zombie zombie = new Zombie(pos, dir);
            zombie.id = creatures.size;
            zombies.add(zombie);
            creatures.add(zombie);
        }
        else {
            player = new Player(pos, dir);
            player.id = creatures.size;
            creatures.add(player);
        }
    }

    public void reset(  ) {
        //player = new Player(new Vector3(5, 0, -10));    // to track camera

        car = new Car(new Vector3(0, 0, -100), new Vector3(0,0,1));

        wolves.clear();
        zombies.clear();


        // aggregate creatures list
        creatures.clear();
        car.id = creatures.size;
        creatures.add(car);

        gameOver = false;
        allZombiesDead = false;
        zombieKills = 0;
        wolfKills = 0;
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
            zombieKills = zombies.size - zombieCount;
            wolfKills = wolves.size - wolfCount;

            if(!allZombiesDead && zombieCount == 0){
                allZombiesDead = true;
                if(wolfCount > 0)
                    hintQueue.showMessage(2f, HintMessage.NO_CREATURE); // all zombies are dead, now kill the wolves
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
            if(creature instanceof Wolf) {
                Wolf wolf = (Wolf)creature;
                if(wolf.targetId >= 0)
                    wolf.target = creatures.get(wolf.targetId);
                wolves.add(wolf);
            }
            if(creature instanceof Zombie)
                zombies.add((Zombie)creature);

        }

    }

    public void dispose() {

    }
}
