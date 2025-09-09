package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public abstract class NPC extends Actor {

    protected Animation<TextureRegion> animation;
    protected float stateTime;

    public NPC(TextureAtlas atlas, String regionName, float frameDuration) {
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(regionName);
        if (regions == null || regions.size == 0) {
            Gdx.app.error("NPC", "No se encontraron regiones para '" + regionName + "' en el atlas.");
            animation = new Animation<>(frameDuration, new Array<TextureRegion>());
            setSize(0, 0);
        } else {
            animation = new Animation<>(frameDuration, regions, Animation.PlayMode.LOOP);
            setSize(regions.first().getRegionWidth(), regions.first().getRegionHeight());
        }
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animation == null || animation.getKeyFrames().length == 0) return;
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.draw(frame, getX(), getY());
    }

    @Override
    public abstract void act(float delta);
}
