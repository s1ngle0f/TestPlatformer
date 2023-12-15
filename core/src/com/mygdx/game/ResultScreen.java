package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ResultScreen implements Screen {
    SpriteBatch batch;
    BitmapFont font;

    long time = -1;
    String timeFinal = "";
    private OrthographicCamera camera;
    private FitViewport hudViewport;
    public ResultScreen(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        hudViewport = new FitViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, camera);
        font = new BitmapFont();
        font.setColor(Color.WHITE); // цвет текста (белый)
        font.getData().setScale(.3f);
    }

    public void create () {

    }

    public void setFinalTime(float time){
        int seconds = (int) (time % 60);

        int mins = (int) seconds / 60;

        timeFinal = (mins + " : " + seconds);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "You won!", camera.position.x, camera.position.y);

        font.draw(batch, timeFinal, camera.position.x, camera.position.y - 10); // текст и координаты вывода
        batch.end();
    }

    @Override
    public void show() {

    }
    @Override
    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        MyGdxGame.SCREEN_WIDTH = hudViewport.getScreenWidth();
        MyGdxGame.SCREEN_HEIGHT = hudViewport.getScreenHeight();
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
    }

}
