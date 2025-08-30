package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Merchant extends Actor {

    private final Animation<TextureRegion> idleAnimation;
    private float stateTime;
    public final Rectangle bounds;

    public Merchant(TextureAtlas atlas, float x, float y) {
        this.stateTime = 0f;

        this.idleAnimation = new Animation<>(0.2f, atlas.findRegions("Mercader/Mercader_idle"), Animation.PlayMode.LOOP);

        if (idleAnimation.getKeyFrames().length > 0) {
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
        if (idleAnimation.getKeyFrames().length == 0) return;

        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}
