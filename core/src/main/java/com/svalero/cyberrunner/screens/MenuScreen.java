package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.svalero.cyberrunner.CyberRunner;

public class MenuScreen implements Screen {

    private final CyberRunner game;
    private Stage stage;

    public MenuScreen(CyberRunner game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        VisTable table = new VisTable();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Cyber Runner", VisUI.getSkin(), "title");

        VisTextButton startButton = new VisTextButton("Iniciar Juego");
        VisTextButton exitButton = new VisTextButton("Salir");

        table.add(titleLabel).padBottom(40).row();
        table.add(startButton).pad(10).row();
        table.add(exitButton).pad(10);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Aquí luego cambiarás a GameScreen
                Gdx.app.log("Menu", "Iniciar juego (falta GameScreen)");
                // game.setScreen(new GameScreen(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }
}
