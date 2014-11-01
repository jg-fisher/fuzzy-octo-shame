package com.jed.state;

public abstract class GameState implements State {

    protected GameStateManager manager;

    public GameState(GameStateManager manager) {
        this.manager = manager;
    }

    @Override
    public void drawChildVertex2f(float x, float y) {
    }
}