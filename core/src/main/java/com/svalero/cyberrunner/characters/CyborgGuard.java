package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CyborgGuard extends Enemy {

    private Player player;
    private float visionRange = 200f;

    public CyborgGuard(TextureAtlas atlas, Player player) {
        super(atlas, "cyborg", 0.1f);
        this.player = player;
    }

    @Override
    public void act(float delta) {
        super.stateTime += delta;

        float distance = Math.abs(player.getX() - getX());

        if (distance < visionRange) {
            if (player.getX() > getX()) {
                moveBy(speed * delta, 0);
            } else {
                moveBy(-speed * delta, 0);
            }
        } else {
            moveBy(speed * delta, 0);
            if (getX() > 700 || getX() < 100) {
                speed = -speed;
            }
        }
    }
}
