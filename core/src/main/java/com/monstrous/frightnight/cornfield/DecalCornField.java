package com.monstrous.frightnight.cornfield;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.frightnight.Assets;

public class DecalCornField implements Disposable {

    private Rectangle area;
    private float minSeparation;
    private Array<Decal>decals;
    private DecalBatch decalBatch;
    private Color baseColor;

    public void setColor( Color color ){
        baseColor.set(color);
        for(Decal decal: decals ) {
            decal.setColor(baseColor);
        }
    }

    public DecalCornField(Assets assets, Camera camera, Rectangle area, float minSeparation) {
        this.area = new Rectangle(area);
        this.minSeparation = minSeparation;
        baseColor = Color.DARK_GRAY;

        decals = new Array<>();

        // generate a random poisson distribution of instances over a rectangular area, meaning instances are never too close together
        PoissonDistribution poisson = new PoissonDistribution();
        Array<Vector2> points = poisson.generatePoissonDistribution(minSeparation, area);

        Texture billboard = assets.get("images/cornstalk-billboard.png");
        TextureRegion region = new TextureRegion(billboard);

        float baseHeight = 3.0f;

        for(Vector2 point: points ) {
            float ht = baseHeight * MathUtils.random(0.8f, 1.2f);                       // vary heights
            Decal decal = Decal.newDecal(1, ht, region, true);
            decal.setPosition(point.x, ht/2f, point.y);
            decal.setColor(baseColor);
            decals.add(decal);
        }
        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
    }

    public void render(Camera camera) {

        for(Decal decal: decals ) {
            decal.lookAt(camera.position, Vector3.Y);
            decalBatch.add(decal);
        }
        decalBatch.flush();
    }

    @Override
    public void dispose() {
        decalBatch.dispose();
        decals.clear();
    }
}
