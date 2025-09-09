package com.svalero.cyberrunner.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreManager {

    private static final String PREFS_NAME = "CyberRunner_Scores";
    private static final int MAX_ENTRIES = 10;

    public static class Entry {
        public final String name;
        public final int score;
        public Entry(String name, int score) { this.name = name; this.score = score; }
    }

    private final Preferences prefs;

    public ScoreManager() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public void addScore(String name, int score) {
        List<Entry> entries = getTopScores();
        entries.add(new Entry(name, score));
        // ordenar desc por puntuación
        entries.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
        if (entries.size() > MAX_ENTRIES) entries = entries.subList(0, MAX_ENTRIES);
        // guardar
        for (int i = 0; i < entries.size(); i++) {
            prefs.putString("name_" + i, entries.get(i).name);
            prefs.putInteger("score_" + i, entries.get(i).score);
        }
        for (int i = entries.size(); i < MAX_ENTRIES; i++) {
            prefs.remove("name_" + i);
            prefs.remove("score_" + i);
        }
        prefs.flush();
    }

    public List<Entry> getTopScores() {
        List<Entry> list = new ArrayList<>();
        for (int i = 0; i < MAX_ENTRIES; i++) {
            String n = prefs.getString("name_" + i, null);
            int s = prefs.getInteger("score_" + i, Integer.MIN_VALUE);
            if (n != null && s != Integer.MIN_VALUE) {
                list.add(new Entry(n, s));
            }
        }
        list.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
        return list;
    }
}
