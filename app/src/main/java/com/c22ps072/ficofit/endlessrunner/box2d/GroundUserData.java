package com.c22ps072.ficofit.endlessrunner.box2d;


import com.c22ps072.ficofit.endlessrunner.enums.UserDataType;

public class GroundUserData extends UserData {

    public GroundUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.GROUND;
    }

}
