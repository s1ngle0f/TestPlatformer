package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MyGdxGame extends Game {
	public static float WIDTH = 1920/(2f * 10);
	public static float HEIGHT = 1080/(2f * 10);
	public static float SCREEN_WIDTH;
	public static float SCREEN_HEIGHT;
	SpriteBatch batch;
	OrthographicCamera camera = new OrthographicCamera(), hudCamera = new OrthographicCamera();
	GameScreen gameScreen;

	public static MyGdxGame instance;

	public MyGdxGame(){
		super();
		instance = this;
	}

	@Override
	public void create () {
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		hudCamera.setToOrtho(false, WIDTH, HEIGHT);

		batch = new SpriteBatch();
		gameScreen = new GameScreen(batch, camera, hudCamera);
		setScreen(gameScreen);
	}

//	@Override
//	public void resize(int width, int height) {
//		fitViewport.update(width, height, true);
//		hudViewport.update(width, height, true);
//		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
//	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
