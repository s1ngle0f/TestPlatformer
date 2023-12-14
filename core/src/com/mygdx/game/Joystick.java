package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Joystick extends Actor {
    private OrthographicCamera camera;
    private Texture bgCircle, fgTexture;
    private float bgCircleSize, fgTextureSize, currentLength;
    private boolean isStatic = true, isTouchedInsideCircle = false;
    private Vector2 centerPosition = new Vector2(), activeCenterPosition = new Vector2();
    private Vector2 result = new Vector2();
    private FitViewport gameViewport;
    private float defaultX = 15, defaultY = 15;
    private boolean isTouched = false;
    private InputProcessor inputProcessor = new InputProcessor() {
        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(pointer == 0){
                if (isStatic) {
                    centerPosition.set(
                            defaultX,
                            defaultY
                    );
                }else if (screenX <= MyGdxGame.SCREEN_WIDTH/2) {
                    centerPosition.set(
                            gameViewport.unproject(
                                    new Vector2(
                                            (screenX),
                                            (screenY)
                                    )
                            )
                    );
                }
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(pointer == 0)
                resetResult();
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if(pointer == 0){
                if (isStatic) {
                    if (new Vector2(
                            gameViewport.unproject(
                                    new Vector2(
                                            (screenX),
                                            (screenY)
                                    )
                            )
                    ).sub(
                            centerPosition.x,
                            centerPosition.y
                    ).len() <= bgCircleSize / 2f) {
                        isTouchedInsideCircle = true;
                    }
                }
                if((!isStatic || isTouchedInsideCircle)
                        && screenX <= MyGdxGame.SCREEN_WIDTH/2){
                    activeCenterPosition.set(
                            gameViewport.unproject(
                                    new Vector2(
                                            (screenX),
                                            (screenY)
                                    )
                            )
                    );
                }
                activeCenterPosition = limitVector(centerPosition, activeCenterPosition, bgCircleSize/2f);
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    };
    public Joystick(FitViewport gameViewport, OrthographicCamera camera, Texture bgCircle, Texture fgTexture, float bgCircleSize, float fgTextureSize) {
        this.gameViewport = gameViewport;
        this.camera = camera;
        this.bgCircle = bgCircle;
        this.fgTexture = fgTexture;
        this.bgCircleSize = bgCircleSize;
        this.fgTextureSize = fgTextureSize;

        Gdx.input.setInputProcessor(inputProcessor);
    }

    public void setDefaultPosition(float x, float y){
        defaultX = x;
        defaultY = y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
//        calculatePosition();
        editResult();
        if(isStatic || Gdx.input.isTouched()){
                batch.draw(bgCircle,
                        centerPosition.x - bgCircleSize / 2f,
                        centerPosition.y - bgCircleSize / 2f,
                        bgCircleSize,
                        bgCircleSize
                );
                batch.draw(fgTexture,
                        activeCenterPosition.x - fgTextureSize / 2f,
                        activeCenterPosition.y - fgTextureSize / 2f,
                        fgTextureSize,
                        fgTextureSize
                );
        }
//        if(!Gdx.input.isTouched() && result.x != 0 && result.y != 0)
//            resetResult();
    }

    private void resetResult() {
        result.set(0, 0);
        isTouchedInsideCircle = false;
        activeCenterPosition.set(centerPosition);
//        if(isStatic){
//            centerPosition.set(
//                    defaultX,
//                    defaultY
//            );
//        }
//        else {
//            centerPosition.set(
//                    -100,
//                    -100
//            );
//        }
//        activeCenterPosition.set(centerPosition);
    }

    private void calculatePosition() {
        if(isStatic) {
            centerPosition.set(
                    defaultX,
                    defaultY
            );
            if(Gdx.input.isTouched() &&
                    new Vector2(
                            gameViewport.unproject(
                                    new Vector2(
                                            (Gdx.input.getX()),
                                            (Gdx.input.getY())
                                    )
                            )
                    ).sub(
                            centerPosition.x,
                            centerPosition.y
                    ).len() <= bgCircleSize/2f){
                isTouchedInsideCircle = true;
            }
        }
        else if (Gdx.input.justTouched() && Gdx.input.getX() <= MyGdxGame.SCREEN_WIDTH/2 && !isTouched) {
            centerPosition.set(
                    gameViewport.unproject(
                            new Vector2(
                                    (Gdx.input.getX()),
                                    (Gdx.input.getY())
                            )
                    )
            );
        }
        if(Gdx.input.isTouched() && (!isStatic || isTouchedInsideCircle) && !isTouched
                && Gdx.input.getX() <= MyGdxGame.SCREEN_WIDTH/2){
            activeCenterPosition.set(
                    gameViewport.unproject(
                            new Vector2(
                                    (Gdx.input.getX()),
                                    (Gdx.input.getY())
                            )
                    )
            );
        }
        activeCenterPosition = limitVector(centerPosition, activeCenterPosition, bgCircleSize/2f);
    }

    public void editResult(){
        Vector2 tmp = new Vector2(activeCenterPosition.x, activeCenterPosition.y);
        tmp = tmp.sub(centerPosition);
        tmp = tmp.nor();
        result.set(
                tmp.x * (currentLength/(bgCircleSize/2f)),
                tmp.y * (currentLength/(bgCircleSize/2f))
        );
    }

    public Vector2 getResult(){
        return result;
    }

    public Vector2 limitVector(Vector2 origin, Vector2 target, float limit){
        Vector2 relativeVector = new Vector2(target.x, target.y);
        relativeVector.sub(origin);
        currentLength = relativeVector.len();
        if(currentLength > limit){
            relativeVector.nor();
            relativeVector.set(
                    relativeVector.x * limit,
                    relativeVector.y * limit
            );
        }
        target = new Vector2(
                origin.x + relativeVector.x,
                origin.y + relativeVector.y
        );
        return target;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
}
