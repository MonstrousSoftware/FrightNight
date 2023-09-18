package com.monstrous.frightnight.screens;


import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
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
import com.monstrous.frightnight.filters.PostProcessor;
import com.monstrous.frightnight.input.CamController;
import com.monstrous.frightnight.lightning.BranchedLightning;
import com.monstrous.frightnight.Settings;
import com.monstrous.frightnight.World;
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
        private static Sound thunder;
        private Color bgColor;
        private float thunderTimer;

        public GameScreen(Main game) {

            this.game = game;

            // load scene asset
            sceneManager = new SceneManager();
            // setup camera
            camera = new PerspectiveCamera(50f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.near = 1f;
            camera.far = 300f;
            camera.position.set(5, Settings.eyeHeight, -10);
            camera.lookAt(0,Settings.eyeHeight,0);
            sceneManager.setCamera(camera);

            world = new World( sceneManager );
        }

        @Override
        public void show() {
            // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
            Gdx.input.setCursorCatched(true);
            Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);

            camController = new CamController(camera);

            // controller
            if(Settings.supportControllers) {
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
            light = new DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).setViewport(100,100,5,400);

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
            thunder = Gdx.audio.newSound(Gdx.files.internal("sound/thunder-25689.mp3"));
            thunderTimer = 3f;
            bgColor = new Color();

        }

        @Override
        public void render(float deltaTime) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
                (currentController != null && currentController.getButton(currentController.getMapping().buttonStart))) {
                game.setScreen(new PauseMenuScreen(game, this));
                return;
            }


            bgColor.set(0.02f, 0, .02f, 1f);
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
                    thunder.play();
                    bgColor.set(1f, 1, 1f, 1f);
                    flashTimer = 0.1f;  // lightning effect
                    thunderTimer = MathUtils.random(Settings.lightningPeriod) + 1;      // time to next flash
                }

                if (flashTimer > 0) {
                    sceneManager.setAmbientLight(1f);
                }
                flashTimer -= deltaTime;
            }



            world.update(deltaTime);

            camController.update(deltaTime);

//        camera.position.set(world.player.eyePosition);
////        camera.position.z = player.position.y;
////        camera.position.y = 1.5f;
//        camera.direction.set(world.player.forward);
//        camera.up.set(0,1,0);
//        camera.update();
            // animate camera
            //camController.update();

            //camera.direction.set(world.getFocusPosition()).sub(camera.position).nor();      // aim camera at car
//        camera.up.set(Vector3.Y);
//        camera.update();


            sceneManager.update(deltaTime);

            // render

            sceneManager.renderShadows();
           // sceneManager.renderTransmission();

            fbo.begin();

            ScreenUtils.clear(bgColor, true);



            sceneManager.renderColors();

            if(Settings.enableWeather) {
                modelBatch.begin(camera);
                world.particleEffects.render(modelBatch);     // rain
                lightning.render(modelBatch, camera.position);
                modelBatch.end();
            }

            fbo.end();



            postProcessor.render(fbo);    // bloom & vignette
        }


        @Override
        public void resize(int width, int height) {
            // Resize your screen here. The parameters represent the new window size.
            sceneManager.updateViewport(width, height);
            //gui.resize(width, height);
            if(fbo != null)
                fbo.dispose();
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            postProcessor.resize(width, height);
        }


        @Override
        public void hide() {
            Gdx.input.setCursorCatched(false);

            if(currentController != null) {
                Controllers.removeListener(controllerAdapter);
                Controllers.addListener(game.controllerToInputAdapter);
            }
            // dispose();            // todo causes crash when reentering game
        }


        @Override
        public void dispose() {
            // Destroy screen's assets here.
            sceneManager.dispose();
            //sceneAsset.dispose();
            environmentCubemap.dispose();
            diffuseCubemap.dispose();
            specularCubemap.dispose();
            brdfLUT.dispose();
            skybox.dispose();
//        gui.dispose();
            world.dispose();
            postProcessor.dispose();
        }
}
