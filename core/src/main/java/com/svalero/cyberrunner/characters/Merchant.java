package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Merchant extends Actor {

    private final Texture texture;
    public final Rectangle bounds;

    public Merchant(float x, float y) {
        Pixmap pixmap = new Pixmap(32, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CYAN);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();

        setSize(32, 64);
        setPosition(x, y);
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
