package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.svalero.cyberrunner.CyberRunner;

public class GameOverScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;
    private final int finalScore;

    public GameOverScreen(final CyberRunner game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label gameOverLabel = new Label("GAME OVER", VisUI.getSkin());
        Label scoreLabel = new Label("Puntuación: " + finalScore, VisUI.getSkin());

        final TextField nameField = new TextField("Jugador", VisUI.getSkin());
        TextButton saveButton = new TextButton("Guardar Puntuación", VisUI.getSkin());

        TextButton playAgainButton = new TextButton("Volver a Jugar", VisUI.getSkin());
        TextButton exitButton = new TextButton("Salir al Menu", VisUI.getSkin());

        table.add(gameOverLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(20).row();
        table.add(new Label("Tu nombre:", VisUI.getSkin())).padBottom(10).row();
        table.add(nameField).width(300).padBottom(10).row();
        table.add(saveButton).padBottom(30).row();
        table.add(playAgainButton).pad(10).fillX().uniformX().row();
        table.add(exitButton).pad(10).fillX().uniformX();
        stage.addActor(table);

        saveButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) name = "Jugador";
                game.scoreManager.addScore(name, finalScore);
                saveButton.setDisabled(true);
            }
        });

        playAgainButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CharacterSelectionScreen(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() { stage.dispose(); }
}

