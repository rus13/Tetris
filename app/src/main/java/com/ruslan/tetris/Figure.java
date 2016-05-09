package com.ruslan.tetris;


import android.graphics.Color;

import java.util.Random;

public class Figure {
    //enum Form{I,J,L,O,S,T,Z};
    //enum Rotation{};
    static final String[][] forms = {
            {/*I*/"****", "*\n*\n*\n*"},
            {/*J*/ "*..\n***", "**\n*.\n*.", ".*\n.*\n**", "***\n..*"},
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

    public static Figure randomFigure() {
        Random rand = new Random();
        int f = rand.nextInt(forms.length);
        int r = rand.nextInt(forms[f].length);
        int c = rand.nextInt(colors.length);
        return new Figure(f, r, colors[c]);
    }

    private void adjust() {
        String[] l = forms[form][rotation].split("\n");
        width = l[0].length();
        height = l.length;
    }

    public void rotate() {
        rotation = (rotation + 1) % forms[form].length;
        adjust();
    }

    public void rotateBack() {
        rotation = (rotation - 1 + forms[form].length) % forms[form].length;
        adjust();
    }

    public void setPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public String getForm() {
        return forms[form][rotation];
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

    public boolean occipiesPosition(int row, int column) {
        String form = getForm();
        String[] lines = form.split("\n");
        for (int i = 0; i < lines.length; i++) {
            for (int j = 0; j < lines[i].length(); j++) {
                if (row == posY + i && column == posX + j && lines[i].charAt(j) == '*')
                    return false;
            }
        }
        return false;
    }
}