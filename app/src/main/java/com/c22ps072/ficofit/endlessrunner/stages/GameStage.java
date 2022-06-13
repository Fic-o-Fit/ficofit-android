package com.c22ps072.ficofit.endlessrunner.stages;

import android.os.SystemClock;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.c22ps072.ficofit.endlessrunner.EndlessRunner;
import com.c22ps072.ficofit.endlessrunner.actors.Background;
import com.c22ps072.ficofit.endlessrunner.actors.Enemy;
import com.c22ps072.ficofit.endlessrunner.actors.Ground;
import com.c22ps072.ficofit.endlessrunner.actors.Runner;
import com.c22ps072.ficofit.endlessrunner.utils.BodyUtils;
import com.c22ps072.ficofit.endlessrunner.utils.WorldUtils;


public class GameStage extends Stage implements ContactListener{

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 480;

    private World world;
    private Ground ground;
    private Runner runner;
    public boolean gameOver;
    public boolean restart;

    private final EndlessRunner endlessRunner;
//    private int userPoints;
    private SpriteBatch batch;
    private BitmapFont font;

    private long startWaitingForEnemy = 0;

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    private long gameOverTime = 0;

    public GameStage(EndlessRunner endlessRunner) {
        super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
                new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
        this.endlessRunner = endlessRunner;
        batch = new SpriteBatch();
        font = new BitmapFont();
        gameOver = false;
        restart = false;
        setUpWorld();
        setupCamera();
//        waitToStart();
//        renderer = new Box2DDebugRenderer();
    }


    private void setUpWorld() {
        if(!gameOver){
            endlessRunner.setCurrentPoints(-1);
            world = WorldUtils.createWorld();
            world.setContactListener(this);
            setUpBackground();
            setUpGround();
            setUpRunner();
            createEnemy();
        }
    }

    private void setUpBackground() {
        addActor(new Background());
    }

    private void setUpGround() {
        ground = new Ground(WorldUtils.createGround(world));
        addActor(ground);
    }

    private void setUpRunner() {
        runner = new Runner(WorldUtils.createRunner(world));
        addActor(runner);
    }

    private void setupCamera() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Array<Body> bodies = new Array<Body>(world.getBodyCount());
        world.getBodies(bodies);

        for (Body body : bodies) {
            update(body);
        }

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        //TODO: Implement interpolation
        if(runner.isHit()){
            gameOverTime = SystemClock.elapsedRealtime();
            gameOver = true;
            batch.begin();
            font.getData().setScale(10, 10);
            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(font, "Game Over");
            font.draw(batch, glyphLayout, (VIEWPORT_WIDTH - glyphLayout.width)/2, (VIEWPORT_HEIGHT - glyphLayout.height)/2);
            batch.end();
        }
    }

    private void update(Body body) {
        if (!BodyUtils.bodyInBounds(body)) {
            if (BodyUtils.bodyIsEnemy(body) && !runner.isHit()) {
                if(startWaitingForEnemy == 0){
                    startWaitingForEnemy = SystemClock.elapsedRealtime();
                }
                else if((SystemClock.elapsedRealtime() - startWaitingForEnemy) > 800){
                    startWaitingForEnemy = 0;
                    createEnemy();
                    world.destroyBody(body);
                }
            }else{
                world.destroyBody(body);
            }
        }
    }

    private void createEnemy() {
        Enemy enemy = new Enemy(WorldUtils.createEnemy(world));
        addActor(enemy);
    }

    @Override
    public void draw() {
        super.draw();
//        renderer.render(world, camera.combined);
    }

    public void triggerJump() {
        if (gameOver){
            batch.dispose();

            Array<Body> bodies = new Array<Body>(world.getBodyCount());
            world.getBodies(bodies);

            for (Body body : bodies) {
                world.destroyBody(body);
            }

            endlessRunner.updateHighScore();

            restart = true;

//                gameOver = false;

//                setUpWorld();

        }else{
            runner.jump();
        }
    }


    @Override
    public void beginContact(Contact contact) {

        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();

        if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsEnemy(b)) ||
                (BodyUtils.bodyIsEnemy(a) && BodyUtils.bodyIsRunner(b))) {
            runner.hit();

        } else if ((BodyUtils.bodyIsRunner(a) && BodyUtils.bodyIsGround(b)) ||
                (BodyUtils.bodyIsGround(a) && BodyUtils.bodyIsRunner(b))) {
            if(endlessRunner.getCurrentPoints() != -1){
                endlessRunner.setTotalPoints(endlessRunner.getTotalPoints()+1);
            }
            endlessRunner.setCurrentPoints(endlessRunner.getCurrentPoints()+1);
            endlessRunner.updateHighScore();

            runner.landed();
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
