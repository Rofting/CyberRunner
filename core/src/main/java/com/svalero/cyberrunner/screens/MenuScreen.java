package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.svalero.cyberrunner.CyberRunner;

public class MenuScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;

    public MenuScreen(CyberRunner game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label titleLabel = new Label("Cyber Runner", VisUI.getSkin());
        TextButton startButton = new TextButton("Iniciar Juego", VisUI.getSkin());
        TextButton optionsButton = new TextButton("Opciones", VisUI.getSkin());
        TextButton instructionsButton = new TextButton("Instrucciones", VisUI.getSkin());
        TextButton scoresButton = new TextButton("Puntuaciones", VisUI.getSkin());
        TextButton exitButton = new TextButton("Salir", VisUI.getSkin());

        table.add(titleLabel).padBottom(40).row();
        table.add(startButton).pad(10).fillX().uniformX().row();
        table.add(optionsButton).pad(10).fillX().uniformX().row();
        table.add(instructionsButton).pad(10).fillX().uniformX().row();
        table.add(scoresButton).pad(10).fillX().uniformX().row();
        table.add(exitButton).pad(10).fillX().uniformX();

        startButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CharacterSelectionScreen(game));
            }
        });
        optionsButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new OptionsScreen(game));
            }
        });
        instructionsButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new InstructionsScreen(game));
            }
        });
        scoresButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HighScoresScreen(game));
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
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

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }
    @Override public void hide() { dispose(); }
    @Override public void pause() {}
    @Override public void resume() {}
}
