package com.example.projetmobile.Model.Mouvement;

public class Position {
    private int x;
    private int y;

    public Position(){
        this.x = 0;
        this.y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position addAndReturn(Position p){
        return new Position(this.x+p.x,this.y+p.y);
    }

    public void add(Position p){
        this.x+=p.x;
        this.y+=p.y;
    }

    public Position difference(Position p){
        return new Position(this.x - p.x, this.y-p.y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
