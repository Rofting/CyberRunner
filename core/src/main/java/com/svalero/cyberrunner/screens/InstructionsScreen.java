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

public class InstructionsScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;

    public InstructionsScreen(final CyberRunner game) {
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

        Label titleLabel = new Label("Instrucciones", VisUI.getSkin());

        // --- Controles del Juego ---
        Label moveLabel = new Label("Flechas Izquierda/Derecha - Moverse", VisUI.getSkin());
        Label jumpLabel = new Label("Flecha Arriba - Saltar / Doble Salto", VisUI.getSkin());
        Label attackLabel = new Label("Barra Espaciadora - Atacar", VisUI.getSkin());
        Label interactLabel = new Label("E - Interactuar con personajes", VisUI.getSkin());

        TextButton backButton = new TextButton("Volver al Menu", VisUI.getSkin());

        // --- Añadir Elementos a la Tabla ---
        table.add(titleLabel).padBottom(40).row();
        table.add(moveLabel).left().pad(10).row();
        table.add(jumpLabel).left().pad(10).row();
        table.add(attackLabel).left().pad(10).row();
        table.add(interactLabel).left().pad(10).row();
        table.add(backButton).fillX().padTop(40);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
}
