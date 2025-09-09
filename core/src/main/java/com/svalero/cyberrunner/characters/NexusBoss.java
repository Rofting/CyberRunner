package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.svalero.cyberrunner.screens.GameScreen;
import java.util.HashMap;
import java.util.Map;

public class NexusBoss extends Actor {

    public enum State {
        ATTACKING, VULNERABLE, HURT, DYING
    }

    private final GameScreen screen;
    private final Map<State, Animation<TextureRegion>> animations;
    private State currentState;
    private State previousState;
    private float stateTime = 0f;

    private int health = 200;

    private int minionsSpawnedThisPhase = 0;
    private final float attackPhaseDuration = 8f;
    private final float vulnerablePhaseDuration = 4f;

    public final Rectangle bounds;

    public NexusBoss(GameScreen screen, TextureAtlas atlas, float x, float y) {
        this.screen = screen;
        this.animations = new HashMap<>();

        animations.put(State.ATTACKING, new Animation<>(0.1f, atlas.findRegions("Nexus_attack1"), Animation.PlayMode.LOOP));
        animations.put(State.VULNERABLE, new Animation<>(0.2f, atlas.findRegions("Nexus_idle"), Animation.PlayMode.LOOP));
        animations.put(State.HURT, new Animation<>(0.1f, atlas.findRegions("Nexus_hurt"), Animation.PlayMode.NORMAL));
        animations.put(State.DYING, new Animation<>(0.15f, atlas.findRegions("Nexus_death"), Animation.PlayMode.NORMAL));

        setState(State.ATTACKING);

        setSize(animations.get(State.ATTACKING).getKeyFrame(0).getRegionWidth(), animations.get(State.ATTACKING).getKeyFrame(0).getRegionHeight());
        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        stateTime += delta;

        if (currentState == State.HURT && animations.get(State.HURT).isAnimationFinished(stateTime)) {
            setState(previousState);
        }

        switch (currentState) {
            case ATTACKING:
                if (stateTime > 2f && minionsSpawnedThisPhase == 0) {
                    spawnMinion();
                    minionsSpawnedThisPhase = 1;
                } else if (stateTime > 4f && minionsSpawnedThisPhase == 1) {
                    spawnMinion();
                    minionsSpawnedThisPhase = 2;
                } else if (stateTime > 6f && minionsSpawnedThisPhase == 2) {
                    spawnMinion();
                    minionsSpawnedThisPhase = 3;
                }

                if (stateTime > attackPhaseDuration) {
                    setState(State.VULNERABLE);
                }
                break;
            case VULNERABLE:
                if (stateTime > vulnerablePhaseDuration) {
                    setState(State.ATTACKING);
                }
                break;
            case DYING:
                if (animations.get(State.DYING).isAnimationFinished(stateTime)) {
                    this.remove();
                    screen.winGame();
                }
                break;
        }
    }

    public void setState(State newState) {
        if (currentState == newState) return;

        if (currentState != State.HURT && currentState != State.DYING) {
            this.previousState = currentState;
        }

        currentState = newState;
        stateTime = 0;

        if (newState == State.ATTACKING) {
            minionsSpawnedThisPhase = 0;
        }
    }

    private void spawnMinion() {
        screen.spawnDrone(getX(), getY() + getHeight() / 2);
    }

    public void takeDamage(int amount) {
        if (currentState != State.VULNERABLE) return;
        health -= amount;
        System.out.println("Vida del Jefe: " + health);
        if (health <= 0) {
            setState(State.DYING);
        } else {
            setState(State.HURT);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Animation<TextureRegion> anim = animations.get(currentState);
        if (anim == null || anim.getKeyFrames().length == 0) return;
        batch.draw(anim.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }
}
