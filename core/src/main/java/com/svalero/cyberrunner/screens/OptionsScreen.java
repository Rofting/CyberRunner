package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.svalero.cyberrunner.CyberRunner;

public class OptionsScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;
    private final Preferences prefs;

    private static final String MUSIC_VOLUME_KEY = "music_volume";
    private static final String SFX_VOLUME_KEY = "sfx_volume";

    public OptionsScreen(final CyberRunner game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.prefs = Gdx.app.getPreferences("CyberRunner_Settings");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        // Cargamos los valores guardados (o los valores por defecto si no existen)
        float musicVolume = prefs.getFloat(MUSIC_VOLUME_KEY, 0.5f);
        float sfxVolume = prefs.getFloat(SFX_VOLUME_KEY, 0.5f);

        // --- Creación de los Elementos de la UI ---
        Label titleLabel = new Label("Opciones", VisUI.getSkin());
        final Slider musicSlider = new Slider(0f, 1f, 0.1f, false, VisUI.getSkin());
        final Slider sfxSlider = new Slider(0f, 1f, 0.1f, false, VisUI.getSkin());
        TextButton backButton = new TextButton("Volver al Menu", VisUI.getSkin());

        // Establecemos la posición inicial de los sliders
        musicSlider.setValue(musicVolume);
        sfxSlider.setValue(sfxVolume);

        // --- Organización en la Tabla ---
        table.add(titleLabel).colspan(2).padBottom(40).row();
        table.add(new Label("Musica:", VisUI.getSkin())).padRight(10);
        table.add(musicSlider).width(300).row();
        table.add(new Label("Efectos:", VisUI.getSkin())).padRight(10).padTop(20);
        table.add(sfxSlider).width(300).padTop(20).row();
        table.add(backButton).colspan(2).fillX().height(50).padTop(40);

        // --- Listeners (Funcionalidad) ---
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = musicSlider.getValue();
                game.soundManager.setMusicVolume(volume);
                prefs.putFloat(MUSIC_VOLUME_KEY, volume);
                prefs.flush();
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = sfxSlider.getValue();
                game.soundManager.setSfxVolume(volume);
                prefs.putFloat(SFX_VOLUME_KEY, volume);
                prefs.flush();
            }
        });

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
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {
        dispose();
    }
}
