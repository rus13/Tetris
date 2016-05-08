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
    public void setOccupied(boolean occ){
        occupied = occ;
    }
    public void free(){
        occupied = false;
    }
}
