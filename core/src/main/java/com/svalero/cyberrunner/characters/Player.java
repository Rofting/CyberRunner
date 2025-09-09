package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.svalero.cyberrunner.managers.SoundManager;
import com.svalero.cyberrunner.screens.GameScreen;

import java.util.HashMap;
import java.util.Map;

public class Player extends Actor {

    public enum State {
        IDLE, RUNNING, JUMPING, DOUBLE_JUMPING, ATTACKING, PUNCH, HURT, DEAD
    }

    private final String characterName;
    private final GameScreen screen;
    private final SoundManager soundManager;
    private final Map<State, Animation<TextureRegion>> animations;
    private State currentState;
    private float stateTime;
    private boolean isFacingRight = true;

    private final Vector2 velocity;
    private boolean isJumping;
    private int jumpsAvailable = 2;

    private float moveSpeed = 200f;
    private float jumpVelocity = 450f;
    private static final float GRAVITY = -1000f;

    private final float mapWidth;
    private final Rectangle bounds;
    private final Array<Rectangle> collisionRects;
    private int energy = 100;

    // Buffs / powerups
    private boolean shieldActive = false;
    private float shieldTimer = 0f;
    private float speedMultiplier = 1f;
    private float speedTimer = 0f;

    public Player(String characterName, GameScreen screen, SoundManager soundManager, Array<Rectangle> collisionRects, float mapWidth) {
        this.screen = screen;
        this.soundManager = soundManager;
        this.collisionRects = collisionRects;
        this.velocity = new Vector2();
        this.animations = new HashMap<>();
        this.mapWidth = mapWidth;
        this.characterName = characterName;

        TextureAtlas atlas = screen.getGame().resourceManager.get("atlas/player.atlas", TextureAtlas.class);

        animations.put(State.RUNNING, new Animation<>(0.08f, atlas.findRegions(characterName + "_run"), Animation.PlayMode.LOOP));
        animations.put(State.IDLE, new Animation<>(0.5f, atlas.findRegions(characterName + "_idle"), Animation.PlayMode.LOOP));
        animations.put(State.JUMPING, new Animation<>(0.1f, atlas.findRegions(characterName + "_jump"), Animation.PlayMode.NORMAL));
        animations.put(State.DOUBLE_JUMPING, new Animation<>(0.1f, atlas.findRegions(characterName + "_doublejump"), Animation.PlayMode.NORMAL));
        animations.put(State.ATTACKING, new Animation<>(0.07f, atlas.findRegions(characterName + "_attack1"), Animation.PlayMode.NORMAL));
        animations.put(State.PUNCH, new Animation<>(0.07f, atlas.findRegions(characterName + "_punch"), Animation.PlayMode.NORMAL));
        animations.put(State.HURT, new Animation<>(0.1f, atlas.findRegions(characterName + "_hurt"), Animation.PlayMode.NORMAL));
        animations.put(State.DEAD, new Animation<>(0.1f, atlas.findRegions(characterName + "_death"), Animation.PlayMode.NORMAL));

        setState(State.IDLE);

        float width = 48;
        float height = 48;
        setSize(width, height);
        this.bounds = new Rectangle(getX(), getY(), width, height);

        applyCharacterStats(characterName);
    }

