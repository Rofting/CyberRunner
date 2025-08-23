package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.svalero.cyberrunner.objects.Bullet;

public class Turret extends Actor {

    private final Texture texture;
    private float shootTimer = 0f;
    private final float shootInterval = 2f; // Dispara cada 2 segundos

    public Turret(float x, float y) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();


        setPosition(x, y);
        setSize(32, 32);
    }

    @Override
    public void act(float delta) {
        shootTimer += delta;

        if (shootTimer >= shootInterval) {
            shoot();
            shootTimer = 0f;
        }
    }

    private void shoot() {
        Stage stage = getStage();
        if (stage != null) {
            Bullet bullet = new Bullet(getX(), getY() + getHeight() / 2);
            stage.addActor(bullet);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        texture.dispose();
    }
}
