package com.ruslan.tetris;


import java.util.Random;

public class Figure {
    //enum Form{I,J,L,O,S,T,Z};
    //enum Rotation{};
    static final String[][] forms ={
            {/*I*/"****","*\n*\n*\n*"},
            {/*J*/ "*..\n***","**\n*.\n*.", ".*\n.*\n**","***\n..*"},
            {/*L*/ "..*\n***","*.\n*.\n**","***\n*..","**\n.*\n.*"},
            {/*O*/ "**\n**"},
            {/*S*/ ".**\n**.","*.\n**\n.*"},
            {/*T*/ ".*.\n***","*.\n**\n*.","***\n.*.",".*\n**\n.*"},
            {/*Z*/ "**.\n.**",".*\n**\n*."}
    };
    private int form;
    private int rotation;
    private int width;
    private int height;
    Figure(int f, int r){
        form = f;
        rotation = r;
        setShape();
    }
    private void setShape(){
        String[] l = forms[form][rotation].split("\n");
        width = l[0].length();
        height = l.length;
    }
    public void rotate(){
        rotation = (rotation + 1) % forms[form].length;
        setShape();
    }
    public void rotateBack(){
        rotation = (rotation - 1 + forms[form].length) % forms[form].length;
        setShape();
    }
    public String getForm(){
        return forms[form][rotation];
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public static Figure randomFigure(){
        Random rand = new Random();
        int f = rand.nextInt(forms.length);
        int r = rand.nextInt(forms[f].length);
        return new Figure(f,r);
    }
}