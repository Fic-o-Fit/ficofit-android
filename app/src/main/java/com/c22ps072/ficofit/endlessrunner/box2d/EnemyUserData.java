package com.c22ps072.ficofit.endlessrunner.box2d;

import com.badlogic.gdx.math.Vector2;
import com.c22ps072.ficofit.endlessrunner.enums.UserDataType;
import com.c22ps072.ficofit.endlessrunner.utils.Constants;

public class EnemyUserData extends UserData {

    private Vector2 linearVelocity;
    private final String filepath;

    public EnemyUserData(float width, float height, String filepath) {
        super(width, height);
        userDataType = UserDataType.ENEMY;
        linearVelocity = Constants.ENEMY_LINEAR_VELOCITY;
        this.filepath = filepath;
    }

    public void setLinearVelocity(Vector2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    public String getFilepath() {
        return filepath;
    }

}
