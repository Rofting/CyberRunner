package com.svalero.cyberrunner.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ResourceManager {

    public AssetManager assetManager;

    public ResourceManager() {
        assetManager = new AssetManager();

        // Para cargar mapas Tiled (.tmx)
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());

        // Cargar mapas
        assetManager.load("maps/level1.tmx", TiledMap.class);

        // Cargar sprites individuales (temporal, luego usar atlas)
        assetManager.load("characters/player.png", Texture.class);

        // Cargar atlas de texturas (si usas uno)
        assetManager.load("textures/game.atlas", TextureAtlas.class); // Opcional

        // Música y efectos de sonido
        assetManager.load("audio/music/theme.mp3", Music.class);
        assetManager.load("audio/sfx/jump.wav", Sound.class);
        assetManager.load("audio/sfx/explosion.wav", Sound.class);

        // Esperar a que cargue todo (bloqueante, ideal para SplashScreen)
        assetManager.finishLoading();
    }

    // Método genérico para obtener cualquier recurso
    public <T> T get(String path, Class<T> type) {
        return assetManager.get(path, type);
    }

    // Liberar recursos cuando ya no se necesitan
    public void dispose() {
        assetManager.dispose();
    }
}
