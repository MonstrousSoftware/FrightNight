package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.monstrous.frightnight.cornfield.DecalCornField;
import com.monstrous.frightnight.creatures.Car;
import com.monstrous.frightnight.creatures.Creature;
import com.monstrous.frightnight.creatures.Wolf;
import com.monstrous.frightnight.creatures.Zombie;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class PopulationScenes {

    public static String GLTF_FILE = "models/frightnight.gltf";

    private SceneAsset sceneAsset;
    private SceneManager sceneManager;
    //private Scene carScene;
    private Scene wheelScene0, wheelScene1, wheelScene2, wheelScene3;
    private float wheelAngle;
    private Vector3 tmpVec = new Vector3();
    private Population population;
    private boolean carRendersOpen;


    public PopulationScenes(Population population, SceneAsset sceneAsset, SceneManager sceneManager ) {
        this.sceneManager = sceneManager;
        this.sceneAsset = sceneAsset;
        this.population = population;
        carRendersOpen = false;
    }



    // use position and orientation from place-holder to create a creature
    private void loadCreature(String name, boolean isWolf, boolean isPlayer){
        Scene scene = new Scene(sceneAsset.scene, name);        // load a place-holder object to obtain position and orientation, it is not added to the scene manager
        Vector3 pos = new Vector3();
        Vector3 dir = new Vector3(0, 0, 1);
        Matrix4 transform = scene.modelInstance.nodes.first().globalTransform;
        transform.getTranslation(pos);
        pos.y = 0;
        dir.rot(transform).nor();
        population.addCreature(pos, dir,isWolf, isPlayer);
    }

    // create wolves in the population based on placeholder scenes in the blender file
    public void loadFromAssetFile(SceneAsset sceneAsset) {
        // follows object names in Blender
        loadCreature("PlayerSpawn", false, true);

        loadCreature("W1", true, false);
        loadCreature("W1.001", true, false);
        loadCreature("W1.002", true, false);
        loadCreature("W1.003", true, false);

        loadCreature("Z1", false, false);
        loadCreature("Z1.001", false, false);
        loadCreature("Z1.002", false, false);
        loadCreature("Z1.003", false, false);
        loadCreature("Z1.004", false, false);

    }

    // add scenes to creatures in population (except player)
    public void reset() {

        for(Wolf wolf : population.wolves) {
            Scene scene = new Scene(sceneAsset.scene, "HellHound");
            scene.modelInstance.transform.set(wolf.transform);
            sceneManager.addScene(scene);
            wolf.scene = scene;
        }

        for(Zombie zombie : population.zombies) {
//            Scene scene = new Scene(sceneAsset.scene, "zombieArmature");
//            scene.modelInstance.transform.set(zombie.transform);
//            sceneManager.addScene(scene);
//            zombie.scene = scene;

            String armature = "ArmatureZombie";
            Scene scene = new Scene(sceneAsset.scene, "zombie", armature );
            if(armature != null) {
                Gdx.app.log("GameObjectType",  " armature: "+armature + " animations: "+scene.modelInstance.animations.size);
                for(int i = 0; i < scene.modelInstance.animations.size; i++) {
                    String id = scene.modelInstance.animations.get(i).id;
                    Gdx.app.log(" animation :", id);
                }
            }
            scene.modelInstance.transform.set(zombie.transform);
            scene.animationController.setAnimation("Walk", -1);
            scene.animationController.update(MathUtils.random(60f));            // advance some random amount to get zombies out of synch
            sceneManager.addScene(scene);
            zombie.scene = scene;
        }

        Scene carScene = new Scene(sceneAsset.scene, "car");
        sceneManager.addScene(carScene);
        population.getCar().scene = carScene;
        Scene carAltScene = new Scene(sceneAsset.scene, "carOpen");
        population.getCar().altScene = carAltScene;

        wheelScene0 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene0);
        wheelScene1 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene1);
        wheelScene2 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene2);
        wheelScene3 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene3);
    }

    public void update(float deltaTime) {

        updateCarScene(population.getCar(), deltaTime);

        for(Creature creature: population.creatures) {
            if(creature.name.contentEquals("hellhound") || creature.name.contentEquals("zombie")) {
                creature.scene.modelInstance.transform.set(creature.transform);
                if(creature.isDead())
                    sceneManager.removeScene(creature.scene);
            }
        }

    }

    private void swapCarScenes(Car car) {
        Scene swap = car.scene;
        sceneManager.removeScene(car.scene);
        car.scene = car.altScene;
        car.altScene = swap;
        sceneManager.addScene(car.scene);

    }

    private void updateCarScene(Car car, float deltaTime) {
        float wx = 1f;
        float wz = 1.83f;
        float wy = 0.37f;

        // replace car mode with the 'door open' model when it is stopped
        if(!carRendersOpen && car.isWaitingForPlayer()) {
            swapCarScenes(car);
            carRendersOpen = true;
        }
        if(carRendersOpen && !car.isWaitingForPlayer()) {
            swapCarScenes(car);
            carRendersOpen = false;
        }


        car.scene.modelInstance.transform.setTranslation(car.position);

        wheelAngle += car.speed * 90f * deltaTime;

        wheelScene0.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z+wz);
        wheelScene1.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z+wz);
        wheelScene2.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z-wz);
        wheelScene3.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z-wz);

    }


    //        String armature = "Armature";
//        sceneAsset = assets.get("models/zombie.gltf");
//        Scene scene = new Scene(sceneAsset.scene, "Ch10", "Armature");
//        if(armature != null) {
//            Gdx.app.log("GameObjectType",  " armature: "+armature + " animations: "+scene.modelInstance.animations.size);
//            for(int i = 0; i < scene.modelInstance.animations.size; i++) {
//                String id = scene.modelInstance.animations.get(i).id;
//                Gdx.app.log(" animation :", id);
//            }
//        }
//        scene.animationController.setAnimation("Idle", -1);
//        sceneManager.addScene(scene);


}
