package com.c22ps072.ficofit.endlessrunner.enums;


import com.c22ps072.ficofit.endlessrunner.utils.Constants;

public enum EnemyType {

//    RUNNING_SMALL(0.5f, 0.5f, Constants.ENEMY_X, Constants.RUNNING_SHORT_ENEMY_Y, Constants.ENEMY_DENSITY),

    RUNNING_WIDE(2f, 1f, Constants.ENEMY_X, Constants.RUNNING_SHORT_ENEMY_Y, Constants.ENEMY_DENSITY,Constants.ENEMY_WIDE_IMAGE_PATH),
    RUNNING_LONG(1f, 2f, Constants.ENEMY_X, Constants.RUNNING_LONG_ENEMY_Y, Constants.ENEMY_DENSITY, Constants.ENEMY_LONG_IMAGE_PATH),
    RUNNING_BIG(1.5f, 2f, Constants.ENEMY_X, Constants.RUNNING_LONG_ENEMY_Y, Constants.ENEMY_DENSITY, Constants.ENEMY_BIG_IMAGE_PATH);

    //    FLYING_SMALL(1f, 1f, Constants.ENEMY_X, Constants.FLYING_ENEMY_Y, Constants.ENEMY_DENSITY),
//    FLYING_WIDE(2f, 1f, Constants.ENEMY_X, Constants.FLYING_ENEMY_Y, Constants.ENEMY_DENSITY);

    private float width;
    private float height;
    private float x;
    private float y;
    private float density;
    private String filepath;

    EnemyType(float width, float height, float x, float y, float density, String filepath) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.density = density;
        this.filepath = filepath;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDensity() {
        return density;
    }

    public String getFilepath() {
        return filepath;
    }

}
