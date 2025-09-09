package com.svalero.cyberrunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;
import com.svalero.cyberrunner.managers.ResourceManager;
import com.svalero.cyberrunner.managers.ScoreManager;
import com.svalero.cyberrunner.managers.SoundManager;
import com.svalero.cyberrunner.screens.MenuScreen;

public class CyberRunner extends Game {

    public SpriteBatch batch;
    public ResourceManager resourceManager;
    public SoundManager soundManager;
    public ScoreManager scoreManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        resourceManager = new ResourceManager();
        soundManager = new SoundManager(resourceManager);
        scoreManager = new ScoreManager();

        VisUI.load();
        this.setScreen(new MenuScreen(this));
    }

    @Override public void render() { super.render(); }

    @Override
    public void dispose() {
        batch.dispose();
        resourceManager.dispose();
        VisUI.dispose();
    }
}
