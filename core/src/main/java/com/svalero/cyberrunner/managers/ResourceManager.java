package com.svalero.cyberrunner.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ResourceManager {

    public final AssetManager assetManager;

    public ResourceManager() {
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());

        assetManager.load("maps/level_01_industrias.tmx", TiledMap.class);
        assetManager.load("maps/level_02_seaport.tmx", TiledMap.class);
        assetManager.load("atlas/player.atlas", TextureAtlas.class);
        assetManager.load("atlas/enemies.atlas", TextureAtlas.class);
        assetManager.load("atlas/boss.atlas", TextureAtlas.class);
        assetManager.load("audio/music.mp3", Music.class);
        assetManager.load("audio/jump.wav", Sound.class);
        assetManager.load("audio/coin.wav", Sound.class);
        assetManager.load("items/coin.png", Texture.class);

        assetManager.finishLoading();

        System.out.println("\n\n--- INICIO DEL CONTENIDO DE enemies.atlas ---");
        TextureAtlas enemyAtlas = assetManager.get("atlas/enemies.atlas", TextureAtlas.class);
        for (TextureAtlas.AtlasRegion region : enemyAtlas.getRegions()) {
            System.out.println("Region de Enemigo encontrada: '" + region.name + "'");
        }
        System.out.println("--- FIN DEL CONTENIDO DE enemies.atlas ---\n\n");
    }

    public <T> T get(String path, Class<T> type) {
        return assetManager.get(path, type);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
