package com.monstrous.frightnight.screens;


import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.frightnight.*;
import com.monstrous.frightnight.filters.PostProcessor;
import com.monstrous.frightnight.input.CamController;
import com.monstrous.frightnight.lightning.BranchedLightning;
import com.monstrous.frightnight.input.MyControllerAdapter;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameScreen extends StdScreenAdapter {

    private static final int SHADOW_MAP_SIZE = 2048;

    private Main game;
    private SceneManager sceneManager;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
    private CamController camController;
    private MyControllerAdapter controllerAdapter;
    private Controller currentController;
    private Vector3 tmpV = new Vector3();
    private FrameBuffer fbo;
    private World world;
    private PostProcessor postProcessor;
    private ModelBatch modelBatch;
    private float flashTimer;
    private SpriteBatch batch;
    private BranchedLightning lightning;
    private Color bgColor;
    private float thunderTimer;
    private boolean playerDied = false;
    private int viewHeight;
    private GUI gui;
    private HintQueue hintQueue;
    private boolean firstThunder = true;
    private boolean startedWithWeather;
    private boolean gameCompleted;

    public GameScreen(Main game) {
        Gdx.app.log("GameScreen", "constructor");

        this.game = game;

        // load scene asset
        sceneManager = new SceneManager(70);
        // setup camera
        camera = new PerspectiveCamera(50f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 300f;
        camera.position.set(5, Settings.eyeHeight, -10);
        camera.lookAt(0, Settings.eyeHeight, 0);
        sceneManager.setCamera(camera);

        hintQueue = new HintQueue();
        world = new World(game.assets, game.sounds, sceneManager, hintQueue);
        gui = new GUI(game.assets);

        startedWithWeather = Settings.enableWeather;

        game.musicManager.startMusic("music/dark-ambient-121126.mp3", true);

    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show()");
        // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        // on teavm setCursorCatched() doesn't work so hide the cursor and let the user turn with the keyboard
        // (you can turn a bit with the mouse, until it reaches the side of the canvas).
        if (Gdx.app.getType() == Application.ApplicationType.WebGL)
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);     // hide cursor


        camController = new CamController(camera);

        // controller
        if (Settings.supportControllers) {
            currentController = Controllers.getCurrent();
            if (currentController != null) {
                Gdx.app.log("current controller", currentController.getName());
                controllerAdapter = new MyControllerAdapter(camController);
                // we define a listener that listens to all controllers, in case the current controller gets disconnected and reconnected
                Controllers.removeListener(game.controllerToInputAdapter);
                Controllers.addListener(controllerAdapter);
            } else
                Gdx.app.log("current controller", "none");
        }

        // input multiplexer to input to GUI and to cam controller
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(camController);

        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0.001f));

        // setup light
        light = new DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).setViewport(100, 100, 5, 400);

        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        light.intensity = Settings.directionalLightIntensity;
        //sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)

        environmentCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(),
            "textures/nightsky/environment_", ".jpg", EnvironmentUtil.FACE_NAMES_NEG_POS);
        // environmentCubemap = iblBuilder.buildEnvMap(1024);
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(Settings.ambientLightLevel);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
        sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog, Settings.fogColour));
        sceneManager.environment.set(new FogAttribute(FogAttribute.FogEquation).set(5, 50, 2.0f));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);


        postProcessor = new PostProcessor();

        modelBatch = new ModelBatch();
        flashTimer = 0;

        batch = new SpriteBatch();
        lightning = new BranchedLightning();
        thunderTimer = 30f;         // big delay for first thunder
        bgColor = new Color();
        playerDied = false;
        gameCompleted = false;

        if (startedWithWeather && !Settings.enableWeather)   // switched off weather mid game?
            hintQueue.addHint(2f, HintMessage.WEATHER); // make snarky comment


    }

    @Override
    public void render(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            (currentController != null && currentController.getButton(currentController.getMapping().buttonStart))) {
            game.setScreen(new PauseMenuScreen(game, this));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5) )
            world.quickSave();
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9) ) {
            world.quickLoad();
        }

        if (playerDied && viewHeight <= 0) {
            game.setScreen(new GameOverScreen(game, this, world.getNameOfKiller()));
            return;
        }

        hintQueue.update(deltaTime, gui, game.sounds);


        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);


        bgColor.set(0.02f, 0, .02f, 1f);
        if (gameCompleted) {
            bgColor.set(Color.ORANGE);
            Settings.ambientLightLevel = 0.6f;
        }
        sceneManager.setAmbientLight(Settings.ambientLightLevel);

        if (Settings.enableWeather) {
            lightning.update(deltaTime);

            thunderTimer -= deltaTime;
            if (thunderTimer < 0) {
                // random position and random direction
                float dx = MathUtils.random(-50, 50);
                float dz = MathUtils.random(-50, 50);

                float theta = MathUtils.random(0, 360);
                float distance = MathUtils.random(20, 200);
                float x = camera.position.x + MathUtils.cos(theta) * distance;
                float z = camera.position.y + MathUtils.sin(theta) * distance;

                lightning.create(new Vector3(x, 200, z), new Vector3(x + dx, 0, z + dz));
                game.sounds.playSound(Sounds.THUNDER);

                bgColor.set(1f, 1, 1f, 1f);
                flashTimer = 0.1f;  // lightning effect
                thunderTimer = MathUtils.random(Settings.lightningPeriod) + 1;      // time to next flash
                if (firstThunder) {
                    hintQueue.addHint(1.5f, HintMessage.FRIGHT);
                    thunderTimer += 5;
                }
                firstThunder = false;
            }

            if (flashTimer > 0) {
                sceneManager.setAmbientLight(1f);
            }
            flashTimer -= deltaTime;
        }


        boolean over = world.update(deltaTime);
        if (over && !playerDied) {
            game.sounds.playSound(Sounds.AARGH);
            playerDied = true;
        }
        if (!playerDied) {
            camController.update(deltaTime);
            sceneManager.update(deltaTime);
        } else {
            if (viewHeight > 0)
                viewHeight -= 10;
        }

        if (!gameCompleted && world.gameCompleted()) {
            gameCompleted = true;
            world.brightenUp();
//            environmentCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(),
//                "textures/daysky/environment_", ".jpg", EnvironmentUtil.FACE_NAMES_NEG_POS);
//            skybox = new SceneSkybox(environmentCubemap);
//            sceneManager.setSkyBox(skybox);
            sceneManager.setSkyBox(null);
            game.musicManager.stopMusic();
            game.musicManager.startMusic("music/happy-quirky-theme-160995.mp3", true);
        }
        if (gameCompleted) {
            startedWithWeather = true;
            Settings.enableWeather = false;
            light.intensity = 0.5f;
            sceneManager.environment.remove(ColorAttribute.Fog);

        }

        // render

        sceneManager.renderShadows();


        fbo.begin();

        ScreenUtils.clear(bgColor, true);


        sceneManager.renderColors();

        world.render(camera);

        if (Settings.enableWeather) {
            modelBatch.begin(camera);
            world.particleEffects.render(modelBatch);     // rain
            lightning.render(modelBatch, camera.position);
            modelBatch.end();
        }

        fbo.end();


        postProcessor.render(fbo, 0, (Gdx.graphics.getHeight() - viewHeight) / 2, Gdx.graphics.getWidth(), viewHeight);    // bloom & vignette

        gui.render(deltaTime);
    }


        @Override
        public void resize(int width, int height) {
            Gdx.app.log("GameScreen", "resize()");
            // Resize your screen here. The parameters represent the new window size.
            sceneManager.updateViewport(width, height);
            gui.resize(width, height);
            if(fbo != null)
                fbo.dispose();
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            postProcessor.resize(width, height);
            viewHeight = height;
        }


        @Override
        public void hide() {
            Gdx.app.log("GameScreen", "hide()");
            Gdx.input.setCursorCatched(false);

            if(currentController != null) {
                Controllers.removeListener(controllerAdapter);
                Controllers.addListener(game.controllerToInputAdapter);
            }
            // dispose what we created in show()
            environmentCubemap.dispose();
            diffuseCubemap.dispose();
            specularCubemap.dispose();
            brdfLUT.dispose();
            skybox.dispose();
            postProcessor.dispose();

            if(Gdx.app.getType() == Application.ApplicationType.WebGL)
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);     // show cursor
        }


        @Override
        public void dispose() {
            // Destroy screen's assets here.
            Gdx.app.log("GameScreen", "dispose()");

            // dispose what we created in constructor
            world.dispose();
            sceneManager.dispose();
            gui.dispose();
            game.musicManager.stopMusic();

        }

        public World getWorld() {
            return world;
        }
}
