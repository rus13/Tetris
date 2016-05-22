package com.ruslan.tetris;

import android.app.Activity;
import android.os.Handler;

public class TetrisController {
    private final int min_speed = 70;
    private final int default_speed = 700;
    State state;

    private int current_speed;
    TetrisModel model;
    TetrisActivity activity;

    TetrisController(TetrisActivity a, TetrisModel m){
        model = m;
        activity = a;
        current_speed = default_speed;
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
                case DOWN_START:
                    current_speed = min_speed;
                    break;
                case DOWN_END:
                    current_speed = default_speed;
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
        state = State.RUNNING;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(model.isGameOver()) {
                    state = State.GAME_OVER;
                    game_over();
                }
                else{
                    switch(state){
                        case RUNNING:
                            if (!model.moveFigureDown())
                                state = State.PLACE_FIGURE;
                            break;
                        case PLACE_FIGURE:
                            model.placeCurrentFigure();
                            state = State.RUNNING;
                            break;
                        default:
                            break;
                    }
                    handler.postDelayed(this, current_speed);
                    activity.update();
                }
            }
        }, current_speed);
    }

//    public void togglePause(){
//        state = (state == State.PAUSE) ? State.RUNNING : State.PAUSE;
//    }
//
//    public void pause(){
//        state = State.PAUSE;
//    }
//
//    public void resume(){
//        state = State.RUNNING;
//    }

    private void game_over() {
        activity.game_over();
    }
    // State and the lock object are needed make the game thread safe (feasible user and game actions)
    enum State {
        RUNNING, PAUSE, PLACE_FIGURE, GAME_OVER
    }
    enum InputEvent{
        LEFT, RIGHT, DOWN_START, DOWN_END, ROTATE_LEFT, ROTATE_RIGHT, PAUSE, RESUME
    }
}
