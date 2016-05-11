package com.ruslan.tetris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TetrisActivity extends AppCompatActivity{
    private final int min_speed = 70;
    private final int default_speed = 700;
    private int current_speed = 700;
    private int score = 0;

    enum State{RUNNING, PLACE_FIGURE};
    State state;
    private final Object lock = new Object();

    TetrisView tetris_view;
    Thread game_thread;
    private int rows;
    private int columns;
    private Block[][] grid;
    private Figure fig;

    private void setUpButtonListener(){
        Button button = (Button) findViewById(R.id.button_left);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                synchronized (lock) {
                    if (state == State.RUNNING)
                        moveFigureLeft();
                }
            }
        });
        button = (Button) findViewById(R.id.button_right);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                synchronized (lock) {
                    if (state == State.RUNNING)
                        moveFigureRight();
                }
            }
        });
        button = (Button) findViewById(R.id.button_down);
        assert button != null;
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    current_speed = min_speed;
                    synchronized (lock){
                        lock.notify();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    current_speed = default_speed;
                }
                return true;
            }
        });
        button = (Button) findViewById(R.id.button_rotate);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                synchronized (lock) {
                    if (state == State.RUNNING)
                        rotateFigure();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tetris_view = (TetrisView) findViewById(R.id.tetris_view);
        rows = tetris_view.getRows();
        columns = tetris_view.getColumns();
        grid = tetris_view.getGrid();
        createNewFigure();
        //Create Button listener
        setUpButtonListener();
        // Use a new thread
        if(game_thread == null) {
            game_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        play_game();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            game_thread.start();
        }
    }

/*    @Override
    protected void onPause() {
        super.onPause();
        try {
            synchronized (game_thread) {
                game_thread.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (game_thread) {
            game_thread.notify();
        }
    }*/

    private void draw(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tetris_view.invalidate();
            }
        });
    }


    public void play_game() throws InterruptedException {
        while(feasiblePlacement()){
           // pause();
            state = State.RUNNING;
            while(state == State.RUNNING){
                pause();
                synchronized (lock){
                    if(!moveFigureDown())
                        state = State.PLACE_FIGURE;
                }
            }
            update();
        }
        game_over();
    }
    private void game_over(){

    }
    private void pause() throws InterruptedException {
        synchronized (lock){
            lock.wait(current_speed);
        }
    }
    private void update(){
        placeFigure();
        checkFullRows();
        createNewFigure();
        updateScore();
    }
    private void updateScore(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) findViewById(R.id.score_num);
                view.setText(Integer.toString(score));
                tetris_view.invalidate();
            }
        });
    }
    private void createNewFigure(){
        fig = Figure.randomFigure();
        int fig_x = columns / 2 - 1;
        int fig_y = rows - fig.getHeight();
        fig.setPosition(fig_x,fig_y);
        tetris_view.setFigure(fig);
        draw();
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
                score++;
            }
            else if (empty_row >= 0) {
                swapRows(r, empty_row);
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
    /*
    * The method checks whether the
    * */
    private boolean feasiblePlacement(){
        int fig_y = fig.getPosY();
        int fig_x = fig.getPosX();
        //out of bounds
        if(fig_x < 0 || fig_y < 0 || fig_x + fig.getWidth() > columns || fig_y + fig.getHeight() > rows)
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
    /*
    * Methods for changing the position or the rotation shape of the figure
    * The changes are done only if its a feasible change, i.e. no overlapping with blocks
    * */
    public void rotateFigure(){
        fig.rotate();
        if(!feasiblePlacement())
            fig.rotateBack();
        draw();
    }
    public void moveFigureLeft(){
        if( fig.getPosX() > 0){
            fig.moveLeft();
            if(!feasiblePlacement())
                fig.moveRight();
        }
        draw();
    }
    public void moveFigureRight(){
        if( fig.getPosX() < columns - 1){
            fig.moveRight();
            if(!feasiblePlacement())
                fig.moveLeft();
        }
        draw();
    }
    /*private boolean downPossible(){
        //check if figure is already on the ground
        if(fig.getPosY() <= 0)
            return false;
        fig.moveDown();
        boolean feasible = feasiblePlacement();
        fig.moveUp();
        return feasible;
    }*/
    public boolean moveFigureDown(){
        if(fig.getPosY() <= 0)
            return false;
        fig.moveDown();
        boolean feasible = feasiblePlacement();
        if(!feasible)
            fig.moveUp();
        draw();
        return feasible;
    }
}