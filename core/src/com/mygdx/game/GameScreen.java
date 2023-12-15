package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameScreen implements Screen {
    long startMills;
    private static final float unitScale = 0.225f;
    private final SpriteBatch batch;
    private final OrthographicCamera camera, hudCamera;
    private FitViewport gameViewport;
    private FitViewport hudViewport;
    private Stage hudStage;
    private TmxMapLoader tmxMapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Box2DDebugRenderer b2dr;
    private World world;
    private Player player;
    private Vector2 touch, worldTouch;
    private Joystick joystick;

    private Texture sky, rock1, rock2, clouds1, clouds2, clouds3, clouds4;
    private final HashMap<String, BackgroundCircle> parallaxBg = new HashMap<>();
    Texture deleteLater;

    List<Body> ground = new ArrayList<>();
    List<Body> coins = new ArrayList<>();
    HashMap<Body, Rectangle> coinsRect = new HashMap<>();
    List<Body> bodyForDelete = new ArrayList<>();
    private TiledMapTileLayer coinLayer;
    private Enemy enemy;
     Sound bruh, huh;

    int direction = -1;
    private float time = 0;
    private MyGdxGame myGdxGame;


    public GameScreen(SpriteBatch batch, OrthographicCamera camera, OrthographicCamera hudCamera, MyGdxGame myGdxGame) {
        this.batch = batch;
        this.camera = camera;
        this.hudCamera = hudCamera;
        this.myGdxGame = myGdxGame;

        bruh = Gdx.audio.newSound(Gdx.files.internal("TMS/bruh.mp3"));
        huh = Gdx.audio.newSound(Gdx.files.internal("TMS/huh.mp3"));

        gameViewport = new FitViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, camera);
        hudViewport = new FitViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, hudCamera);
        hudStage = new Stage(hudViewport, batch);

        Drawable active_button = new TextureRegionDrawable(new Texture("active_button.png"));
        Drawable none_active_button = new TextureRegionDrawable(new Texture("none_active_button.png"));

        deleteLater= new Texture("badlogic.jpg");
        tmxMapLoader = new TmxMapLoader();
        map = tmxMapLoader.load("TMS/jo.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);


        coinLayer = (TiledMapTileLayer) map.getLayers().get("coin");

        world = new World(new Vector2(0, -3600), true);
        //enemy = new Enemy(world, 5, 10);


        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if(contact.getFixtureB().getBody() == player.body && coins.contains(contact.getFixtureA().getBody()))
                    if(world != null) {
                        coins.remove(contact.getFixtureA().getBody());
                        bodyForDelete.add(contact.getFixtureA().getBody());
                    }
                if(contact.getFixtureB().getBody() == player.body && contact.getFixtureA().getBody() == enemy.getBody()){
                        enemy.handleCollision(player);
                        if(player.isInvease() == false)
                            player.startInvease();
                } else if(contact.getFixtureA().getBody() == player.body && contact.getFixtureB().getBody() == enemy.getBody()){
                    enemy.handleCollision(player);
                    if (player.isInvease() == false)
                        player.startInvease();
                } if (contact.getFixtureB().getBody() == player.body &&  ground.contains(contact.getFixtureA().getBody()))
                    player.setJumpCounter(0);
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
        });

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        b2dr = new Box2DDebugRenderer();

        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Body body;
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.x + rect.getWidth()/2) * unitScale,
                    (rect.y + rect.getHeight()/2) * unitScale);

            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth()/2*unitScale, rect.getHeight()/2*unitScale);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);

            ground.add(body);
        }

        initCoinsBody(bodyDef, shape, fixtureDef);

        startConfig();
        touch = new Vector2(
                Gdx.input.getX() - gameViewport.getScreenX(),
                gameViewport.getScreenHeight() - Gdx.input.getY() + gameViewport.getScreenY()
        );

        joystick = new Joystick(hudViewport, hudCamera, new Texture("bgJoystick.png"),
                new Texture("fgStick.png"), 20, 6);
        hudStage.addActor(joystick);
        Button button = new Button(none_active_button, active_button);
        button.setPosition(10,10);
        button.setWidth(10);
        button.setHeight(6);
        hudStage.addActor(button);
        initBackground();
        enemy = new Enemy(world, player.body.getPosition().x - 10, player.body.getPosition().y);

        Gdx.input.setInputProcessor(hudStage);
    }

    private void initCoinsBody(BodyDef bodyDef, PolygonShape shape, FixtureDef fixtureDef) {
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Body body;
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.x + rect.getWidth()/2) * unitScale,
                    (rect.y + rect.getHeight()/2) * unitScale);

            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth()/2*unitScale, rect.getHeight()/2*unitScale);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);

            coins.add(body);
            coinsRect.put(body, rect);
        }
    }

    private void startConfig() {
        Vector2 startPos = new Vector2(640, 400);
        player = new Player(world, batch, 50, 50);
        player.body.setTransform(startPos, player.body.getAngle());
        camera.position.set(player.body.getPosition().x, player.body.getPosition().y, 0);
        System.out.println("Player: " + player.body);
    }

    private void correctCamera(){
        float cameraSpeed = 2f;
        float xDirection = player.body.getPosition().x - camera.position.x;
        float yDirection = player.body.getPosition().y - camera.position.y;
        if(Math.abs(xDirection) > MyGdxGame.WIDTH/2 * 0.4f){
            if(xDirection > 0)
                camera.position.add(cameraSpeed, 0, 0);
            else
                camera.position.add(-cameraSpeed, 0, 0);
        }
        if(Math.abs(yDirection) > MyGdxGame.HEIGHT/2 * 0.4f){
            if(yDirection > 0)
                camera.position.add(0, cameraSpeed, 0);
            else
                camera.position.add(0, -cameraSpeed, 0);
        }
    }

    public void renderBackground(float delta){
        for (BackgroundCircle bgCircle : parallaxBg.values()) {
            bgCircle.render(delta);
        }
    }

    private void initBackground() {
        sky = new Texture("background/sky.png");
        rock1 = new Texture("background/rocks_1.png");
        rock2 = new Texture("background/rocks_2.png");
        clouds1 = new Texture("background/clouds_1.png");
        clouds2 = new Texture("background/clouds_2.png");
        clouds3 = new Texture("background/clouds_3.png");
        clouds4 = new Texture("background/clouds_4.png");
        parallaxBg.put("skyBg", new BackgroundCircle(sky, batch, camera, 0));
        parallaxBg.put("rock1Bg", new BackgroundCircle(rock1, batch, camera, 0));
        parallaxBg.put("rock2Bg", new BackgroundCircle(rock2, batch, camera, -0.03f));
        parallaxBg.put("clouds1Bg", new BackgroundCircle(clouds1, batch, camera, 0.1f));
        parallaxBg.put("clouds2Bg", new BackgroundCircle(clouds2, batch, camera, -0.13f));
        parallaxBg.put("clouds3Bg", new BackgroundCircle(clouds3, batch, camera, -0.15f));
        parallaxBg.put("clouds4Bg", new BackgroundCircle(clouds4, batch, camera, -0.17f));
    }

    @Override
    public void show() {
        startMills = TimeUtils.millis();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1,1,1,1);
        time += delta;
        updateTouch();

        if (joystick.getResult().x > 0.75f && joystick.getResult().y < 0.5f) {
            player.body.applyForceToCenter(new Vector2(3000, 0), true);
            player.setDirect(1);
        }
        if (joystick.getResult().x < -0.75f && joystick.getResult().y < 0.5f) {
            player.body.applyForceToCenter(new Vector2(-3000, 0), true);
            player.setDirect(-1);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
            int jumpCounter = player.getJumpCounter();

            if (jumpCounter < 2) {
                player.body.applyLinearImpulse(new Vector2(0, 700000),
                        player.body.getPosition(), true);
                player.setJumpCounter(jumpCounter + 1);
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            player.body.applyForceToCenter(new Vector2(3000, 0), true);
            player.setDirect(1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            player.body.applyForceToCenter(new Vector2(-3000, 0), true);
            player.setDirect(-1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L) && player.getAttack() == false){
            Body body = enemy.getBody();
            player.setAttack(true);
            player.setStartAttack(TimeUtils.millis());
            if (body.getPosition().x < player.body.getPosition().x + 20 &&
            player.body.getPosition().x < body.getPosition().x && player.getDirect() == 1){
                huh.play();
                enemy.isAlive = false;
            } else if (body.getPosition().x > player.body.getPosition().x - 20 &&
                player.body.getPosition().x > body.getPosition().x && player.getDirect() == -1){
                huh.play();
                enemy.isAlive = false;
            }
        }


        moveCamera();
        if(Gdx.input.isTouched()) {
//            System.out.println(
//                    (touch.x) + ", " +
//                    (touch.y)
//            );
            System.out.println(
                    "! " + worldTouch.x + ", " + worldTouch.y
            );
       }

//        camera.position.add(
//                joystick.getResult().x/3f,
//                joystick.getResult().y/3f,
//                0
//        );
        correctCamera();

        //Обновление камеры
        camera.update();
        hudCamera.update();

        //Для отображения объектов через batch.begin() batch.end()
        batch.setProjectionMatrix(camera.combined);

        enemy.update(delta, player);
        batch.begin();
        renderBackground(delta);
        batch.end();

        //Отображение карты
        renderer.setView(camera);
        renderer.render();

        batch.begin();
        enemy.render(batch);
        player.render(delta);
        batch.end();

        //Отвечает за отрисовку границ rectangle
        b2dr.render(world, camera.combined);

        hudStage.act(delta);
        hudStage.draw();

        deleteBodies();
        //Физическая симуляция мира
        world.step(1/160f, 6, 2);

//        if (enemy.isAlive == false){
//            myGdxGame.resultScreen.setFinalTime(time);
//            myGdxGame.setScreen(myGdxGame.resultScreen);
//        }
//
//        if (Gdx.input.justTouched()){
//            enemy.isAlive = false;
//        }

    }

    private void deleteBodies() {
        for(Body body : bodyForDelete){
            if(!world.isLocked()) {
                world.destroyBody(body);

                int mapHeightInTiles = map.getProperties().get("height", Integer.class);
                int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);
                int mapHeightInPixels = mapHeightInTiles * tilePixelHeight;

                Rectangle rect = coinsRect.get(body);
                int tileX = (int) (rect.x)/tilePixelHeight;
//                int tileY = (int) (Math.abs(tilePixelHeight - rect.y));
                int tileY = (int) (rect.y)/tilePixelHeight;

                TiledMapTileLayer.Cell cell = coinLayer.getCell(tileX, tileY);

                if (cell != null) {
                    coinLayer.setCell(tileX, tileY, null);
                }else{
                    System.out.println(tileX + " and " + tileY);
                    System.out.println("Не та ячейка!");
                }
            }
        }
        bodyForDelete.clear();
    }

    private void moveCamera() {
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            camera.position.add(1, 0, 0);
    }

    @Override
    public void resize(int width, int height) {
		gameViewport.update(width, height, true);
		hudViewport.update(width, height, true);
//		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        MyGdxGame.SCREEN_WIDTH = gameViewport.getScreenWidth();
        MyGdxGame.SCREEN_HEIGHT = gameViewport.getScreenHeight();
        camera.position.set(player.body.getPosition().x, player.body.getPosition().y, 0);
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
        worldTouch = gameViewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    }
}
