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
        // Carga una imagen simple para mostrar.
        // Este archivo debe estar en tu carpeta `assets`.
        splashImage = new Texture("splash.png");

        // Programa una tarea para cambiar de pantalla después de 2 segundos.
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new GameScreen(game));
            }
        }, 2);
    }

    @Override
    public void render(float delta) {
        // Limpia la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibuja la imagen de splash a pantalla completa
        game.batch.begin();
        game.batch.draw(splashImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // No es necesario gestionar el re-escalado aquí
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // Libera la imagen cuando la pantalla ya no se vea
        dispose();
    }

    @Override
    public void dispose() {
        if (splashImage != null) {
            splashImage.dispose();
        }
    }
}