    private void applyCharacterStats(String name) {
        switch (name) {
            case "Rex":   this.energy = 120; this.moveSpeed = 200f; this.jumpVelocity = 450f; break;
            case "Biker": this.energy = 100; this.moveSpeed = 240f; this.jumpVelocity = 450f; break;
            case "Punk":  this.energy = 100; this.moveSpeed = 200f; this.jumpVelocity = 520f; break;
            default:      this.energy = 100; this.moveSpeed = 200f; this.jumpVelocity = 450f;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Animation<TextureRegion> currentAnimation = animations.get(currentState);
        if (currentAnimation == null || currentAnimation.getKeyFrames().length == 0) return;

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime);

        if (!isFacingRight && !frame.isFlipX()) frame.flip(true, false);
        else if (isFacingRight && frame.isFlipX()) frame.flip(true, false);

        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    public void setState(State newState) {
        if (currentState == newState) return;
        if (currentState == State.DEAD) return;

        currentState = newState;
        if (newState == State.ATTACKING || newState == State.JUMPING || newState == State.DEAD
            || newState == State.HURT || newState == State.PUNCH || newState == State.DOUBLE_JUMPING) {
            stateTime = 0;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        // timers de buffs
        if (shieldActive) { shieldTimer -= delta; if (shieldTimer <= 0) shieldActive = false; }
        if (speedMultiplier > 1f) { speedTimer -= delta; if (speedTimer <= 0) speedMultiplier = 1f; }

        velocity.y += GRAVITY * delta;
        if (currentState != State.DEAD) handleInput();
        handleCollisions(delta);
        updateState();

        if (getY() < -100){
            screen.gameOver();
        }
    }

    private void handleInput() {
        if (isAnimationPlaying()) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            setState(State.ATTACKING);
            // Solo daña si el hitbox del ataque alcanza al boss
            Rectangle hit = getAttackHitbox(40f);
            screen.damageBoss(10, hit);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            setState(State.PUNCH);
            Rectangle hit = getAttackHitbox(55f);
            screen.damageBoss(12, hit);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -moveSpeed * speedMultiplier;
            isFacingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = moveSpeed * speedMultiplier;
            isFacingRight = true;
        } else {
            velocity.x = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpsAvailable > 0) {
            velocity.y = jumpVelocity;
            soundManager.playJumpSound();
            jumpsAvailable--;
            if (isJumping) setState(State.DOUBLE_JUMPING);
            else { isJumping = true; setState(State.JUMPING); }
        }
    }


    private Rectangle getAttackHitbox(float reach) {
        float x = isFacingRight ? getX() + getWidth() : getX() - reach;
        float y = getY() + 6f;
        float w = reach;
        float h = getHeight() - 12f;
        return new Rectangle(x, y, w, h);
    }

    private void updateState() {
        if (currentState == State.DEAD || isAnimationPlaying()) return;

        if (currentState == State.DOUBLE_JUMPING && animations.get(State.DOUBLE_JUMPING).isAnimationFinished(stateTime)) {
            setState(State.JUMPING);
        }

        if (isJumping) {
            if(currentState != State.JUMPING && currentState != State.DOUBLE_JUMPING) setState(State.JUMPING);
        } else if (velocity.x != 0) {
            setState(State.RUNNING);
        } else {
            setState(State.IDLE);
        }
    }

    private boolean isAnimationPlaying() {
        if (currentState == State.ATTACKING || currentState == State.PUNCH || currentState == State.HURT) {
            Animation<TextureRegion> anim = animations.get(currentState);
            return anim != null && !anim.isAnimationFinished(stateTime);
        }
        return false;
    }

    private void handleCollisions(float delta) {
        setX(getX() + velocity.x * delta);
        bounds.x = getX();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                if (velocity.x > 0) setX(rect.x - getWidth());
                else if (velocity.x < 0) setX(rect.x + rect.width);
                velocity.x = 0;
                bounds.x = getX();
                break;
            }
        }
        setY(getY() + velocity.y * delta);
        bounds.y = getY();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                if (velocity.y > 0) setY(rect.y - getHeight());
                else if (velocity.y < 0) {
                    setY(rect.y + rect.height);
                    isJumping = false;
                    jumpsAvailable = 2;
                }
                velocity.y = 0;
                bounds.y = getY();
                break;
            }
        }

        if (getX() < 0) { setX(0); velocity.x = 0; }
        if (getX() + getWidth() > mapWidth) { setX(mapWidth - getWidth()); velocity.x = 0; }
    }

    public void takeDamage(int damage) {
        if (shieldActive) return;
        this.energy -= damage;
        setState(State.HURT);
        if (this.energy <= 0) {
            setState(State.DEAD);
            screen.gameOver();
        }
    }

    // --- Powerups ---
    public void heal(int amount) {
        this.energy = Math.min(200, this.energy + amount);
        screen.updateEnergyLabel(this.energy);
    }
    public void activateShield(float durationSec) {
        this.shieldActive = true;
        this.shieldTimer = durationSec;
    }
    public void activateSpeedBoost(float multiplier, float durationSec) {
        this.speedMultiplier = multiplier;
        this.speedTimer = durationSec;
    }

    public void setEnergy(int newEnergy) { this.energy = newEnergy; }
    public Rectangle getBounds() { bounds.setPosition(getX(), getY()); return bounds; }
    public int getEnergy() { return energy; }
    public String getCharacterName() { return characterName; }
}

