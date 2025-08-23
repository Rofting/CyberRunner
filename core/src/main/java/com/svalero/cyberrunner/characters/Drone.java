package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle; // <-- IMPORTACIÓN CORREGIDA
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Drone extends Actor {

    public final Rectangle bounds;
    private final Texture texture;
    private float speed = 100f;
    private final float startX;
    private final float patrolRange = 150f;

    public Drone(float x, float y) {
        Pixmap pixmap = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();

        // --- POSICIÓN Y TAMAÑO ---
        this.startX = x;
        setPosition(x, y);
        setSize(24, 24);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        moveBy(speed * delta, 0);

        if (getX() > startX + patrolRange || getX() < startX) {
            speed = -speed;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
