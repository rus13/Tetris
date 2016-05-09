package com.ruslan.tetris;

public class Block {

    private boolean occupied;
    private int color;

    public Block(){
        occupied = false;
    }

    public boolean isOccupied(){
        return occupied;
    }
    public void setColor(int c){
        color = c;
    }
    public int getColor(){
        return color;
    }
    public void setOccupied(boolean occ){
        occupied = occ;
    }
    public void free(){
        occupied = false;
    }
}