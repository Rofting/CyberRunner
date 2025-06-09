package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;

public class Drone extends Enemy {

    private boolean movingRight = true;

    public Drone(TextureAtlas atlas) {
        super(atlas, "drone", 0.1f);
    }

    @Override
    public void act(float delta) {
        super.stateTime += delta;

        if (movingRight) {
            moveBy(speed * delta, 0);
            if (getX() > 700) movingRight = false;
        } else {
            moveBy(-speed * delta, 0);
            if (getX() < 100) movingRight = true;
        }
    }
}

