package com.svalero.cyberrunner.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Merchant extends NPC {

    private Player player;
    private boolean playerNearby = false;
    private boolean messageShown = false;

    public Merchant(com.badlogic.gdx.graphics.g2d.TextureAtlas atlas, Player player) {
        super(atlas, "merchant", 0.2f); // usa "merchant" del atlas con 0.2s por frame
        this.player = player;
    }

    @Override
    public void act(float delta) {
        super.stateTime += delta;

        // Detectar si el jugador está cerca horizontalmente
        float distance = Math.abs(getX() - player.getX());
        playerNearby = distance < 80;

        // Si está cerca y presiona ESPACIO, muestra un mensaje (una sola vez)
        if (playerNearby && Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !messageShown) {
            Gdx.app.log("Merchant", "¡Recuerda usar los powerups para escapar más rápido!");
            messageShown = true;
        }
    }
}
