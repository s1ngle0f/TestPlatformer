package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    private static final float unitScale = 0.1f;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private FitViewport gameViewport;
    private FitViewport hudViewport;
    private Stage hudStage;
    private TmxMapLoader tmxMapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Box2DDebugRenderer b2dr;
    private World world;
    private Player player;
    private Vector2 touch;

    public GameScreen(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;

        gameViewport = new FitViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, camera);
        hudViewport = new FitViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, camera);

        tmxMapLoader = new TmxMapLoader();
        map = tmxMapLoader.load("tilemaps/example.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);

        world = new World(new Vector2(0, -3600), true);

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        b2dr = new Box2DDebugRenderer();

        for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.x + rect.getWidth()/2) * unitScale,
                    (rect.y + rect.getHeight()/2) * unitScale);

            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth()/2*unitScale, rect.getHeight()/2*unitScale);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        player = new Player(world, batch, new Texture("badlogic.jpg"));

        touch = new Vector2(
                Gdx.input.getX() - gameViewport.getScreenX(),
                gameViewport.getScreenHeight() - Gdx.input.getY() + gameViewport.getScreenY()
        );
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1,1,1,1);
        updateTouch();

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
            player.body.applyLinearImpulse(new Vector2(0, 700000),
                    player.body.getPosition(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            player.body.applyForceToCenter(new Vector2(3000, 0), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            player.body.applyForceToCenter(new Vector2(-3000, 0), true);
        }
        if(Gdx.input.isTouched()) {
//            System.out.println(Gdx.input.getX() + ", " +
//                              (Gdx.input.getY()) + ", " +
//                               (MyGdxGame.SCREEN_HEIGHT - Gdx.input.getY()));
            System.out.println(
                    (touch.x) + ", " +
                    (touch.y));
       }

        //Обновление камеры
        camera.update();

        //Для отображения объектов через batch.begin() batch.end()
        batch.setProjectionMatrix(camera.combined);

        //Отображение карты
        renderer.setView(camera);
        renderer.render();

        //Отвечает за отрисовку границ rectangle
        b2dr.render(world, camera.combined);

        //Физическая симуляция мира
        world.step(1/160f, 6, 2);
    }

    @Override
    public void resize(int width, int height) {
		gameViewport.update(width, height, true);
		hudViewport.update(width, height, true);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        MyGdxGame.SCREEN_WIDTH = gameViewport.getScreenWidth();
        MyGdxGame.SCREEN_HEIGHT = gameViewport.getScreenHeight();
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
    public void dispose() {

    }

    private void updateTouch(){
        touch.set(
                Gdx.input.getX() - gameViewport.getScreenX(),
                gameViewport.getScreenHeight() - Gdx.input.getY() + gameViewport.getScreenY()
        );
    }
}
