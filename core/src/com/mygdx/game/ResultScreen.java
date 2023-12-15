package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ResultScreen implements Screen {
    SpriteBatch batch;
    BitmapFont font;

    long time = -1;
    String timeFinal = "";
    private OrthographicCamera camera;

    public ResultScreen(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        font = new BitmapFont();
        font.setColor(Color.WHITE); // цвет текста (белый)
        font.getData().setScale(10);
    }

    public void create () {

    }

    public void setFinalTime(long Time){
        int seconds = (int) Time / 1000;

        int mins = (int) seconds / 60;
        int minFirst = (int) mins % 10;
        int minSec = (int) mins / 10;

        int sec = seconds - (mins * 60);
        int secFirst = seconds % 10;
        int secSec = (int) seconds / 10;

        timeFinal = (minFirst + minSec + ":" + secFirst + secSec);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "You won!", camera.position.x, camera.position.y);

        font.draw(batch, timeFinal, 800, 700); // текст и координаты вывода
        batch.end();
    }

    @Override
    public void show() {

    }
    @Override
    public void resize(int width, int height) {
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
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
