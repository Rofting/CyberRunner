package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Drone extends Actor {

    public final Rectangle bounds;
    private float speed = 100f;
    private final float startX;
    private final float patrolRange = 150f;
    private final Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private boolean isFacingRight = true;

    public Drone(TextureAtlas atlas, float x, float y) {
        this.stateTime = 0f;


        this.walkAnimation = new Animation<>(0.1f, atlas.findRegions("Drone_walk"), Animation.PlayMode.LOOP);
        this.startX = x;

        if (walkAnimation.getKeyFrames().length > 0) {
            setSize(walkAnimation.getKeyFrame(0).getRegionWidth(), walkAnimation.getKeyFrame(0).getRegionHeight());
        }

        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        moveBy(speed * delta, 0);

        if (speed > 0 && getX() >= startX + patrolRange) {
            speed = -speed;
            isFacingRight = false;
        } else if (speed < 0 && getX() <= startX) {
            speed = -speed;
            isFacingRight = true;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (walkAnimation.getKeyFrames().length == 0) return;

        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        if (!isFacingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (isFacingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }
}
