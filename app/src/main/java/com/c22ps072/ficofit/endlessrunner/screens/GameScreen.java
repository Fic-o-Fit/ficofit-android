package com.c22ps072.ficofit.endlessrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.c22ps072.ficofit.endlessrunner.EndlessRunner;
import com.c22ps072.ficofit.endlessrunner.stages.GameStage;

public class GameScreen implements Screen {

    public GameStage stage;
    private final EndlessRunner endlessRunner;

    public GameScreen(EndlessRunner endlessRunner) {
        this.endlessRunner = endlessRunner;
        stage = new GameStage(endlessRunner);
    }

    @Override
    public void render(float delta) {
        if(stage.gameOver && stage.restart){
            stage.gameOver = false;
            stage.restart = false;
            stage.dispose();
            stage = new GameStage(endlessRunner);
        }
        Gdx.gl.glClearColor(255/255f, 255/255f, 255/255f, 1);

        //Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Update the stage
        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}