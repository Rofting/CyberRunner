package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.svalero.cyberrunner.screens.GameScreen;

public class Player extends Actor {

    private final GameScreen screen;

    private final Texture debugTexture;

    private final Vector2 velocity;
    private boolean isJumping;
    private static final float SPEED = 200f;
    private static final float JUMP_VELOCITY = 450f;
    private static final float GRAVITY = -1000f;

    private final Rectangle bounds;
    private final Array<Rectangle> collisionRects;

    private int energy = 100;

    public Player(GameScreen screen, Array<Rectangle> collisionRects) {
        this.screen = screen;
        this.collisionRects = collisionRects;
        this.velocity = new Vector2();

        // --- CREACIÓN DEL CUADRADO VERDE DE PRUEBA ---
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        debugTexture = new Texture(pixmap);
        pixmap.dispose();

        float width = 48;
        float height = 48;
        setSize(width, height);
        this.bounds = new Rectangle(getX(), getY(), width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        velocity.y += GRAVITY * delta;
        handleInput();
        handleCollisions(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.draw(debugTexture, getX(), getY(), getWidth(), getHeight());
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = SPEED;
        } else {
            velocity.x = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !isJumping) {
            velocity.y = JUMP_VELOCITY;
            isJumping = true;
        }
    }

    private void handleCollisions(float delta) {
        float oldX = getX(), oldY = getY();

        setX(getX() + velocity.x * delta);
        bounds.x = getX();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                setX(oldX);
                velocity.x = 0;
                break;
            }
        }

        setY(getY() + velocity.y * delta);
        bounds.y = getY();
        for (Rectangle rect : collisionRects) {
            if (bounds.overlaps(rect)) {
                if (velocity.y < 0) {
                    isJumping = false;
                }
                setY(oldY);
                velocity.y = 0;
                break;
            }
        }
    }

    public void takeDamage(int damage){
        this.energy -= damage;
        System.out.println("Energy: " + energy);

        if (this.energy <= 0) {
            System.out.println("Game Over!");
        }
    }
    public int getEnergy() {
        return energy;
    }

    public void dispose() {
        debugTexture.dispose();
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }
}
