package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Merchant extends Actor {

    private Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;
    public final Rectangle bounds;

    public Merchant(TextureAtlas atlas, float x, float y) {
        String[] candidates = new String[] {
            "Enemies/Mercader/Mercader_idle",
            "Mercader/Mercader_idle",
            "Enemies/Mercader/idle",
            "Mercader/idle",
            "Mercader_idle",
            "Mercader"
        };

        Array<TextureAtlas.AtlasRegion> regions = new Array<>();
        for (String name : candidates) {
            Array<TextureAtlas.AtlasRegion> found = atlas.findRegions(name);
            if (found != null && found.size > 0) {
                regions = found;
                Gdx.app.log("Merchant", "Animación encontrada: '" + name + "' (" + found.size + " frames)");
                break;
            }
        }

        if (regions.size == 0) {
            Gdx.app.error("Merchant", "No se encontraron regiones para Mercader en el atlas. Revisa el nombre exacto en enemies.atlas.");
            setSize(0, 0);
            setPosition(x, y);
            this.bounds = new Rectangle(x, y, getWidth(), getHeight());
            return;
        }

        idleAnimation = new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
        setSize(regions.first().getRegionWidth(), regions.first().getRegionHeight());
        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        bounds.setPosition(getX(), getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (idleAnimation == null || idleAnimation.getKeyFrames().length == 0) return;
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        bounds.setPosition(getX(), getY());
        return bounds;
    }
}
