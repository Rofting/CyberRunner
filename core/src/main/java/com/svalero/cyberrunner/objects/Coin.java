package com.svalero.cyberrunner.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Coin extends Actor {

    private final Animation<TextureRegion> idleAnimation;
    private float stateTime;
    public final Rectangle bounds;


    private Texture fallbackTexture;
    private boolean useFallback = false;

    public Coin(TextureAtlas atlas, float x, float y) {
        this.stateTime = 0f;

        Animation<TextureRegion> anim = new Animation<>(0.1f, atlas.findRegions("Coin"), Animation.PlayMode.LOOP);
        if (anim.getKeyFrames().length == 0) {
            useFallback = true;
            fallbackTexture = new Texture(Gdx.files.internal("items/coin.png"));
            setSize(fallbackTexture.getWidth(), fallbackTexture.getHeight());
            this.idleAnimation = null;
        } else {
            this.idleAnimation = anim;
            setSize(idleAnimation.getKeyFrame(0).getRegionWidth(), idleAnimation.getKeyFrame(0).getRegionHeight());
        }

        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (useFallback) {
            batch.draw(fallbackTexture, getX(), getY(), getWidth(), getHeight());
        } else {
            if (idleAnimation == null || idleAnimation.getKeyFrames().length == 0) return;
            batch.draw(idleAnimation.getKeyFrame(stateTime, true), getX(), getY(), getWidth(), getHeight());
        }
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }

    @Override
    public boolean remove() {
        boolean removed = super.remove();
        if (removed && useFallback && fallbackTexture != null) {
            fallbackTexture.dispose();
            fallbackTexture = null;
        }
        return removed;
    }
}
