package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Drone extends Actor {

    private final Vector2 velocity;
    private static final float GRAVITY = -1000f;
    public final Rectangle bounds;
    private float speed = 100f;
    private final float startX;
    private final float patrolRange = 150f;
    private final Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private boolean isFacingRight = true;
    private final Array<Rectangle> collisionRects;

    public Drone(TextureAtlas atlas, Array<Rectangle> collisionRects, float x, float y) {
        this.stateTime = 0f;
        this.velocity = new Vector2();
        this.collisionRects = collisionRects;

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

        velocity.y += GRAVITY * delta;

        velocity.x = speed;

        if (speed > 0 && getX() >= startX + patrolRange) {
            speed = -speed;
            isFacingRight = false;
        } else if (speed < 0 && getX() <= startX) {
            speed = -speed;
            isFacingRight = true;
        }
        float oldX = getX();

        setX(getX() + velocity.x * delta);
        bounds.x = getX();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                setX(oldX);
                speed = -speed;
                break;
            }
        }

        setY(getY() + velocity.y * delta);
        bounds.y = getY();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                setY(rect.y + rect.height);
                velocity.y = 0;
                break;
            }
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
