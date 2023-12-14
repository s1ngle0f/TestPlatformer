package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackgroundCircle {
    private Texture background;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private int n = -1;
    private int width, height;
    private float parallaxKoef;

    public BackgroundCircle(Texture background, SpriteBatch batch, OrthographicCamera camera, float parallaxKoef) {
        this.background = background;
        this.batch = batch;
        this.camera = camera;
        this.parallaxKoef = parallaxKoef;

        height = (int) MyGdxGame.HEIGHT;
        width = (int) ((height / (float) background.getHeight()) * background.getWidth());

        n = (int) (MyGdxGame.WIDTH) / width;
        if (n < 4)
            n = 4;
        else
            n+=2;
        n++;
    }

    public void render(float delta){
        int leftBottomPointCamera = (int)(camera.position.x) - (int) (MyGdxGame.WIDTH)/2;
        int startPoint = ((leftBottomPointCamera)/(width)) *
                width - width;
        for(int i = 0; i < n; i++) {
            batch.draw(background,
                    startPoint + i * width + (leftBottomPointCamera * parallaxKoef) % width,
                    0,
                    width,
                    height
            );
        }
//        System.out.println("Start point: " + startPoint + "; Position: " + (((int)(camera.position.x) - MyGdxGame.WIDTH/2)));
    }

    public Texture getBackground() {
        return background;
    }
}
