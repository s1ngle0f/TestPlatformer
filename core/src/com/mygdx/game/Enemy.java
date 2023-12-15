package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Enemy {
    Sound bruh, huh;
    private boolean playerContact = false;
    private World world;
    private Body body;
    private Texture texture, texture2;
    private TextureRegion textureRegion;

    public boolean isAlive = true;

    public Enemy(World world, float x, float y) {
        this.world = world;

        bruh = Gdx.audio.newSound(Gdx.files.internal("TMS/bruh.mp3"));
        huh = Gdx.audio.newSound(Gdx.files.internal("TMS/huh.mp3"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5f, 5f);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);


        shape.dispose();


        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        texture2 = new Texture(Gdx.files.internal("badlogicDEAD.jpg"));
        textureRegion = new TextureRegion(texture);

    }

    public void render(SpriteBatch batch) {
        if (isAlive) {
            batch.draw(textureRegion,
                    body.getPosition().x - 5,
                    body.getPosition().y - 5,
                    5, 5, 10, 10, 1, 1, body.getAngle()* MathUtils.radiansToDegrees);
            System.out.println(body.getAngle());
        }
        else
            batch.draw(texture2, body.getPosition().x - 5, body.getPosition().y - 5, 10, 10);
    }

    public void update(float deltaTime, Player player) {
        float x = player.body.getPosition().x;
        Random rnd = new Random();
        if (isAlive == true) {
            if (body.getPosition().x > x){
                body.applyLinearImpulse(new Vector2(-7, 0), body.getPosition(), true);
            } else if (body.getPosition().x < x){
                body.applyLinearImpulse(new Vector2(7, 0), body.getPosition(), true);
            }
        }

    }

    public void handleCollision(Player player) {
        if (isAlive == true && player.isInvease() == false){
            huh.play();
        }

        //System.out.println("Enemy collided!");
    }

    public void setUserData(String enemy) {
        body.setUserData(enemy);
    }

    public Body getBody(){
        return body;
    }


}

