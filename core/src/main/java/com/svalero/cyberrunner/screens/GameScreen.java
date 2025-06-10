package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.svalero.cyberrunner.CyberRunner;
import com.svalero.cyberrunner.characters.Player;

public class GameScreen implements Screen {

    private final CyberRunner game;
    private OrthographicCamera camera;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Stage stage;
    private Player player;

    public GameScreen(CyberRunner game) {
        this.game = game;

        // Configurar cámara y stage
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // resolución virtual
        stage = new Stage(new FitViewport(800, 480, camera), game.batch);

        // Cargar el mapa y el renderer
        map = game.resourceManager.get("maps/level1.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);

        // Cargar atlas de texturas y crear jugador
        TextureAtlas atlas = game.resourceManager.get("textures/game.atlas", TextureAtlas.class);
        player = new Player(atlas);
        player.setPosition(100, 100); // posición inicial

        stage.addActor(player);
    }

    @Override
    public void render(float delta) {
        // Actualizar lógica
        stage.act(delta);
        camera.update();

        // Limpiar la pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Renderizar mapa
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Dibujar jugadores y elementos del stage
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        stage.dispose();
    }
}
