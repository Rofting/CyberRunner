package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.svalero.cyberrunner.CyberRunner;

public class SplashScreen implements Screen {

    private final CyberRunner game;
    private Texture splashImage;

    public SplashScreen(CyberRunner game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Carga una imagen temporal para mostrar en pantalla
        splashImage = new Texture("splash.png");

        // Cargamos recursos en segundo plano con un pequeño retardo
        Timer.schedule(new Task() {
            @Override
            public void run() {
                game.setScreen(new MenuScreen(game)); // Cuando termina, pasamos al menú
            }
        }, 2); // espera 2 segundos
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla con un color de fondo
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar la imagen
        game.batch.begin();
        game.batch.draw(splashImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        splashImage.dispose();
    }
}
