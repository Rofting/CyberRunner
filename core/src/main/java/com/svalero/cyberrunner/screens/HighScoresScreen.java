package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.svalero.cyberrunner.CyberRunner;
import com.svalero.cyberrunner.managers.ScoreManager;

public class HighScoresScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;

    public HighScoresScreen(CyberRunner game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        root.add(new Label("Top 10 Puntuaciones", VisUI.getSkin())).padBottom(30).row();

        Table table = new Table(VisUI.getSkin());
        table.defaults().pad(5);
        table.add("Pos").left();
        table.add("Nombre").left();
        table.add("Puntos").left().row();

        int pos = 1;
        for (ScoreManager.Entry e : game.scoreManager.getTopScores()) {
            table.add(String.valueOf(pos++)).left();
            table.add(e.name).left();
            table.add(String.valueOf(e.score)).left().row();
        }
        root.add(table).row();

        TextButton back = new TextButton("Volver", VisUI.getSkin());
        back.addListener(c -> { game.setScreen(new MenuScreen(game)); return true; });
        root.add(back).padTop(20);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.08f,0.08f,0.1f,1);
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
