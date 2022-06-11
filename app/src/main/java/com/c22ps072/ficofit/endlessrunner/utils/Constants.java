package com.c22ps072.ficofit.endlessrunner.utils;

import com.badlogic.gdx.math.Vector2;

public class Constants {
//    public static int APP_WIDTH;
//    public static final int APP_HEIGHT = APP_WIDTH * 3 / 5;

    public static final Vector2 WORLD_GRAVITY = new Vector2(0, -10);

    public static final float GROUND_X = 0;
    public static final float GROUND_Y = 1f;
//    public static final float GROUND_WIDTH = APP_WIDTH;
    public static final float GROUND_HEIGHT = 2f;  //
    public static final float GROUND_DENSITY = 0f;

    public static final float RUNNER_X = 2;
    public static final float RUNNER_Y = GROUND_Y + GROUND_HEIGHT;
    public static final float RUNNER_WIDTH = 2f;
    public static final float RUNNER_HEIGHT = RUNNER_WIDTH;
    public static float RUNNER_DENSITY = 0.5f;
    public static final float RUNNER_GRAVITY_SCALE = 0.5f;
    public static final Vector2 RUNNER_JUMPING_LINEAR_IMPULSE = new Vector2(0, 13f);
    public static final float RUNNER_HIT_ANGULAR_IMPULSE = 20f;

    public static final float RUNNER_DODGE_X = 2f;
    public static final float RUNNER_DODGE_Y = 1.5f;

    public static final float ENEMY_X = 25f;
    public static final float ENEMY_DENSITY = 1f;
    public static final float RUNNING_SHORT_ENEMY_Y = GROUND_Y + 1.5f; //
    public static final float RUNNING_LONG_ENEMY_Y = GROUND_Y + 2f;    //
//    public static final float FLYING_ENEMY_Y = 3f;
    public static final Vector2 ENEMY_LINEAR_VELOCITY = new Vector2(-10f, 0);

    public static final String BACKGROUND_IMAGE_PATH = "background.png";
    public static final String GROUND_IMAGE_PATH = "ground.png";
    public static final float WORLD_TO_SCREEN = 32;
    public static final String RUNNER_IMAGE_PATH = "runner.png";
    public static final String ENEMY_WIDE_IMAGE_PATH = "enemy_gepeng.png";
    public static final String ENEMY_LONG_IMAGE_PATH = "enemy_sosis.png";
    public static final String ENEMY_BIG_IMAGE_PATH = "enemy_lipfiller.png";
}
