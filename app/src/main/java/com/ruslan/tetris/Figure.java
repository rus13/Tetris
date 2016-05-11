package com.ruslan.tetris;

import android.graphics.Color;
import java.util.Random;

public class Figure {
    static final String[][] forms = {
            {/*I*/"****", "*\n*\n*\n*"},
            {/*J*/ "*..\n***", "**\n*.\n*.", "***\n..*", ".*\n.*\n**"},
            {/*L*/ "..*\n***", "*.\n*.\n**", "***\n*..", "**\n.*\n.*"},
            {/*O*/ "**\n**"},
            {/*S*/ ".**\n**.", "*.\n**\n.*"},
            {/*T*/ ".*.\n***", "*.\n**\n*.", "***\n.*.", ".*\n**\n.*"},
            {/*Z*/ "**.\n.**", ".*\n**\n*."}
    };
    static final int[] colors = {Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN};

    private int form;
    private int rotation;
    private int color;

    private int width;
    private int height;

    private int posX;
    private int posY;

    Figure(int f, int r, int c) {
        form = f;
        rotation = r;
        color = c;
        adjust();
    }

    private void adjust() {
        String[] l = forms[form][rotation].split("\n");
        width = l[0].length();
        height = l.length;
    }
    public void setPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    public void rotate() {
        rotation = (rotation - 1 + forms[form].length) % forms[form].length;
        adjust();
    }
    public void rotateBack() {
        rotation = (rotation + 1) % forms[form].length;
        adjust();
    }
    public void moveLeft(){
        posX--;
    }
    public void moveRight(){
        posX++;
    }
    public void moveDown(){
        posY--;
    }
    public void moveUp(){
        posY++;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getColor() {
        return color;
    }
    public int getPosX() {
        return posX;
    }
    public int getPosY() {
        return posY;
    }

    public boolean occipiesPosition(int row, int column) {
        String f = forms[form][rotation];
        String[] lines = f.split("\n");
        if (row >= posY && row < posY + height && column >= posX && column < posX + width && lines[row - posY].charAt(column - posX) == '*')
            return true;
        else
            return false;
    }
    public static Figure randomFigure() {
        Random rand = new Random();
        int f = rand.nextInt(forms.length);
        int r = rand.nextInt(forms[f].length);
        int c = rand.nextInt(colors.length);
        return new Figure(f, r, colors[c]);
    }
}