package com.monstrous.frightnight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.monstrous.frightnight.cornfield.DecalCornField;
import com.monstrous.frightnight.creatures.Car;
import com.monstrous.frightnight.creatures.Creature;
import com.monstrous.frightnight.creatures.Zombie;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.io.StringWriter;

public class World implements Disposable {
    public static String GLTF_FILE = "models/frightnight.gltf";
    public static String SAVE_FILE_NAME = "save_file.txt";

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
    public Population population;
    private PopulationScenes populationScenes;
    private HintQueue hintQueue;


    public World(Assets assets, Sounds sounds, SceneManager sceneManager, HintQueue hintQueue ) {
        this.sceneManager = sceneManager;
        this.hintQueue = hintQueue;
        sceneAsset = assets.get(GLTF_FILE); //new GLTFLoader().load(Gdx.files.internal(GLTF_FILE));
        wheelAngle = 0;

        staticScenes = new Array<>();

        particleEffects = new ParticleEffects( sceneManager.camera );

        population = new Population(sounds);
        populationScenes = new PopulationScenes(population, sceneAsset, sceneManager);

        reset();
        population.save(SAVE_FILE_NAME);    // overwrite quick save from previous play through

        setCameraToPlayerPosition(sceneManager.camera);
        playerTransform = new Matrix4();
        playerTransform.setToTranslation(sceneManager.camera.position);
        particleEffects.addRain(playerTransform);

        // road is along the y axis
        cornFields = new Array<>();
        Rectangle area = new Rectangle(10, -200, 50, 400);      // south side along whole width of map
        DecalCornField cornField = new DecalCornField(assets, sceneManager.camera, area, SEPARATION_DISTANCE);
        cornFields.add(cornField);
        Rectangle area2 = new Rectangle(-165, -200, 150, 200);
        cornField = new DecalCornField(assets, sceneManager.camera, area2, SEPARATION_DISTANCE);
        cornFields.add(cornField);
        Rectangle area3 = new Rectangle(-165, 150, 150, 50);
        cornField = new DecalCornField(assets, sceneManager.camera, area3, SEPARATION_DISTANCE);
        cornFields.add(cornField);
        Rectangle area4 = new Rectangle(-165, 0, 20, 150);  // behind church
        cornField = new DecalCornField(assets, sceneManager.camera, area4, SEPARATION_DISTANCE);
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



    private void loadStaticScenes() {

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
    }

    public void reset() {
        loadStaticScenes();
        population.reset();
        populationScenes.loadFromAssetFile(sceneAsset);
        populationScenes.reset();
    }

    public void quickSave() {
        Gdx.app.log("quick save", "");
        hintQueue.addHint(-1, HintMessage.QUICKSAVE);
        population.save(SAVE_FILE_NAME);
    }

    public void quickLoad() {
        Gdx.app.log("quick load", "");
        hintQueue.addHint(-1, HintMessage.QUICKLOAD);

        loadStaticScenes();
        population.reset();
        population.load(SAVE_FILE_NAME);
        populationScenes.reset();
        setCameraToPlayerPosition(sceneManager.camera);
    }

    private void setCameraToPlayerPosition(Camera camera) {

        // move camera to player position
        camera.position.set(population.getPlayer().position);
        camera.direction.set(population.getPlayer().getForward());
        camera.update();
    }

    // return true if player died
    public boolean update(float deltaTime ) {
        deltaTime = Math.min(deltaTime, 0.05f); // don't go haywire when debugging

        if(population.getPlayer().isDead())
            return true;
        population.getPlayer().getForward().set(sceneManager.camera.direction);

        population.update(sceneManager.camera.position, hintQueue, deltaTime );
        populationScenes.update(deltaTime);


        playerTransform.setToTranslation(sceneManager.camera.position);       // as this is a first person game, this is also the camera position
        particleEffects.moveRain(playerTransform);

        particleEffects.update(deltaTime);
        return false;
    }

    public boolean gameCompleted() {
        return population.getCar().hasPickedUp();
    }

    public void brightenUp(){
        for(DecalCornField field : cornFields)
            field.setColor(Color.WHITE);
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
