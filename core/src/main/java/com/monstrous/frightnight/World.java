package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

public class World implements Disposable {
    public static String GLTF_FILE = "models/frightnight.gltf";

    private SceneAsset sceneAsset;
    private SceneManager sceneManager;
    //public Player player;
    private Car car;
    private Scene carScene;
    private Scene wheelScene0, wheelScene1, wheelScene2, wheelScene3;
    private float wheelAngle;
    private Vector3 tmpVec = new Vector3();
    public ParticleEffects particleEffects;
    private Matrix4 playerTransform;

    public World(SceneManager sceneManager ) {
        this.sceneManager = sceneManager;
        sceneAsset = new GLTFLoader().load(Gdx.files.internal(GLTF_FILE));
        wheelAngle = 0;

        particleEffects = new ParticleEffects( sceneManager.camera );


        reset();

        playerTransform = new Matrix4();
        playerTransform.setToTranslation(sceneManager.camera.position);
        particleEffects.addRain(playerTransform);
    }

    public void reset() {
        //player = new Player(10, 1.5f, 20);
        car = new Car(0,0,0);

        // todo clear sceneManager

        // extract some scenery items and add to scene manager
        Scene scene = new Scene(sceneAsset.scene, "road");
        sceneManager.addScene(scene);
        carScene = new Scene(sceneAsset.scene, "car");
        sceneManager.addScene(carScene);
        wheelScene0 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene0);
        wheelScene1 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene1);
        wheelScene2 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene2);
        wheelScene3 = new Scene(sceneAsset.scene, "wheel");
        sceneManager.addScene(wheelScene3);
        scene = new Scene(sceneAsset.scene, "groundplane");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "church");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "HellHound");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree2");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree3");
        sceneManager.addScene(scene);
        scene = new Scene(sceneAsset.scene, "spookytree4");
        sceneManager.addScene(scene);
    }

    public void update(float deltaTime ) {
        //player.move(deltaTime);
        car.move(deltaTime);

        float wx = 1f;
        float wz = 1.83f;
        float wy = 0.37f;

        carScene.modelInstance.transform.setTranslation(car.position);

        wheelAngle += car.speed * 90f * deltaTime;

        wheelScene0.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z+wz);
        wheelScene1.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z+wz);
        wheelScene2.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).trn(car.position.x+wx, wy, car.position.z-wz);
        wheelScene3.modelInstance.transform.setToRotation(Vector3.X, wheelAngle).rotate(Vector3.Y, 180).trn(car.position.x-wx, wy, car.position.z-wz);


        playerTransform.setToTranslation(sceneManager.camera.position);       // as this is a first person game, this is also the camera position
        particleEffects.moveRain(playerTransform);

        particleEffects.update(deltaTime);
    }


    @Override
    public void dispose() {

        sceneAsset.dispose();
        particleEffects.dispose();
    }
}
