package com.ruslan.tetris;

import android.os.Handler;

public class TetrisController {
    private final int default_speed = 700;
    State state;
    State backup_state;
    int post_id;
    GameEngine game;

    private int current_speed;
    TetrisModel model;
    TetrisActivity activity;

    TetrisController(TetrisActivity a, TetrisModel m){
        model = m;
        activity = a;
        current_speed = default_speed;
        state = State.RUNNING;
        backup_state = state;
        post_id = 0;
        game = new GameEngine(post_id);
    }

    public void processEvent(InputEvent e){
            switch (e) {
                case LEFT:
                    if(state == State.RUNNING)
                        model.moveFigureLeft();
                    break;
                case RIGHT:
                    if(state == State.RUNNING)
                        model.moveFigureRight();
                    break;
                case ROTATE_LEFT:
                    if(state == State.RUNNING)
                        model.rotateFigureLeft();
                    break;
                case ROTATE_RIGHT:
                    if(state == State.RUNNING)
                        model.rotateFigureRight();
                    break;
                case DOWN:
                    if(state == State.RUNNING)
                        model.moveFigureDown();
                    break;
                case DOWN_BOTTOM:
                    if(state == State.RUNNING)
                        while(model.moveFigureDown());
                    break;
                case PAUSE:
                    state = State.PAUSE;
                    break;
                case RESUME:
                    state = State.RUNNING;
                    break;
                default:
                    break;
            }
            activity.update();
    }

    public void start(){
        final Handler handler = new Handler();
        handler.postDelayed(game, current_speed);
    }

    public void stop(){
        backup_state = state;
        state = State.STOP;
    }

    public void resume(){
        state = backup_state;
        game = new GameEngine(++post_id);
        start();
    }

    private void game_over() {
        activity.game_over();
    }
    public boolean isPause(){
        return state == State.PAUSE;
    }
    enum State {
        RUNNING, PAUSE, PLACE_FIGURE, GAME_OVER, STOP
    }
    enum InputEvent{
        LEFT, RIGHT, DOWN_BOTTOM, DOWN, ROTATE_LEFT, ROTATE_RIGHT, PAUSE, RESUME
    }

    class GameEngine implements Runnable {
        final Handler handler;
        int post_counter;
        GameEngine(int pc){
            handler = new Handler();
            post_counter = pc;
        }
        @Override
        public void run() {
            if(post_counter < post_id)
                return;
            if (model.isGameOver())
                state = State.GAME_OVER;
            switch (state) {
                case RUNNING:
                    if (!model.moveFigureDown())
                        state = State.PLACE_FIGURE;
                    handler.postDelayed(this, current_speed);
                    break;
                case PLACE_FIGURE:
                    model.placeCurrentFigure();
                    state = State.RUNNING;
                    handler.postDelayed(this, current_speed);
                    break;
                case PAUSE:
                    handler.postDelayed(this, current_speed);
                case STOP:
                    break;
                case GAME_OVER:
                    game_over();
                    break;
                default:
                    break;
            }
            activity.update();
        }
    }
}
