package com.monstrous.frightnight.lightning;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BranchedLightning {
    private Array<LightningBolt> bolts;
    private Array<LightningBolt> boltsToDelete;

    public BranchedLightning() {
        bolts = new Array<>();
        boltsToDelete = new Array<>();
    }

    // lightning bolts will be removed when faded out
    public void update(float deltaTime) {
        for(LightningBolt bolt : bolts) {
            bolt.update(deltaTime);
            if (bolt.isCompleted())
                boltsToDelete.add(bolt);
        }
        for(LightningBolt bolt : boltsToDelete) {
            bolt.dispose();
        }
        bolts.removeAll(boltsToDelete, true);
        boltsToDelete.clear();
    }


    public void render(ModelBatch modelBatch, Vector3 cameraPosition){
        for(LightningBolt bolt : bolts)
            bolt.render(modelBatch, cameraPosition);
    }


    // add new lightning
    public void create(Vector3 from, Vector3 to) {
        LightningBolt main = new LightningBolt(from, to, 1.0f);
        bolts.add(main);
        Vector3 mainVec = new Vector3(to).sub(from);

        int numBranches = MathUtils.random(3, 6);
        Array<Float> fractions = new Array<>();
        for(int i = 0; i < numBranches; i++){
            fractions.add( MathUtils.random());
        }
        fractions.sort();

        float angle = 30f;
        for(float fraction: fractions) {
            Vector3 branchStart = main.getPoint(fraction);
            Vector3 branchVec = new Vector3(mainVec).scl(1 - fraction);
            branchVec.rotate(Vector3.X, angle);
            branchVec.rotate(Vector3.Z, angle);

            angle = -angle;
            branchVec.add(branchStart);
            LightningBolt sub = new LightningBolt(branchStart, branchVec, 0.3f);
            bolts.add(sub);
        }
    }

    public void dispose() {
        for(LightningBolt bolt : bolts)
            bolt.dispose();
    }
}
