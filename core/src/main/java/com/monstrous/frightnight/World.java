package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.frightnight.cornfield.DecalCornField;
import com.monstrous.frightnight.creatures.Car;
import com.monstrous.frightnight.creatures.Creature;
import com.monstrous.frightnight.creatures.Zombie;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class World implements Disposable {
    public static String GLTF_FILE = "models/frightnight.gltf";

    private static final float SEPARATION_DISTANCE = 1.0f;          // min distance between instances
    private static final float AREA_LENGTH = 100.0f;                // size of the (square) field


    private SceneAsset sceneAsset;
    private SceneManager sceneManager;
    private Scene carScene;
    private Scene wolfScene;
    private Scene zombieScene;
    private Scene wheelScene0, wheelScene1, wheelScene2, wheelScene3;
    private float wheelAngle;
    private Vector3 tmpVec = new Vector3();
    public ParticleEffects particleEffects;
    private Matrix4 playerTransform;
    private Array<DecalCornField> cornFields;
    private Population population;
    private PopulationScenes populationScenes;


    public World(Assets assets, SceneManager sceneManager ) {
        this.sceneManager = sceneManager;
        sceneAsset = assets.get(GLTF_FILE); //new GLTFLoader().load(Gdx.files.internal(GLTF_FILE));
        wheelAngle = 0;

        particleEffects = new ParticleEffects( sceneManager.camera );

        population = new Population();
        populationScenes = new PopulationScenes(population, sceneAsset, sceneManager);

        reset();

        playerTransform = new Matrix4();
        playerTransform.setToTranslation(sceneManager.camera.position);
        particleEffects.addRain(playerTransform);

        // road is along the y axis
        cornFields = new Array<>();
        Rectangle area = new Rectangle(10, -100, 50, 200);
        DecalCornField cornField = new DecalCornField(assets, sceneManager.camera, area, SEPARATION_DISTANCE);
        cornFields.add(cornField);
        Rectangle area2 = new Rectangle(-65, -100, 50, 100);
        cornField = new DecalCornField(assets, sceneManager.camera, area2, SEPARATION_DISTANCE);
        cornFields.add(cornField);

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
        //player = new Player(10, 1.5f, 20);
        //car = new Car(Vector3.Zero, new Vector3(0,0,1), 8f);

        // todo clear sceneManager

        // extract some scenery items and add to scene manager
        Scene scene = new Scene(sceneAsset.scene, "road");
        sceneManager.addScene(scene);
//        carScene = new Scene(sceneAsset.scene, "car");
//        sceneManager.addScene(carScene);
//        wheelScene0 = new Scene(sceneAsset.scene, "wheel");
//        sceneManager.addScene(wheelScene0);
//        wheelScene1 = new Scene(sceneAsset.scene, "wheel");
//        sceneManager.addScene(wheelScene1);
//        wheelScene2 = new Scene(sceneAsset.scene, "wheel");
//        sceneManager.addScene(wheelScene2);
//        wheelScene3 = new Scene(sceneAsset.scene, "wheel");
//        sceneManager.addScene(wheelScene3);
        scene = new Scene(sceneAsset.scene, "groundplane");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "church");
        sceneManager.addScene(scene);
//        wolfScene = new Scene(sceneAsset.scene, "HellHound");
//        sceneManager.addScene(wolfScene);
//        zombieScene = new Scene(sceneAsset.scene, "zombieArmature");
//        sceneManager.addScene(zombieScene);

        scene = new Scene(sceneAsset.scene, "spookytree");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree2");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree3");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree4");
        sceneManager.addScene(scene);

        population.reset();
        populationScenes.reset();
    }

    // return true when game over
    public boolean update(float deltaTime ) {
        if(population.getPlayer().isDead())
            return true;

        population.update(sceneManager.camera.position, deltaTime);
        populationScenes.update(deltaTime);


        playerTransform.setToTranslation(sceneManager.camera.position);       // as this is a first person game, this is also the camera position
        particleEffects.moveRain(playerTransform);

        particleEffects.update(deltaTime);
        return false;
    }

//    private void updateCarScene(Car car,float deltaTime) {
//        float wx = 1f;
//        float wz = 1.83f;
//        float wy = 0.37f;
//
//
//
//        carScene.modelInstance.transform.setTranslation(car.position);
//
//        wheelAngle += car.speed * 90f * deltaTime;
//
//        wheelScene0.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z+wz);
//        wheelScene1.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z+wz);
//        wheelScene2.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z-wz);
//        wheelScene3.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z-wz);
//
//    }


    public String getNameOfKiller() {
        return population.getPlayer().killedBy.name;
    }


    public void render(Camera camera ) {
        for( DecalCornField cornField : cornFields)
            cornField.render(camera);
    }


    @Override
    public void dispose() {
        particleEffects.dispose();
        for( DecalCornField cornField : cornFields)
            cornField.dispose();
    }
}
