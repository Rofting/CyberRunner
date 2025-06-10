package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player extends Actor {

    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    private float speed = 150f;

    public Player(TextureAtlas atlas) {
        // Creamos la animación usando las regiones del atlas llamadas "player_walk"
        walkAnimation = new Animation<>(0.1f, atlas.findRegions("player_walk"), Animation.PlayMode.LOOP);
        setBounds(getX(), getY(), walkAnimation.getKeyFrame(0).getRegionWidth(), walkAnimation.getKeyFrame(0).getRegionHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        // Movimiento con teclado
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            moveBy(-speed * delta, 0);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            moveBy(speed * delta, 0);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            moveBy(0, speed * delta);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            moveBy(0, -speed * delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, getX(), getY());
    }
}
