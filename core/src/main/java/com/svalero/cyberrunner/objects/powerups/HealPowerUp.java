package com.svalero.cyberrunner.objects.powerups;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.svalero.cyberrunner.characters.Player;

public class HealPowerUp extends Actor {

    private final Animation<TextureRegion> anim;
    private float time = 0f;
    private final Rectangle bounds;
    private final int healAmount;

    public HealPowerUp(TextureAtlas atlas, float x, float y, int healAmount) {
        this.anim = new Animation<>(0.1f, atlas.findRegions("Powerups/Heal"), Animation.PlayMode.LOOP);
        setSize(anim.getKeyFrame(0).getRegionWidth(), anim.getKeyFrame(0).getRegionHeight());
        setPosition(x, y);
        this.bounds = new Rectangle(x, y, getWidth(), getHeight());
        this.healAmount = healAmount;
    }

    @Override public void act(float delta) {
        time += delta;
        bounds.setPosition(getX(), getY());
        for (Actor a : getStage().getActors()) {
            if (a instanceof Player) {
                Player p = (Player) a;
                if (p.getBounds().overlaps(bounds)) {
                    p.heal(healAmount);
                    remove();
                    break;
                }
            }
        }
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = anim.getKeyFrame(time, true);
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }
}
