package com.c22ps072.ficofit.endlessrunner.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.c22ps072.ficofit.endlessrunner.utils.Constants;

public class Background extends Actor {

    private final TextureRegion trBackground;
    private Rectangle textureRegionBounds1;
    private Rectangle textureRegionBounds2;
    private final int speed = 100;
    private final int APP_WIDTH;
    private final int APP_HEIGHT;

    public Background() {
        APP_WIDTH = 800;
        APP_HEIGHT = 480;
        System.out.println(APP_HEIGHT);
        trBackground = new TextureRegion(new Texture(Gdx.files.internal(Constants.BACKGROUND_IMAGE_PATH)));
        textureRegionBounds1 = new Rectangle(-APP_WIDTH / 2, 0, APP_WIDTH, APP_HEIGHT);
        textureRegionBounds2 = new Rectangle(APP_WIDTH / 2, 0, APP_WIDTH, APP_HEIGHT);
    }

    @Override
    public void act(float delta) {
        if (leftBoundsReached(delta)) {
            resetBounds();
        } else {
            updateXBounds(-delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(trBackground, textureRegionBounds1.x, textureRegionBounds1.y, APP_WIDTH,
                APP_HEIGHT);
        batch.draw(trBackground, textureRegionBounds2.x, textureRegionBounds2.y, APP_WIDTH,
                APP_HEIGHT);
    }

    private boolean leftBoundsReached(float delta) {
        return (textureRegionBounds2.x - (delta * speed)) <= 0;
    }

    private void updateXBounds(float delta) {
        textureRegionBounds1.x += delta * speed;
        textureRegionBounds2.x += delta * speed;
    }

    private void resetBounds() {
        textureRegionBounds1 = textureRegionBounds2;
        textureRegionBounds2 = new Rectangle(APP_WIDTH, 0, APP_WIDTH, APP_HEIGHT);
    }

}
