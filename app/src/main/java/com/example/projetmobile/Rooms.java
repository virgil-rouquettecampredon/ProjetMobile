package com.example.projetmobile;

public class Rooms {
    private String player1;
    private String player2;
    private String gameMode;
    private String gameName;
    private long ranking;
    private long turn;
    private String piece1;
    private String piece2;
    private long loose;

    public Rooms() {
    }

    public Rooms(String player1, String player2, String gameMode, String gameName, long ranking, long turn, String piece1, String piece2, long loose) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameMode = gameMode;
        this.gameName = gameName;
        this.ranking = ranking;
        this.turn = turn;
        this.piece1 = piece1;
        this.piece2 = piece2;
        this.loose = loose;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getRanking() {
        return ranking;
    }

    public void setRanking(long ranking) {
        this.ranking = ranking;
    }

    public long getTurn() {
        return turn;
    }

    public void setTurn(long turn) {
        this.turn = turn;
    }

    public String getPiece1() {
        return piece1;
    }

    public void setPiece1(String piece1) {
        this.piece1 = piece1;
    }

    public String getPiece2() {
        return piece2;
    }

    public void setPiece2(String piece2) {
        this.piece2 = piece2;
    }

    @Override
    public String toString() {
        return "Rooms{" +
                "player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", gameMode='" + gameMode + '\'' +
                ", gameName='" + gameName + '\'' +
                ", ranking=" + ranking +
                ", turn=" + turn +
                ", piece1='" + piece1 + '\'' +
                ", piece2='" + piece2 + '\'' +
                '}';
    }

    public long getLoose() {
        return loose;
    }
}
