package com.svalero.cyberrunner.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private final Music backgroundMusic;
    private final Sound jumpSound;
    private final Sound coinSound;
    private float sfxVolume;

    public SoundManager(ResourceManager resourceManager) {

        this.backgroundMusic = resourceManager.assetManager.get("audio/music.mp3", Music.class);
        this.jumpSound = resourceManager.assetManager.get("audio/jump.wav", Sound.class);
        this.coinSound = resourceManager.assetManager.get("audio/coin.wav", Sound.class);


        Preferences prefs = Gdx.app.getPreferences("CyberRunner_Settings");
        float musicVolume = prefs.getFloat("music_volume", 0.5f);
        this.sfxVolume = prefs.getFloat("sfx_volume", 0.5f);

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(musicVolume);
    }

    public void playMusic() {
        if (!backgroundMusic.isPlaying()) backgroundMusic.play();
    }

    public void playJumpSound() {
        jumpSound.play(sfxVolume);
    }

    public void playCoinSound() {
        coinSound.play(sfxVolume);
    }

    public void setMusicVolume(float volume) {
        backgroundMusic.setVolume(volume);
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
    }
    public void stopMusic() {
        backgroundMusic.stop();
    }
}
