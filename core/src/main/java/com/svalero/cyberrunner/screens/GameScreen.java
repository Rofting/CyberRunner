package com.svalero.cyberrunner.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.svalero.cyberrunner.CyberRunner;
import com.svalero.cyberrunner.characters.Drone;
import com.svalero.cyberrunner.characters.Merchant;
import com.svalero.cyberrunner.characters.NexusBoss;
import com.svalero.cyberrunner.characters.Player;
import com.svalero.cyberrunner.characters.Turret;
import com.svalero.cyberrunner.managers.SoundManager;
import com.svalero.cyberrunner.objects.Bullet;
import com.svalero.cyberrunner.objects.Coin;
import com.svalero.cyberrunner.objects.powerups.HealPowerUp;
import com.svalero.cyberrunner.objects.powerups.ShieldPowerUp;
import com.svalero.cyberrunner.objects.powerups.SpeedPowerUp;

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

    // --- Mercader / Tienda ---
    private VisWindow merchantDialog;
    private Label shopMsgLabel;
    private static final int COST_HEAL = 20;
    private static final int COST_SPEED = 30;
    private static final int COST_SHIELD = 40;

    private int score;
    private final SoundManager soundManager;
    private final String characterName;

    // --- Pausa ---
    private boolean paused = false;
    private VisWindow pauseMenu;
    private InputMultiplexer multiplexer;
    private InputAdapter keyCatcher;


    public GameScreen(CyberRunner game, String mapFile, String characterName, int startingScore, int startingEnergy) {
        this(game, mapFile, characterName, startingScore, startingEnergy, "PlayerStart");
    }


    public GameScreen(CyberRunner game, String mapFile, String characterName, int startingScore, int startingEnergy, String spawnName) {
        this.game = game;
        this.soundManager = game.soundManager;
        this.characterName = characterName;

        camera = new OrthographicCamera();
        FitViewport viewport = new FitViewport(1600, 800, camera);
        stage = new Stage(viewport, game.batch);


        hudStage = new Stage(new ScreenViewport());


        keyCatcher = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (merchantDialog != null && merchantDialog.isVisible()) {
                        merchantDialog.setVisible(false);
                        setPaused(false);
                        return true;
                    }
                    setPaused(!paused);
                    return true;
                }
                return false;
            }
        };
        multiplexer = new InputMultiplexer(keyCatcher, hudStage, stage);
        Gdx.input.setInputProcessor(multiplexer);

        // Carga mapa/atlas
        map = game.resourceManager.get(mapFile, TiledMap.class);
        TextureAtlas enemyAtlas = game.resourceManager.get("atlas/enemies.atlas", TextureAtlas.class);
        TextureAtlas bossAtlas = game.resourceManager.get("atlas/boss.atlas", TextureAtlas.class);

        mapRenderer = new OrthogonalTiledMapRenderer(map);
        soundManager.playMusic();

        // Colisiones
        collisionObjects = new Array<>();
        MapLayer collisionLayer = map.getLayers().get("Collisions");
        if (collisionLayer != null) {
            for (RectangleMapObject obj : collisionLayer.getObjects().getByType(RectangleMapObject.class)) {
                collisionObjects.add(obj.getRectangle());
            }
        }

        // portales
        MapLayer triggerLayer = map.getLayers().get("Triggers");
        if (triggerLayer != null) {
            for (RectangleMapObject obj : triggerLayer.getObjects().getByType(RectangleMapObject.class)) {
                if ("exit_portal".equals(obj.getName())) this.exitPortal = obj.getRectangle();
                else if ("victory_portal".equals(obj.getName())) this.victoryPortal = obj.getRectangle();
            }
        }

        // Ancho del mapa
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int tileWidthInPixels = map.getProperties().get("tilewidth", Integer.class);
        float mapWidthInPixels = mapWidthInTiles * tileWidthInPixels;


        float[] spawn = resolveSpawn(spawnName);
        player = new Player(characterName, this, soundManager, collisionObjects, mapWidthInPixels);
        player.setPosition(spawn[0], spawn[1]);
        stage.addActor(player);


        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();


        MapLayer characterLayer = map.getLayers().get("Characters");
        if (characterLayer != null) {
            for (MapObject mo : characterLayer.getObjects()) {
                String tag = mo.getName();
                if (tag == null || tag.isEmpty()) tag = mo.getProperties().get("type", String.class);
                if (tag == null || tag.isEmpty()) tag = mo.getProperties().get("class", String.class);
                if (tag == null) tag = "";

                float x, y;
                if (mo instanceof RectangleMapObject) {
                    RectangleMapObject r = (RectangleMapObject) mo;
                    x = r.getRectangle().x;
                    y = r.getRectangle().y;
                } else if (mo instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject t = (TiledMapTileMapObject) mo;
                    x = t.getX();
                    y = t.getY();
                } else {
                    continue;
                }

                switch (tag) {
                    case "Drone":
                        stage.addActor(new Drone(enemyAtlas, collisionObjects, x, y));
                        break;
                    case "Turret":
                        stage.addActor(new Turret(enemyAtlas, soundManager, x, y));
                        break;
                    case "Merchant":
                    case "Mercader":
                        stage.addActor(new Merchant(enemyAtlas, x, y));
                        break;
                    case "Nexus":
                        stage.addActor(new NexusBoss(this, bossAtlas, x, y));
                        break;
                    case "Coin":
                        stage.addActor(new Coin(enemyAtlas, x, y));
                        break;
                    default:
                        break;
                }
            }
        }

        MapLayer powerLayer = map.getLayers().get("PowerUps");
        if (powerLayer != null) {
            for (MapObject mo : powerLayer.getObjects()) {
                String tag = mo.getName();
                if (tag == null || tag.isEmpty()) tag = mo.getProperties().get("type", String.class);
                if (tag == null || tag.isEmpty()) tag = mo.getProperties().get("class", String.class);
                if (tag == null) tag = "";

                float x, y;
                if (mo instanceof RectangleMapObject) {
                    RectangleMapObject r = (RectangleMapObject) mo;
                    x = r.getRectangle().x;
                    y = r.getRectangle().y;
                } else if (mo instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject t = (TiledMapTileMapObject) mo;
                    x = t.getX();
                    y = t.getY();
                } else continue;

                if ("Heal".equalsIgnoreCase(tag)) stage.addActor(new HealPowerUp(enemyAtlas, x, y, 30));
                else if ("Speed".equalsIgnoreCase(tag)) stage.addActor(new SpeedPowerUp(enemyAtlas, x, y, 1.5f, 6f));
                else if ("Shield".equalsIgnoreCase(tag)) stage.addActor(new ShieldPowerUp(enemyAtlas, x, y, 5f));
            }
        }


        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        this.score = startingScore;
        player.setEnergy(startingEnergy);

        scoreLabel = new Label("PUNTOS: " + this.score, labelStyle);
        levelLabel = new Label("NIVEL: " + (mapFile.contains("01") ? "1" : "2"), labelStyle);
        energyLabel = new Label("ENERGIA: " + player.getEnergy(), labelStyle);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.pad(10);
        table.add(scoreLabel).left().row();
        table.add(levelLabel).left().row();
        table.add(energyLabel).left().row();
        hudStage.addActor(table);


        buildMerchantShop();

        buildPauseMenu();
    }

    private float[] resolveSpawn(String spawnName) {
        float sx = 100f, sy = 150f; // fallback
        MapLayer spawnLayer = map.getLayers().get("Spawns");
        RectangleMapObject pick = null;
        if (spawnLayer != null) {
            for (RectangleMapObject obj : spawnLayer.getObjects().getByType(RectangleMapObject.class)) {
                if (spawnName.equals(obj.getName())) { pick = obj; break; }
            }
            if (pick == null) {
                for (RectangleMapObject obj : spawnLayer.getObjects().getByType(RectangleMapObject.class)) {
                    if ("PlayerStart".equals(obj.getName())) { pick = obj; break; }
                }
            }
            if (pick == null) {
                Array<RectangleMapObject> all = spawnLayer.getObjects().getByType(RectangleMapObject.class);
                if (all.size > 0) pick = all.first();
            }
            if (pick != null) {
                sx = pick.getRectangle().x;
                sy = pick.getRectangle().y;
            }
        }
        return new float[]{sx, sy};
    }

    // ---------- TIENDA DEL MERCADER ----------
    private void buildMerchantShop() {
        merchantDialog = new VisWindow("Mercader");
        merchantDialog.setModal(true);
        merchantDialog.setMovable(false);

        Table content = new Table();
        content.defaults().pad(6);

        content.add(new Label("Bienvenido, viajero.\n¿Deseas mejorar tus capacidades?", VisUI.getSkin()))
            .colspan(2).padBottom(10).row();

        TextButton healBtn = new TextButton("Curación +30", VisUI.getSkin());
        Label healCost = new Label(COST_HEAL + " pts", VisUI.getSkin());
        content.add(healBtn).left();
        content.add(healCost).right().row();

        TextButton speedBtn = new TextButton("Velocidad x1.5 (6s)", VisUI.getSkin());
        Label speedCost = new Label(COST_SPEED + " pts", VisUI.getSkin());
        content.add(speedBtn).left();
        content.add(speedCost).right().row();

        TextButton shieldBtn = new TextButton("Escudo (5s)", VisUI.getSkin());
        Label shieldCost = new Label(COST_SHIELD + " pts", VisUI.getSkin());
        content.add(shieldBtn).left();
        content.add(shieldCost).right().row();

        shopMsgLabel = new Label("", VisUI.getSkin());
        content.add(shopMsgLabel).colspan(2).padTop(8).row();

        TextButton closeBtn = new TextButton("Cerrar", VisUI.getSkin());
        content.add(closeBtn).colspan(2).fillX().padTop(10);

        merchantDialog.add(content).pad(10);
        merchantDialog.pack();
        merchantDialog.setVisible(false);
        merchantDialog.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
        hudStage.addActor(merchantDialog);

        healBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (spendScore(COST_HEAL)) { player.heal(30); shopMsg("¡Curación aplicada! (-" + COST_HEAL + ")"); }
                else shopMsg("Puntos insuficientes.");
            }
        });
        speedBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (spendScore(COST_SPEED)) { player.activateSpeedBoost(1.5f, 6f); shopMsg("¡Velocidad mejorada! (-" + COST_SPEED + ")"); }
                else shopMsg("Puntos insuficientes.");
            }
        });
        shieldBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (spendScore(COST_SHIELD)) { player.activateShield(5f); shopMsg("¡Escudo activado! (-" + COST_SHIELD + ")"); }
                else shopMsg("Puntos insuficientes.");
            }
        });
        closeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                merchantDialog.setVisible(false);
                setPaused(false);
            }
        });
    }

    private void shopMsg(String msg) { shopMsgLabel.setText(msg); }
    private boolean spendScore(int cost) {
        if (score >= cost) {
            score -= cost;
            scoreLabel.setText("PUNTOS: " + score);
            soundManager.playCoinSound();
            return true;
        }
        return false;
    }

    private void buildPauseMenu() {
        pauseMenu = new VisWindow("Pausa");
        pauseMenu.setModal(true);
        pauseMenu.setMovable(false);

        final CheckBox muteBox = new CheckBox(" Silencio música", VisUI.getSkin());
        TextButton continueBtn = new TextButton("Continuar", VisUI.getSkin());
        TextButton mainMenuBtn = new TextButton("Volver al Menú", VisUI.getSkin());
        TextButton exitBtn = new TextButton("Salir del juego", VisUI.getSkin());

        pauseMenu.add(muteBox).left().row();
        pauseMenu.add(continueBtn).padTop(10).fillX().row();
        pauseMenu.add(mainMenuBtn).padTop(10).fillX().row();
        pauseMenu.add(exitBtn).padTop(10).fillX().row();
        pauseMenu.pack();
        pauseMenu.setVisible(false);
        pauseMenu.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, Align.center);
        hudStage.addActor(pauseMenu);

        muteBox.addListener(e -> { game.soundManager.setMusicVolume(muteBox.isChecked() ? 0f : 0.5f); return true; });
        continueBtn.addListener(e -> { setPaused(false); return true; });
        mainMenuBtn.addListener(e -> { game.setScreen(new MenuScreen(game)); return true; });
        exitBtn.addListener(e -> { Gdx.app.exit(); return true; });
    }

    private void setPaused(boolean value) {
        paused = value;
        pauseMenu.setVisible(paused);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cámara sigue al jugador con límites
        Vector3 targetPosition = new Vector3(player.getX(), player.getY(), 0);
        camera.position.lerp(targetPosition, 0.1f);

        float cameraHalfWidth = camera.viewportWidth / 2f;
        float cameraHalfHeight = camera.viewportHeight / 2f;
        float mapWidthInPixels  = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeightInPixels = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

        if (camera.position.x < cameraHalfWidth) camera.position.x = cameraHalfWidth;
        if (camera.position.x > mapWidthInPixels - cameraHalfWidth) camera.position.x = mapWidthInPixels - cameraHalfWidth;
        if (camera.position.y < cameraHalfHeight) camera.position.y = cameraHalfHeight;
        if (camera.position.y > mapHeightInPixels - cameraHalfHeight) camera.position.y = mapHeightInPixels - cameraHalfHeight;

        camera.update();

        // Act
        stage.act(paused ? 0 : delta);
        hudStage.act(delta);

        if (!paused) {
            checkCollisions();
            handleMerchantInteraction();
        }

        // Draw
        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.draw();
        hudStage.draw();
    }

    private void checkCollisions() {
        Array<Actor> actorsCopy = new Array<>(stage.getActors());
        for (Actor actor : actorsCopy) {
            if (actor instanceof Coin) {
                Coin coin = (Coin) actor;
                if (player.getBounds().overlaps(coin.bounds)) {
                    addScore(10);
                    soundManager.playCoinSound();
                    coin.remove();
                }
            } else if (actor instanceof Drone) {
                Drone drone = (Drone) actor;
                if (player.getBounds().overlaps(drone.getBounds())) {
                    player.takeDamage(10);
                    updateEnergyLabel(player.getEnergy());
                }
            } else if (actor instanceof Bullet) {
                Bullet bullet = (Bullet) actor;
                if (player.getBounds().overlaps(bullet.getBounds())) {
                    player.takeDamage(5);
                    updateEnergyLabel(player.getEnergy());
                    bullet.remove();
                    bullet.dispose();
                }
            }
        }

        // Portales
        if (exitPortal != null && player.getBounds().overlaps(exitPortal)) {
            game.setScreen(new GameScreen(game, "maps/level_02_seaport.tmx",
                player.getCharacterName(), score, player.getEnergy(), "PlayerStart"));
        }
        if (victoryPortal != null && player.getBounds().overlaps(victoryPortal)) {
            winGame();
        }
    }

    private void handleMerchantInteraction() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Merchant) {
                float dx = player.getX() - actor.getX();
                float dy = player.getY() - actor.getY();
                float distance = (float) Math.hypot(dx, dy);
                if (distance < 60f && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    merchantDialog.setVisible(true);
                    shopMsg("");
                    setPaused(true);
                }
            }
        }
    }


    public void damageBoss(int amount, Rectangle attackHitbox) {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof NexusBoss) {
                NexusBoss boss = (NexusBoss) actor;
                if (attackHitbox.overlaps(boss.getBounds())) {
                    boss.takeDamage(amount);
                }
            }
        }
    }

    @Deprecated
    public void damageBoss(int amount) { }

    public void addScore(int points) {
        this.score += points;
        scoreLabel.setText("PUNTOS: " + score);
    }

    public void spawnDrone(float x, float y) {
        TextureAtlas enemyAtlas = game.resourceManager.get("atlas/enemies.atlas", TextureAtlas.class);
        Drone drone = new Drone(enemyAtlas, collisionObjects, x, y);
        stage.addActor(drone);
    }

    public void updateEnergyLabel(int energy) {
        energyLabel.setText("ENERGIA: " + energy);
    }

    public void gameOver() {
        soundManager.stopMusic();
        game.setScreen(new GameOverScreen(game, score));
    }

    public void winGame() {
        soundManager.stopMusic();
        game.setScreen(new YouWinScreen(game, score));
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        hudStage.getViewport().update(width, height, true);
    }

    @Override public void dispose() {
        map.dispose();
        stage.dispose();
        hudStage.dispose();
    }

    public CyberRunner getGame() { return game; }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
