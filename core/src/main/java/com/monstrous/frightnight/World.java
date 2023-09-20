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
    private Array<Scene> staticScenes;       // for cleanup
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

        staticScenes = new Array<>();

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

    private void addStaticScene( String name ){
        Scene scene = new Scene(sceneAsset.scene, name);
        staticScenes.add(scene);
        sceneManager.addScene(scene);
    }

    public void reset() {

        // clear sceneManager
        sceneManager.getRenderableProviders().clear();      // this removes all scenes
//        for(Scene scene : staticScenes)
//            sceneManager.removeScene(scene);
//        populationScenes.clearPopulation();
        staticScenes.clear();

        // extract some scenery items and add to scene manager
        addStaticScene("groundplane");
        addStaticScene("road");
        addStaticScene("church");
        addStaticScene("spookytree");
        addStaticScene("spookytree2");
        addStaticScene("spookytree3");
        addStaticScene("spookytree4");
        addStaticScene("spookytree3.001");
        addStaticScene("spookytree4.001");
        addStaticScene("gravestone");
        addStaticScene("gravestone2");
        addStaticScene("gravestone3");
        addStaticScene("gravestone4");
        addStaticScene("gravestone3.001");
        addStaticScene("gravestone4.001");
        addStaticScene("gravestone4.002");

        population.reset();
        populationScenes.loadFromAssetFile(sceneAsset);
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
