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

public class CharacterSelectionScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;

    public CharacterSelectionScreen(final CyberRunner game) {
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

        Label titleLabel = new Label("Elige tu Personaje", VisUI.getSkin());

        TextButton rexButton = new TextButton("Rex (Cyborg)", VisUI.getSkin());
        TextButton bikerButton = new TextButton("Kael (Biker)", VisUI.getSkin());
        TextButton punkButton = new TextButton("Anya (Punk)", VisUI.getSkin());

        table.add(titleLabel).padBottom(40).row();
        table.add(rexButton).pad(10).fillX().uniformX().row();
        table.add(bikerButton).pad(10).fillX().uniformX().row();
        table.add(punkButton).pad(10).fillX().uniformX().row();

        // --- Listeners para cada personaje ---

        rexButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGameWithCharacter("Rex");
            }
        });

        bikerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGameWithCharacter("Biker");
            }
        });

        punkButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGameWithCharacter("Punk");
            }
        });
    }

    private void startGameWithCharacter(String characterName) {
        System.out.println("Personaje seleccionado: " + characterName);
        game.setScreen(new GameScreen(game, "maps/level_01_industrias.tmx", characterName, 0, 100));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() { stage.dispose(); }
}
