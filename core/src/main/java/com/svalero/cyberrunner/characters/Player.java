package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;

public class Player extends Actor {

    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    private Vector2 velocity = new Vector2();
    private boolean isJumping = false;

    private static final float GRAVITY = -980f;
    private static final float JUMP_VELOCITY = 400f;
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

        velocity.y += GRAVITY * delta;

        // Movilidad
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            velocity.x = -speed * delta;
        } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            velocity.x = speed;
        } else {
            velocity.x = 0;
        }

        //Salto
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !isJumping) {
            velocity.y = JUMP_VELOCITY;
            isJumping = true;
        }

        //Caida
        if (getY() < 50) {
            setY(50);
            isJumping = false;
        }

        moveBy(velocity.x * delta, velocity.y * delta);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, getX(), getY());
    }
}
