package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.CpuSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.List;

public class Player extends Sprite {
    private World world;
    public Body body;
    public BodyDef bodyDef;
    private HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();
    private HashMap<String, Texture> bigTexturesForAnimation = new HashMap<>();
    private float startAnimTime = 0;
    private float time = 0, width, height;
    private SpriteBatch batch;
    Texture deleteLater;
    Vector2 direction = new Vector2();
    String curAnim = "", lastAnim = "";
    public Player(World world, SpriteBatch batch, float width, float height){
        this.world = world;
        this.batch = batch;
        this.width = width;
        this.height = height;
        definePlayer();
//        deleteLater = new Texture("badlogic.jpg");
        addAnimation("idle", "TMS/bg/players/FantasyWarrior/Sprites/Idle.png",
                162, 162, .1f, 1, 10, 0,
                Animation.PlayMode.LOOP);
        addAnimation("run", "TMS/bg/players/FantasyWarrior/Sprites/RunRight.png",
                162, 162, .1f, 1, 8, 0,
                Animation.PlayMode.LOOP);
    }

    private void playAnimation(String name, Vector2 unitScale){
        TextureRegion textureRegion = animations.get(name).getKeyFrame(time - startAnimTime);
        batch.draw(
                textureRegion,
                body.getPosition().x - (width/2)*(int) unitScale.x,
                body.getPosition().y - height/2,
                width * (int) unitScale.x,
                height * (int) unitScale.y
        );
    }

    private void playAnimation(String name){
        playAnimation(name, new Vector2(1, 1));
    }

    private void addAnimation(String name, String path,
                              int tileWidth, int tileHeight, float frameDuration,
                              int rowCount, int columnCount, int emptyCount,
                              Animation.PlayMode playMode) {
        Array<TextureRegion> animationFramesIdle = new Array<>();
        Texture animMap = new Texture(Gdx.files.internal(path));
        bigTexturesForAnimation.put(
                name,
                animMap
        );
        TextureRegion[][] texturesIdle = TextureRegion.split(animMap, tileWidth, tileHeight);
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) animationFramesIdle.add(texturesIdle[i][j]);
        }
        for(int i = 0; i < emptyCount; i++){
            animationFramesIdle.removeIndex(animationFramesIdle.size-1);
        }
        animations.put(
                name,
                new Animation<TextureRegion>(frameDuration, animationFramesIdle, playMode)
        );
    }

    public void resetAnimationTimer(){
        startAnimTime = time;
    }

    private void definePlayer() {
        bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(10, 10));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/10f, height/10f);
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.95f;
        body.createFixture(fixtureDef);
    }

    public void render(float delta){
        time += delta;

        calculateDirection();
        selectorAnimations(delta);

        lastAnim = curAnim;
    }

    private Vector2 calculateDirection() { // Немножко говнокод, но пока норм!
        direction.set(
                getZnak(body.getLinearVelocity().x),
                getZnak(body.getLinearVelocity().y)
        );
        return new Vector2(direction);
    }

    private void selectorAnimations(float delta){
        Vector2 linearVelocity = body.getLinearVelocity();
        if(linearVelocity.x > 0) {
            curAnim = "run";
            direction.set(1, 1);
            playAnimation(curAnim, direction);
        }
        else if(linearVelocity.x < 0) {
            curAnim = "run";
            direction.set(-1, 1);
            playAnimation(curAnim, direction);
        }
        if (linearVelocity.x == 0){
            curAnim = "idle";
            direction.set(1, 1);
            playAnimation(curAnim, direction);
        }

        if(!lastAnim.equals(curAnim))
            resetAnimationTimer();
    }

    public void setUserData(String player) {
        body.setUserData(player);
    }

    public void handleCollision() {
        System.out.println("enemy detected!");
    }

    private int getZnak(float f){
        if(f > 0) return 1;
        if(f < 0) return -1;
        return 0;
    }
}
