package com.svalero.cyberrunner.objects; // O el paquete donde la hayas creado

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Coin extends Actor {

    private final Texture texture;
    public final Rectangle bounds;

    public Coin(float x, float y) {
        // --- CREACIÓN DEL CUADRADO AMARILLO ---
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.YELLOW);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();

        // --- POSICIÓN Y TAMAÑO ---
        setPosition(x, y);
        setSize(16, 16);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
    }
}
