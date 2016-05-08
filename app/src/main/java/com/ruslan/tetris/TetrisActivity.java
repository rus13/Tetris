package com.ruslan.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TetrisActivity extends AppCompatActivity{
    static final int height = 20;
    static final int width = 10;

    TetrisView tetris_view;

    private int speed = 10;
    private Block[][] board;

    private Figure cur_figure;
    private int x,y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tetris_view= (TetrisView) findViewById(R.id.tetris_view);
        //tetris_view.setOnCellTouchListener(mNumGridView_OnCellTouchListener);
        /*board = new Block[height][width];
        cur_figure = Figure.randomFigure();
        try {
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void run() throws InterruptedException {
        while(feasiblePlacement()){
            while(downPossible()){
                wait(100);
                down();
                draw();
            }
            wait(100);
            placeFigure();
            checkFullRows();
            draw();
            cur_figure = Figure.randomFigure();
        }
    }
    private void draw(){

    }

    private boolean isFullRow(int row){
        for(int i = 0; i < width; i++){
            if(!board[i][row].isOccupied())
                return false;
        }
        return true;
    }
    private void deleteRow(int row){
        for(int i = 0; i < width; i++)
            board[i][row].setOccupied(false);
    }
    private void swapRows(int r1, int r2){
        Block tmp;
        for(int i = 0; i < width; i++){
            tmp = board[i][r1];
            board[i][r2] = board[i][r1];
            board[i][r1] = tmp;
        }
    }
    private void checkFullRows(){
        int empty_row = -1;
        for(int r = 0; r < board.length; r++){
            if(isFullRow(r)){
                deleteRow(r);
                if(empty_row == -1)
                    empty_row = r;
            }
            else{
                if(empty_row >= 0){
                    swapRows(r, empty_row);
                    deleteRow(r);
                    empty_row++;
                }
            }
        }
    }
    private void placeFigure(){
        if(feasiblePlacement()){
            String form = cur_figure.getForm();
            String[] lines = form.split("\n");
            for(int i = 0; i < lines.length; i++){
                for(int j = 0; j < lines[i].length(); j++){
                    if(lines[i].charAt(j) == '*')
                        board[x+i][y+j].setOccupied(true);
                }
            }
        }
    }
    private boolean feasiblePlacement(){
        //out of bounds
        if((x + cur_figure.getWidth() > width) || (y + cur_figure.getHeight() > height))
            return false;
        //overlapping with some block
        String form = cur_figure.getForm();
        String[] lines = form.split("\n");
        for(int i = 0; i < lines.length; i++){
            for(int j = 0; j < lines[i].length(); j++){
                if(lines[i].charAt(j) == '*' && board[x+i][y+j].isOccupied())
                    return false;
            }
        }
        return true;
    }
    public void rotateFigure(){
        cur_figure.rotate();
        if(!feasiblePlacement())
            cur_figure.rotateBack();
    }
    public void left(){
        if(x > 0)
            x--;
    }
    public void right(){
        if(x + cur_figure.getWidth() < width)
            x++;
    }
    public void down(){
        if(y > 0 && downPossible()){
            y--;
        }
    }
    private boolean downPossible(){
        if(y == 0)//ground
            return false;
        //check if beneath the figure there is some occupied block
        String form = cur_figure.getForm();
        String[] lines = form.split("\n");
        for(int i = 0; i < cur_figure.getWidth(); i++){
            if(lines[0].charAt(i) == '*' && board[i][y - 1].isOccupied())
                return false;
        }
        return true;
    }
}
