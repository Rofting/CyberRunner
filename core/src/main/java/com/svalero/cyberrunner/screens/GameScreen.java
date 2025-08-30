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
    private final Drone testDrone;
    private Coin testCoin;
    private final Turret testTurret;
    private final Merchant testMerchant;
    private final Array<Rectangle> collisionObjects;

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

        collisionObjects = new Array<>();
        MapLayer collisionLayer = map.getLayers().get("Collisions");
        if (collisionLayer != null) {
            for (RectangleMapObject obj : collisionLayer.getObjects().getByType(RectangleMapObject.class)) {
                collisionObjects.add(obj.getRectangle());
            }
        }

        String characterToLoad = "Rex";
        player = new Player(characterToLoad, this, soundManager, collisionObjects);
        player.setPosition(100, 150);
        stage.addActor(player);

        testDrone = new Drone(enemyAtlas, 300, 150);
        stage.addActor(testDrone);

        testCoin = new Coin(200, 100);
        stage.addActor(testCoin);

        testTurret = new Turret(enemyAtlas, soundManager, 400, 400);
        stage.addActor(testTurret);

        testMerchant = new Merchant(enemyAtlas, 500, 200);
        stage.addActor(testMerchant);

        hudStage = new Stage(new ScreenViewport());
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        score = 0;
        scoreLabel = new Label("PUNTOS: " + score, labelStyle);
        levelLabel = new Label("NIVEL: 1", labelStyle);
        energyLabel = new Label("ENERGIA: 100", labelStyle);
        messageLabel = new Label("", labelStyle);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.pad(10);

        table.add(scoreLabel).left();
        table.row();
        table.add(levelLabel).left();
        table.row();
        table.add(energyLabel).left();
        table.row();
        table.add(messageLabel).left().padTop(20);

        hudStage.addActor(table);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.x = player.getX();
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
        if (testCoin != null && player.getBounds().overlaps(testCoin.bounds)) {
            addScore(10);
            soundManager.playCoinSound();
            testCoin.remove();
            testCoin.dispose();
            testCoin = null;
        }

        if (testDrone != null && player.getBounds().overlaps(testDrone.getBounds())) {
            player.takeDamage(10);
            updateEnergyLabel(player.getEnergy());
        }

        for (Actor actor : stage.getActors()) {
            if (actor instanceof Bullet) {
                Bullet bullet = (Bullet) actor;
                if (player.getBounds().overlaps(bullet.getBounds())) {
                    player.takeDamage(5);
                    updateEnergyLabel(player.getEnergy());
                    bullet.remove();
                    bullet.dispose();
                }
            }
        }
    }

    private void handleMerchantInteraction() {
        float distance = Math.abs(player.getX() - testMerchant.getX());
        if (distance < 50) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E) && !isInConversation) {
                isInConversation = true;
            }
            if (isInConversation) {
                messageLabel.setText("Cuidado con las torretas...");
            } else {
                messageLabel.setText("[E] para hablar");
            }
        } else {
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
        System.out.println("GAME OVER");
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
        if (testCoin != null) {
            testCoin.dispose();
        }
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
