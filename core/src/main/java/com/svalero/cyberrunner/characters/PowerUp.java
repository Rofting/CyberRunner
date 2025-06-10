package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Clase  base para todos los powerups del juego.
 * El jugador podrá interactuar con ellos al tocarlos.
 */
public abstract class PowerUp extends Actor {

    protected Animation<TextureRegion> animation;
    protected float stateTime;

    public PowerUp(TextureAtlas atlas, String regionName, float frameDuration) {
        animation = new Animation<>(frameDuration, atlas.findRegions(regionName), Animation.PlayMode.LOOP);
        setBounds(getX(), getY(), animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public abstract void act(float delta);
}
