package com.monstrous.frightnight.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class LightningBolt {

    private static final float BOLT_WIDTH = 0.5f;

    Array<Vector3> points;
    private float alpha;        // to fade out
    private Vector3 lineVec;
    private Model boltModel;
    private ModelInstance boltInstance;
    private static LightningShader lightningShader = null;
    private Texture texture;
    private BlendingAttribute blendingAttribute;
    private float widthFactor; // 0 to 1, 1 for thickest branch, smaller values for side branches


    public LightningBolt( Vector3 from, Vector3 to, float widthFactor) {
        alpha = 1f;
        lineVec = new Vector3();
        points = new Array<>();

        texture = new Texture(Gdx.files.internal("textures/bolt.png"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        makeBolt(from, to, widthFactor);

    }

    public void update( float deltaTime ){
        alpha -= deltaTime;
    }

    public boolean isCompleted() {
        return alpha <= 0;
    }


    public Vector3 getPoint( float fraction ){
        int index = (int) (fraction * points.size);         // get point closest to requested fraction
        return points.get(index);
    }



    private void makeBolt(Vector3 start, Vector3 end, float widthFactor) {
        this.widthFactor = widthFactor;

        lineVec.set(end).sub(start);
        float len = lineVec.len();
        // determine 2 vectors A and B that orthogonal to the line vector and each other
        Vector3 normal = new Vector3(lineVec.y, -lineVec.x, lineVec.z).nor();
        Vector3 A = new Vector3();
        Vector3 B = new Vector3();
        A.set(normal).crs(lineVec).nor();
        B.set(A).crs(lineVec).nor();

        Array<Float> fractions = new Array<>();
        for (int i = 0; i < len / 0.3f; i++) {
            fractions.add(MathUtils.random());
        }
        fractions.sort();


        points.clear();
        Vector3 source = start;
        Vector3 offsetA = new Vector3();
        Vector3 offsetB = new Vector3();
        float sway = 60;
        float jaggedness = 1f / sway;
        float prevDisplacementA = 0;
        float prevDisplacementB = 0;

        points.add(start);
        for (int i = 1; i < fractions.size; i++) {
            float fraction = fractions.get(i);

            float scale = (len * jaggedness) * (fraction - fractions.get(i - 1));
            // defines an envelope. Points near the middle of the bolt can be further from the central line.
            float envelope = fraction > 0.9f ? 10 * (1 - fraction) : 1;

            float displacement = MathUtils.random(-sway, sway);
            displacement -= (displacement - prevDisplacementA) * (1 - scale);
            displacement *= envelope;
            prevDisplacementA = displacement;
            offsetA.set(A).scl(displacement);

            displacement = MathUtils.random(-sway, sway);
            displacement -= (displacement - prevDisplacementB) * (1 - scale);
            displacement *= envelope;
            prevDisplacementB = displacement;
            offsetB.set(B).scl(displacement);

            Vector3 point = new Vector3();
            point.set(lineVec).scl(fraction);
            point.add(offsetA);
            point.add(offsetB);
            point.add(source);
            points.add(point);
        }
        points.add(end);
    }

    public void wiggleBolt() {
        Vector3 offsetA = new Vector3();
        Vector3 offsetB = new Vector3();

        lineVec.set(points.get(points.size-1)).sub(points.get(0));
        // determine 2 vectors A and B that orthogonal to the line vector and each other
        Vector3 normal = new Vector3(lineVec.y, -lineVec.x, lineVec.z).nor();
        Vector3 A = new Vector3();
        Vector3 B = new Vector3();
        A.set(normal).crs(lineVec).nor();
        B.set(A).crs(lineVec).nor();

        for(int i = 0; i < points.size; i++){

            if(MathUtils.random(1.0f) < 0.05f) {
                offsetA.set(A).scl(MathUtils.random(-0.20f,0.20f));
                offsetB.set(B).scl(MathUtils.random(-0.2f, 0.2f));
                points.get(i).add(offsetA);
            }

        }
    }

    public void buildModelInstance( Vector3 cameraPosition ) {
        // now build a model
        if(boltModel != null)
            boltModel.dispose();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        Material mat = new Material(TextureAttribute.createDiffuse(texture));
        blendingAttribute = new BlendingAttribute(Gdx.gl20.GL_SRC_ALPHA, Gdx.gl20.GL_ONE);
        mat.set(blendingAttribute);
        MeshPartBuilder builder = modelBuilder.part("line", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorUnpacked|VertexAttributes.Usage.TextureCoordinates, mat);
        builder.setColor(Color.WHITE);
        for(int i = 0; i < points.size-1; i++){
            Vector3 from = points.get(i);
            Vector3 to = points.get(i+1);
            rect(builder, cameraPosition, from, to, BOLT_WIDTH*widthFactor);
        }
        boltModel = modelBuilder.end();
        boltInstance = new ModelInstance(boltModel);
    }

    private Vector3 view = new Vector3();
    private Vector3 line = new Vector3();
    private Vector3 normal = new Vector3();
    private Vector3 cornerPos = new Vector3();
    private MeshPartBuilder.VertexInfo c00 = new MeshPartBuilder.VertexInfo();
    private MeshPartBuilder.VertexInfo c01 = new MeshPartBuilder.VertexInfo();
    private MeshPartBuilder.VertexInfo c11 = new MeshPartBuilder.VertexInfo();
    private MeshPartBuilder.VertexInfo c10 = new MeshPartBuilder.VertexInfo();
    private Color color = Color.WHITE;

    private void rect(MeshPartBuilder builder, Vector3 camPos, Vector3 a, Vector3 b, float width) {
        view.set(camPos).sub(a).nor();    // view vector
        float dist = view.len();    // view distance
        line.set(b).sub(a);            // line vector
        normal.set(line).crs(view).nor();        // orthogonal vector (we will use it also for point b) (which way does it point?)
        float hw = width/2f;
        hw *= dist*dist/2;      // make visual width independent of distance (i.e. make far away parts larger)

        // we draw a camera facing rectangle for a line segment
        // (note: there are no miters, so there may be gaps & overlaps in corners)

        cornerPos.set(normal).scl(-hw).add(b);
        c01.set(cornerPos, view, color, new Vector2(0,1));
        cornerPos.set(normal).scl(hw).add(b);
        c11.set(cornerPos, view, color, new Vector2(1,1));
        cornerPos.set(normal).scl(-hw).add(a);
        c00.set(cornerPos, view, color, new Vector2(0,0));
        cornerPos.set(normal).scl(hw).add(a);
        c10.set(cornerPos, view, color, new Vector2(1,0));

        builder.rect(c00,c10,c11,c01);    // create rectangle

    }


    public void render( ModelBatch modelBatch, Vector3 cameraPosition){
        if(alpha > 0) {
            wiggleBolt();
            buildModelInstance(cameraPosition); // only really needed if camera moved...
            blendingAttribute.opacity = alpha;
            modelBatch.render(boltInstance);
        }
    }


    public void dispose() {
        boltModel.dispose();
    }
}
