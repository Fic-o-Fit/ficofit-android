package com.c22ps072.ficofit.endlessrunner.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.c22ps072.ficofit.endlessrunner.box2d.RunnerUserData;
import com.c22ps072.ficofit.endlessrunner.utils.Constants;

public class Runner extends GameActor {

    private boolean dodging;
    private boolean jumping;
    private boolean hit;
    private final TextureRegion trRunner;

    public Runner(Body body) {
        super(body);
        trRunner = new TextureRegion(new Texture(Gdx.files.internal(Constants.RUNNER_IMAGE_PATH)));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(trRunner, screenRectangle.x, screenRectangle.y,
                screenRectangle.getWidth(), screenRectangle.getHeight());
    }

    @Override
    public RunnerUserData getUserData() {
        return (RunnerUserData) userData;
    }

    public void jump() {

        if (!(jumping || dodging || hit)) {
            body.applyLinearImpulse(getUserData().getJumpingLinearImpulse(), body.getWorldCenter(), true);
            jumping = true;
        }

    }

    public void landed() {
        jumping = false;
    }

    public void dodge() {
        if (!(jumping || hit)) {
            body.setTransform(getUserData().getDodgePosition(), getUserData().getDodgeAngle());
            dodging = true;
        }
    }

    public void stopDodge() {
        dodging = false;
        // If the runner is hit don't force him back to the running position
        if (!hit) {
            body.setTransform(getUserData().getRunningPosition(), 0f);
        }
    }

    public boolean isDodging() {
        return dodging;
    }

    public void hit() {
        body.applyAngularImpulse(getUserData().getHitAngularImpulse(), true);
        hit = true;
    }

    public boolean isHit() {
        return hit;
    }

}
