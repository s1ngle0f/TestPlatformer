package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Sprite {
    private World world;
    public Body body;
    public BodyDef bodyDef;
    private Texture texture;
    private SpriteBatch batch;
    public Player(World world, SpriteBatch batch, Texture texture){
        this.world = world;
        this.texture = texture;
        this.batch = batch;
        definePlayer();

    }

    private void definePlayer() {
        bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(10, 10));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5, 5);
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.95f;
        body.createFixture(fixtureDef);
    }


    public void setUserData(String player) {
        body.setUserData(player);
    }

    public void handleCollision() {
        System.out.println("enemy detected!");
    }
}
