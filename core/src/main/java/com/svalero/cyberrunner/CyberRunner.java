package com.svalero.cyberrunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.svalero.cyberrunner.managers.ResourceManager;
import com.svalero.cyberrunner.screens.SplashScreen;


public class CyberRunner extends Game {

    public SpriteBatch batch;
    public ResourceManager resourceManager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        resourceManager = new ResourceManager();

        this.setScreen(new SplashScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        resourceManager.dispose();
    }
}
