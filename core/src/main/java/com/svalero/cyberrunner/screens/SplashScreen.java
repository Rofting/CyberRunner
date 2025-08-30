package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.svalero.cyberrunner.CyberRunner;

public class SplashScreen implements Screen {

    private final CyberRunner game;
    private Texture splashImage;

    public SplashScreen(CyberRunner game) {
        this.game = game;
    }

    @Override
    public void show() {
        splashImage = new Texture("splash.png");

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new GameScreen(game, "maps/level_01_industrias.tmx"));
            }
        }, 2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(splashImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (splashImage != null) {
            splashImage.dispose();
        }
    }
}
