package com.svalero.cyberrunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.cyberrunner.managers.ResourceManager;
import com.svalero.cyberrunner.managers.SoundManager;
import com.svalero.cyberrunner.screens.MenuScreen;
import com.kotcrab.vis.ui.VisUI;


public class CyberRunner extends Game {

    public SpriteBatch batch;
    public ResourceManager resourceManager;
    public SoundManager soundManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        resourceManager = new ResourceManager();
        soundManager = new SoundManager(resourceManager);

        VisUI.load();

        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        resourceManager.dispose();

        VisUI.dispose();
    }
}
