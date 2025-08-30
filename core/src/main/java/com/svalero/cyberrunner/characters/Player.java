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
        IDLE, RUNNING, JUMPING, ATTACKING, PUNCH, HURT, DEAD
    }

    private final GameScreen screen;
    private final SoundManager soundManager;
    private final Map<State, Animation<TextureRegion>> animations;
    private State currentState;
    private float stateTime;
    private boolean isFacingRight = true;

    private final Vector2 velocity;
    private boolean isJumping;
    private static final float SPEED = 200f;
    private static final float JUMP_VELOCITY = 450f;
    private static final float GRAVITY = -1000f;

    private final Rectangle bounds;
    private final Array<Rectangle> collisionRects;
    private int energy = 100;

    public Player(String characterName, GameScreen screen, SoundManager soundManager, Array<Rectangle> collisionRects) {
        this.screen = screen;
        this.soundManager = soundManager;
        this.collisionRects = collisionRects;
        this.velocity = new Vector2();
        this.animations = new HashMap<>();

        TextureAtlas atlas = screen.getGame().resourceManager.get("atlas/player.atlas", TextureAtlas.class);

        // --- CARGA DE TODAS LAS ANIMACIONES DE Personaje ---
        animations.put(State.RUNNING, new Animation<>(0.08f, atlas.findRegions("Rex_run"), Animation.PlayMode.LOOP));
        animations.put(State.IDLE, new Animation<>(0.5f, atlas.findRegions("Rex_idle"), Animation.PlayMode.LOOP));
        animations.put(State.JUMPING, new Animation<>(0.1f, atlas.findRegions("Rex_jump"), Animation.PlayMode.NORMAL));
        animations.put(State.ATTACKING, new Animation<>(0.07f, atlas.findRegions("Rex_attack1"), Animation.PlayMode.NORMAL));
        animations.put(State.PUNCH, new Animation<>(0.07f, atlas.findRegions("Rex_punch"), Animation.PlayMode.NORMAL));
        animations.put(State.HURT, new Animation<>(0.1f, atlas.findRegions("Rex_hurt"), Animation.PlayMode.NORMAL));
        animations.put(State.DEAD, new Animation<>(0.1f, atlas.findRegions("Rex_death"), Animation.PlayMode.NORMAL));

        setState(State.IDLE);

        float width = 48;
        float height = 48;
        setSize(width, height);
        this.bounds = new Rectangle(getX(), getY(), width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Animation<TextureRegion> currentAnimation = animations.get(currentState);
        if (currentAnimation == null || currentAnimation.getKeyFrames().length == 0) return;

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime);

        // Lógica para girar el sprite si mira a la izquierda
        if (!isFacingRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (isFacingRight && frame.isFlipX()) {
            frame.flip(true, false);
        }
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    public void setState(State newState) {
        if (currentState == newState) return;

        // Evita que un estado final como DEAD sea interrumpido
        if (currentState == State.DEAD) return;

        currentState = newState;
        // Reinicia el tiempo para animaciones que no se repiten
        if (newState == State.ATTACKING || newState == State.JUMPING || newState == State.DEAD || newState == State.HURT || newState == State.PUNCH) {
            stateTime = 0;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        velocity.y += GRAVITY * delta;
        if (currentState != State.DEAD) handleInput();
        handleCollisions(delta);
        updateState();
    }

    private void handleInput() {
        // No permite otras acciones si una animación de un solo uso está en curso
        if (isAnimationPlaying()) return;

        // Ataque
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            setState(State.ATTACKING);
            return;
        }
        // Puñetazo
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            setState(State.PUNCH);
            return;
        }

        // Movimiento Horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -SPEED;
            isFacingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = SPEED;
            isFacingRight = true;
        } else {
            velocity.x = 0;
        }

        // Salto
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !isJumping) {
            velocity.y = JUMP_VELOCITY;
            soundManager.playJumpSound();
            isJumping = true;
        }
    }

    private void updateState() {
        // Estados prioritarios que no deben ser interrumpidos
        if (currentState == State.DEAD || isAnimationPlaying()) return;

        // Lógica de estado normal
        if (isJumping) {
            setState(State.JUMPING);
        } else if (velocity.x != 0) {
            setState(State.RUNNING);
        } else {
            setState(State.IDLE);
        }
    }
    private boolean isAnimationPlaying() {
        Animation<TextureRegion> anim = animations.get(currentState);
        if (currentState == State.ATTACKING || currentState == State.PUNCH || currentState == State.HURT) {
            return !anim.isAnimationFinished(stateTime);
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
                }
                velocity.y = 0;
                bounds.y = getY();
                break;
            }
        }
    }

    public void takeDamage(int damage) {
        this.energy -= damage;
        setState(State.HURT);
        System.out.println("Energia restante: " + energy);
        if (this.energy <= 0) {
            setState(State.DEAD);
            screen.gameOver();
        }
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }
    public int getEnergy() { return energy; }

}
