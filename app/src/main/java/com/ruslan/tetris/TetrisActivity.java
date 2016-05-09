package com.ruslan.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TetrisActivity extends AppCompatActivity{
    private int default_figure_xpos = 4;
    private int speed = 1000;

    TetrisView tetris_view;
    private int rows;
    private int columns;
    private Block[][] grid;

    private Figure fig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tetris_view = (TetrisView) findViewById(R.id.tetris_view);
        //tetris_view.setOnCellTouchListener(mNumGridView_OnCellTouchListener);
        rows = tetris_view.getRows();
        columns = tetris_view.getColumns();
        grid = tetris_view.getGrid();
        default_figure_xpos = columns / 2 - 1;
        createNewFigure();
        try {
            play_game();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void play_game() throws InterruptedException {
        while(feasiblePlacement()){
            while(downPossible()){
                Thread.sleep(speed);
                down();
            }
            Thread.sleep(speed);
            placeFigure();
            checkFullRows();
            createNewFigure();
        }
        game_over();
    }
    private void game_over(){

    }

    private void createNewFigure(){
        fig = Figure.randomFigure();
        int fig_x = default_figure_xpos;
        int fig_y = rows - fig.getHeight();
        fig.setPosition(fig_x,fig_y);
        tetris_view.setFigure(fig);
    }

    private boolean isFullRow(int row){
        for(int c = 0; c < columns; c++){
            if(!grid[row][c].isOccupied())
                return false;
        }
        return true;
    }
    private void deleteRow(int row){
        for(int c = 0; c < columns; c++) {
            tetris_view.freeBlock(row,c);
        }
    }
    private void swapRows(int r1, int r2){
        Block tmp;
        for(int c = 0; c < columns; c++){
            tmp = grid[r2][c];
            grid[r2][c] = grid[r1][c];
            grid[r1][c] = tmp;
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
            else if (empty_row >= 0) {
                swapRows(r, empty_row);
                //deleteRow(r);
                empty_row++;
            }
        }
    }
    private void placeFigure(){
        int fig_y = fig.getPosY();
        int fig_x = fig.getPosX();
        if(feasiblePlacement()){
            for(int r = fig_y; r < fig_y + fig.getHeight(); r++){
                for(int c = fig_x; c < fig_x + fig.getWidth(); c++){
                    if(fig.occipiesPosition(r,c)){
                        tetris_view.setBlock(r,c, fig.getColor());
                    }
                }
            }
        }
    }
    private boolean feasiblePlacement(){
        int fig_y = fig.getPosY();
        int fig_x = fig.getPosX();
        //out of bounds
        if((fig_x + fig.getWidth() > columns) || (fig_y + fig.getHeight() > rows))
            return false;
        //overlapping with some block
        for(int r = fig_y; r < fig_y + fig.getHeight(); r++){
            for(int c = fig_x; c < fig_x + fig.getWidth(); c++){
                if(fig.occipiesPosition(r,c) && grid[r][c].isOccupied())
                    return false;
            }
        }
        return true;
    }
    private boolean downPossible(){
        int fig_y = fig.getPosY();
        int fig_x = fig.getPosX();
        //check if figure is already on the ground
        if(fig_y == 0)
            return false;
        //check if beneath the figure there is some occupied block
        for(int r = fig_y; r < fig_y + fig.getHeight(); r++){
            for(int c = fig_x; c < fig_x + fig.getWidth(); c++){
                if(fig.occipiesPosition(r,c) && grid[r-1][c].isOccupied())
                    return false;
            }
        }
        return true;
    }
    public void rotateFigure(){
        fig.rotate();
        if(!feasiblePlacement())
            fig.rotateBack();
    }
    public void left(){
        int fig_x = fig.getPosX();
        if( fig_x > 0)
            fig.setPosition(fig_x, fig.getPosY());
    }
    public void right(){
        int fig_x = fig.getPosX();
        if(fig_x + fig.getWidth() < columns)
            fig.setPosition(fig_x+1, fig.getPosY());
    }
    public void down(){
        int fig_y = fig.getPosY();
        if(fig_y > 0 && downPossible()){
            fig.setPosition(fig.getPosX(), fig_y - 1);
        }
    }
}