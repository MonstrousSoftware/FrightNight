package com.monstrous.frightnight;

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
    private Scene wheelScene0, wheelScene1, wheelScene2, wheelScene3;
    private float wheelAngle;
    private Vector3 tmpVec = new Vector3();
    private Population population;


    public PopulationScenes(Population population, SceneAsset sceneAsset, SceneManager sceneManager ) {
        this.sceneManager = sceneManager;
        this.sceneAsset = sceneAsset;
        this.population = population;



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

    public void reset() {

        for(Wolf wolf : population.wolves) {
            Scene scene = new Scene(sceneAsset.scene, "HellHound");
            scene.modelInstance.transform.set(wolf.transform);
            sceneManager.addScene(scene);
            wolf.scene = scene;
        }

        for(Zombie zombie : population.zombies) {
            Scene scene = new Scene(sceneAsset.scene, "zombieArmature");
            scene.modelInstance.transform.set(zombie.transform);
            sceneManager.addScene(scene);
            zombie.scene = scene;
        }

        Scene carScene = new Scene(sceneAsset.scene, "car");
        sceneManager.addScene(carScene);
        population.getCar().scene = carScene;

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

    private void updateCarScene(Car car, float deltaTime) {
        float wx = 1f;
        float wz = 1.83f;
        float wy = 0.37f;

        car.scene.modelInstance.transform.setTranslation(car.position);

        wheelAngle += car.speed * 90f * deltaTime;

        wheelScene0.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z+wz);
        wheelScene1.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z+wz);
        wheelScene2.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z-wz);
        wheelScene3.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z-wz);

    }


}
