package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Enemy extends Actor {

    protected Animation<TextureRegion> animation;
    protected float stateTime;
    protected float speed = 80f;

    public Enemy(TextureAtlas atlas, String regionName, float animationSpeed) {
        this.animation = new Animation<>(animationSpeed, atlas.findRegions(regionName), Animation.PlayMode.LOOP);
        setBounds(getX(), getY(), animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = animation.getKeyFrame(stateTime);
        batch.draw(frame, getX(), getY());
    }

    @Override
    public abstract void act(float delta);
}
