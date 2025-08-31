package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.svalero.cyberrunner.managers.SoundManager;
import com.svalero.cyberrunner.objects.Bullet;

public class Turret extends Actor {

    private enum State { IDLE, ATTACKING }

    private final SoundManager soundManager;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> attackAnimation;
    private State currentState;
    private float stateTime = 0f;

    private float shootTimer = 0f;
    private final float shootInterval = 2f;

    public Turret(TextureAtlas atlas, SoundManager soundManager, float x, float y) {
        this.soundManager = soundManager;
        this.currentState = State.IDLE;

        this.idleAnimation = new Animation<>(0.2f, atlas.findRegions("Tower_idle"), Animation.PlayMode.LOOP);
        this.attackAnimation = new Animation<>(0.1f, atlas.findRegions("Tower_attack"), Animation.PlayMode.NORMAL);

        if (idleAnimation.getKeyFrames().length > 0) {
            setSize(idleAnimation.getKeyFrame(0).getRegionWidth(), idleAnimation.getKeyFrame(0).getRegionHeight());
        }

        setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
        shootTimer += delta;

        if (currentState == State.ATTACKING && attackAnimation.isAnimationFinished(stateTime)) {
            currentState = State.IDLE;
        }

        if (shootTimer >= shootInterval) {
            shoot();
            shootTimer = 0f;
        }
    }

    private void shoot() {
        Stage stage = getStage();
        if (stage != null) {
            currentState = State.ATTACKING;
            stateTime = 0;


            Bullet bullet = new Bullet(getX(), getY() + getHeight() / 2);
            stage.addActor(bullet);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Animation<TextureRegion> currentAnimation = (currentState == State.ATTACKING) ? attackAnimation : idleAnimation;
        if (currentAnimation == null || currentAnimation.getKeyFrames().length == 0) return;

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}
