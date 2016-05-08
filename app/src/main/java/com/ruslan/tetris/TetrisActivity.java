package com.ruslan.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TetrisActivity extends AppCompatActivity{
    static final int height = 20;
    static final int width = 10;

    TetrisView tetris_view;

    private int speed = 10;
    private Block[][] grid;

    private Figure cur_figure;
    private int x,y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tetris_view = (TetrisView) findViewById(R.id.tetris_view);
        //tetris_view.setOnCellTouchListener(mNumGridView_OnCellTouchListener);
        grid = new Block[height][width];
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                grid[i][j] = new Block();
            }
        }
        cur_figure = Figure.randomFigure();
        try {
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() throws InterruptedException {
        while(feasiblePlacement()){
            while(downPossible()){
                Thread.sleep(1000);
                down();
                draw();
            }
            Thread.sleep(1000);
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
            if(!grid[row][i].isOccupied())
                return false;
        }
        return true;
    }
    private void deleteRow(int row){
        for(int i = 0; i < width; i++)
            grid[row][i].setOccupied(false);
    }
    private void swapRows(int r1, int r2){
        Block tmp;
        for(int i = 0; i < width; i++){
            tmp = grid[r1][i];
            grid[r2][i] = grid[r1][i];
            grid[r1][i] = tmp;
        }
    }
    private void checkFullRows(){
        int empty_row = -1;
        for(int r = 0; r < grid.length; r++){
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
                        grid[x+i][y+j].setOccupied(true);
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
                if(lines[i].charAt(j) == '*' && grid[x+i][y+j].isOccupied())
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
            if(lines[0].charAt(i) == '*' && grid[i][y - 1].isOccupied())
                return false;
        }
        return true;
    }
}
