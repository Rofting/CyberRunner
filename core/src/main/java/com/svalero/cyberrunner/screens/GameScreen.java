package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.svalero.cyberrunner.CyberRunner;
import com.svalero.cyberrunner.characters.Player;


public class GameScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;
    private final OrthographicCamera camera;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    private final Player player;
    private final Array<Rectangle> collisionObjects;

    public GameScreen(CyberRunner game) {
        this.game = game;

        // --- Configuración de Cámara y Escena (Stage) ---
        camera = new OrthographicCamera();
        FitViewport viewport = new FitViewport(800, 480, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage); // Permite que la escena reciba eventos de input

        // --- Carga de Assets desde ResourceManager ---
        map = game.resourceManager.get("maps/level_01_industrias.tmx", TiledMap.class);
        TextureAtlas atlas = game.resourceManager.get("atlas/game.atlas", TextureAtlas.class);

        // --- Renderizador del Mapa ---
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // --- Carga de Objetos de Colisión del Mapa ---
        collisionObjects = new Array<>();
        MapLayer collisionLayer = map.getLayers().get("Collisions");
        if (collisionLayer != null) {
            for (RectangleMapObject obj : collisionLayer.getObjects().getByType(RectangleMapObject.class)) {
                collisionObjects.add(obj.getRectangle());
            }
        }
        System.out.println("Colisiones cargadas: " + collisionObjects.size);

        //String characterToLoad = "player/Rex";       // Opción 1: Controlar a Rex
        // String characterToLoad = "player/Kael/Biker";    // Opción 2: Controlar a Kael
        // String characterToLoad = "player/Anya/Punk";     // Opción 3: Controlar a Anya


        player = new Player(collisionObjects);
        player.setPosition(100, 150);
        stage.addActor(player);
    }

    @Override
    public void render(float delta) {
        // Limpia la pantalla con un color de fondo oscuro
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = player.getX();
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();


        stage.act(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        map.dispose();
        stage.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
