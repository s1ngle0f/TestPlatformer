package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Enemy {
    private boolean playerContact = false;
    private World world;
    private Body body;
    private Texture texture, texture2;

    public boolean isAlive = true;

    public Enemy(World world, float x, float y) {
        this.world = world;


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

    }

    public void render(SpriteBatch batch) {
        if (isAlive)
            batch.draw(texture, body.getPosition().x - 5, body.getPosition().y - 5, 10, 10);
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

    public void handleCollision() {
        System.out.println("Enemy collided!");
    }

    public void setUserData(String enemy) {
        body.setUserData(enemy);
    }

    public Body getBody(){
        return body;
    }
}

