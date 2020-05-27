package com.example.tictactoe;

public class PlayerStatictics {
    private String username;
    private int winCount = 0;
    private int lossCount = 0;
    private int drawCount = 0;

    public PlayerStatictics(){

    }

    public PlayerStatictics(String username, int winCount, int lossCount, int drawCount) {
        this.username = username;
        this.winCount = winCount;
        this.lossCount = lossCount;
        this.drawCount = drawCount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public void setLossCount(int lossCount) {
        this.lossCount = lossCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public String getUsername() {
        return username;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLossCount() {
        return lossCount;
    }

    public int getDrawCount() {
        return drawCount;
    }
}
