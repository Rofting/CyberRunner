package com.svalero.cyberrunner.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private final Music backgroundMusic;
    private final Sound jumpSound;
    private final Sound coinSound;


    public SoundManager(ResourceManager resourceManager) {
        this.backgroundMusic = resourceManager.assetManager.get("audio/music.mp3", Music.class);
        this.jumpSound = resourceManager.assetManager.get("audio/jump.wav", Sound.class);
        this.coinSound = resourceManager.assetManager.get("audio/coin.wav", Sound.class);


        this.backgroundMusic.setLooping(true);
        this.backgroundMusic.setVolume(0.5f);
    }

    public void playMusic() {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void stopMusic() {
        backgroundMusic.stop();
    }


    public void playJumpSound() {
        jumpSound.play();
    }

    public void playCoinSound() {
        coinSound.play();
    }
}
