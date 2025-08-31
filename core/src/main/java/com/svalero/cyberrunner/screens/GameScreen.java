package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.svalero.cyberrunner.CyberRunner;
import com.svalero.cyberrunner.characters.Drone;
import com.svalero.cyberrunner.characters.Merchant;
import com.svalero.cyberrunner.characters.Player;
import com.svalero.cyberrunner.characters.Turret;
import com.svalero.cyberrunner.managers.SoundManager;
import com.svalero.cyberrunner.objects.Bullet;
import com.svalero.cyberrunner.objects.Coin;

public class GameScreen implements Screen {

    private final CyberRunner game;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    private final Player player;
    private final Array<Rectangle> collisionObjects;
    private Rectangle exitPortal;
    private Rectangle victoryPortal;

    private final Stage hudStage;
    private final Label scoreLabel;
    private final Label levelLabel;
    private final Label energyLabel;
    private final Label messageLabel;
    private int score;
    private boolean isInConversation = false;

    private final SoundManager soundManager;

    public GameScreen(CyberRunner game, String mapFile) {
        this.game = game;
        this.soundManager = game.soundManager;

        camera = new OrthographicCamera();
        FitViewport viewport = new FitViewport(800, 480, camera);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        map = game.resourceManager.get(mapFile, TiledMap.class);
        TextureAtlas playerAtlas = game.resourceManager.get("atlas/player.atlas", TextureAtlas.class);
        TextureAtlas enemyAtlas = game.resourceManager.get("atlas/enemies.atlas", TextureAtlas.class);

        mapRenderer = new OrthogonalTiledMapRenderer(map);
        soundManager.playMusic();

        // --- Carga de Colisiones ---
        collisionObjects = new Array<>();
        MapLayer collisionLayer = map.getLayers().get("Collisions");
        if (collisionLayer != null) {
            for (RectangleMapObject obj : collisionLayer.getObjects().getByType(RectangleMapObject.class)) {
                collisionObjects.add(obj.getRectangle());
            }
        }

        // --- Carga de Triggers (Portales) ---
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        if (triggerLayer != null) {
            for (RectangleMapObject obj : triggerLayer.getObjects().getByType(RectangleMapObject.class)) {
                if ("exit_portal".equals(obj.getName())) {
                    this.exitPortal = obj.getRectangle();
                } else if ("victory_portal".equals(obj.getName())) {
                    this.victoryPortal = obj.getRectangle();
                }
            }
        }
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int tileWidthInPixels = map.getProperties().get("tilewidth", Integer.class);
        float mapWidthInPixels = mapWidthInTiles * tileWidthInPixels;

        // --- Creación del Jugador ---
        String characterToLoad = "Rex";
        player = new Player(characterToLoad, this, soundManager, collisionObjects, mapWidthInPixels);
        player.setPosition(100, 150);
        stage.addActor(player);

        // --- Carga de ENEMIGOS y NPCS desde el MAPA ---
        MapLayer characterLayer = map.getLayers().get("Characters");
        if (characterLayer != null) {
            for (RectangleMapObject obj : characterLayer.getObjects().getByType(RectangleMapObject.class)) {
                String type = obj.getName();
                float x = obj.getRectangle().x;
                float y = obj.getRectangle().y;

                if ("Drone".equals(type)) {
                    stage.addActor(new Drone(enemyAtlas, collisionObjects, x, y));
                } else if ("Turret".equals(type)) {
                    stage.addActor(new Turret(enemyAtlas, soundManager, x, y));
                } else if ("Merchant".equals(type)) {
                    stage.addActor(new Merchant(enemyAtlas, x, y));
                } else if ("Coin".equals(type)) {
                    stage.addActor(new Coin(x, y));
                }
            }
        }

        // --- Inicialización del HUD ---
        hudStage = new Stage(new ScreenViewport());
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        score = 0;
        scoreLabel = new Label("PUNTOS: " + score, labelStyle);
        levelLabel = new Label("NIVEL: 1", labelStyle);
        energyLabel = new Label("ENERGIA: " + player.getEnergy(), labelStyle);
        messageLabel = new Label("", labelStyle);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.pad(10);

        table.add(scoreLabel).left().row();
        table.add(levelLabel).left().row();
        table.add(energyLabel).left().row();
        table.add(messageLabel).left().padTop(20);

        hudStage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = player.getX();

        float cameraHalfWidth = camera.viewportWidth / 2;
        float mapWidthInPixels  = 100 * 32;

        if (camera.position.x < cameraHalfWidth) {
            camera.position.x = cameraHalfWidth;
        }
        if (camera.position.x > mapWidthInPixels - cameraHalfWidth) {
            camera.position.x = mapWidthInPixels - cameraHalfWidth;
        }

        camera.update();

        stage.act(delta);
        checkCollisions();
        handleMerchantInteraction();

        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.draw();
        hudStage.draw();
    }

    private void checkCollisions() {
        // Recorremos todos los actores de la escena para comprobar colisiones
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Coin) {
                Coin coin = (Coin) actor;
                if (player.getBounds().overlaps(coin.bounds)) {
                    addScore(10);
                    soundManager.playCoinSound();
                    actor.remove();
                    coin.dispose();
                }
            }
            if (actor instanceof Drone) {
                Drone drone = (Drone) actor;
                if (player.getBounds().overlaps(drone.getBounds())) {
                    player.takeDamage(10);
                    updateEnergyLabel(player.getEnergy());
                }
            }
            if (actor instanceof Bullet) {
                Bullet bullet = (Bullet) actor;
                if (player.getBounds().overlaps(bullet.getBounds())) {
                    player.takeDamage(5);
                    updateEnergyLabel(player.getEnergy());
                    actor.remove();
                    bullet.dispose();
                }
            }
        }

        // Comprobación de portales
        if (exitPortal != null && player.getBounds().overlaps(exitPortal)) {
            game.setScreen(new GameScreen(game, "maps/level_02_seaport.tmx"));
        }
        if (victoryPortal != null && player.getBounds().overlaps(victoryPortal)) {
            game.setScreen(new YouWinScreen(game));
        }
    }

    private void handleMerchantInteraction() {
        boolean merchantFound = false;
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Merchant) {
                float distance = Math.abs(player.getX() - actor.getX());
                if (distance < 50) {
                    merchantFound = true;
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E) && !isInConversation) {
                        isInConversation = true;
                    }
                    if (isInConversation) {
                        messageLabel.setText("Cuidado con las torretas...");
                    } else {
                        messageLabel.setText("[E] para hablar");
                    }
                }
            }
        }
        if (!merchantFound) {
            isInConversation = false;
            messageLabel.setText("");
        }
    }

    public void addScore(int points) {
        this.score += points;
        scoreLabel.setText("PUNTOS: " + score);
    }

    public void updateEnergyLabel(int energy) {
        energyLabel.setText("ENERGIA: " + energy);
    }

    public void gameOver() {
        soundManager.stopMusic();
        game.setScreen(new GameOverScreen(game));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        hudStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        map.dispose();
        stage.dispose();
        hudStage.dispose();
    }

    public CyberRunner getGame() {
        return game;
    }

    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}
