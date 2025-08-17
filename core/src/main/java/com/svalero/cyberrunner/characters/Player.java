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

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un personaje jugable. Esta clase es flexible y puede representar
 * a cualquier personaje (Rex, Kael, Anya) dependiendo del prefijo de atlas que se le pase.
 */
public class Player extends Actor {

    public enum State {
        IDLE, RUNNING, JUMPING, ATTACKING, DEAD
    }

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

    /**
     * Constructor para un personaje jugable.
     * @param characterPrefix El prefijo para encontrar las animaciones en el atlas (ej: "player/Rex/Cyborg")
     * @param atlas El TextureAtlas que contiene todos los sprites.
     * @param collisionRects La lista de rectángulos de colisión del nivel.
     */
    public Player(String characterPrefix, TextureAtlas atlas, Array<Rectangle> collisionRects) {
        this.collisionRects = collisionRects;
        this.velocity = new Vector2();
        this.animations = new HashMap<>();

        // --- CARGA DE ANIMACIONES DINÁMICA ---
        // Construye el nombre de la animación usando el prefijo del personaje
        animations.put(State.RUNNING, new Animation<>(0.08f, atlas.findRegions(characterPrefix + "_run"), Animation.PlayMode.LOOP));
        animations.put(State.IDLE, new Animation<>(0.5f, atlas.findRegions(characterPrefix + "_idle"), Animation.PlayMode.LOOP));
        animations.put(State.JUMPING, new Animation<>(0.1f, atlas.findRegions(characterPrefix + "_jump"), Animation.PlayMode.NORMAL));
        animations.put(State.ATTACKING, new Animation<>(0.07f, atlas.findRegions(characterPrefix + "_attack1"), Animation.PlayMode.NORMAL));
        animations.put(State.DEAD, new Animation<>(0.1f, atlas.findRegions(characterPrefix + "_death"), Animation.PlayMode.NORMAL));

        // Establece el estado inicial
        setState(State.IDLE);

        // Configura el tamaño y el hitbox del jugador. AJUSTA ESTOS VALORES si tus personajes tienen tamaños diferentes.
        float width = 48;
        float height = 48;
        setSize(width, height);
        this.bounds = new Rectangle(getX(), getY(), width, height);
    }

    public void setState(State newState) {
        if (currentState == newState) return;

        currentState = newState;
        if (newState == State.ATTACKING || newState == State.JUMPING || newState == State.DEAD) {
            stateTime = 0;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        velocity.y += GRAVITY * delta;

        if (currentState != State.DEAD) {
            handleInput();
        }

        handleCollisions(delta);
        updateState();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -SPEED;
            isFacingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = SPEED;
            isFacingRight = true;
        } else {
            velocity.x = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !isJumping) {
            velocity.y = JUMP_VELOCITY;
            isJumping = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            setState(State.ATTACKING);
        }
    }

    private void updateState() {
        if (currentState == State.DEAD) return;
        if (currentState == State.ATTACKING && !animations.get(State.ATTACKING).isAnimationFinished(stateTime)) return;

        if (isJumping) {
            setState(State.JUMPING);
        } else if (velocity.x != 0) {
            setState(State.RUNNING);
        } else {
            setState(State.IDLE);
        }
    }

    private void handleCollisions(float delta) {
        float oldX = getX(), oldY = getY();
        boolean collidedX = false, collidedY = false;

        setX(getX() + velocity.x * delta);
        bounds.x = getX();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                collidedX = true;
                break;
            }
        }
        if (collidedX) {
            setX(oldX);
            velocity.x = 0;
        }

        setY(getY() + velocity.y * delta);
        bounds.y = getY();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                collidedY = true;
                break;
            }
        }
        if (collidedY) {
            if (velocity.y < 0) {
                isJumping = false;
            }
            setY(oldY);
            velocity.y = 0;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Animation<TextureRegion> currentAnimation = animations.get(currentState);
        if (currentAnimation == null || currentAnimation.getKeyFrames().length == 0) return;

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime);

        if (!isFacingRight && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (isFacingRight && frame.isFlipX()) {
            frame.flip(true, false);
        }

        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }
}
