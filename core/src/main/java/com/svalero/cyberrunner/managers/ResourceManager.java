package com.svalero.cyberrunner.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ResourceManager {

    public final AssetManager assetManager;

    public ResourceManager() {
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());

        assetManager.load("maps/level_01_industrias.tmx", TiledMap.class);
        assetManager.load("atlas/player.atlas", TextureAtlas.class);
        assetManager.load("atlas/enemies.atlas", TextureAtlas.class);
        assetManager.load("audio/music.mp3", Music.class);
        assetManager.load("audio/jump.wav", Sound.class);
        assetManager.load("audio/coin.wav", Sound.class);

        assetManager.finishLoading();

    }

    public <T> T get(String path, Class<T> type) {
        return assetManager.get(path, type);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
