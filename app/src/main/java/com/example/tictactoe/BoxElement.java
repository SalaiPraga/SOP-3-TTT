package com.example.tictactoe;

public class BoxElement {
    private float x;
    private float y;
    boolean firstPlayerTurn;

    public BoxElement(float x, float y, boolean firstPlayerTurn) {
        this.x = x;
        this.y = y;
        this.firstPlayerTurn = firstPlayerTurn;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isFirstPlayerTurn() {
        return firstPlayerTurn;
    }
}
