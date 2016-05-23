package com.ruslan.tetris;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TetrisActivity extends AppCompatActivity {
    TetrisController controller;
    TetrisModel model;

    GestureDetectorCompat mDetector;
    MyGestureListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Load attributes
        int columns = getApplicationContext().getResources().getInteger(R.integer.blocksX);
        int rows = getApplicationContext().getResources().getInteger(R.integer.blocksY);
        model = new TetrisModel(rows, columns);
        controller = new TetrisController(this, model);
        //Set up the views
        GridView g = (GridView) findViewById(R.id.tetris_view);
        g.setModel(model);
        NextFigureView nf = (NextFigureView) findViewById(R.id.next_figure);
        nf.setModel(model);
        gestureListener = new MyGestureListener();
        mDetector = new GestureDetectorCompat(this, gestureListener);
        //Create Button listener
        setUpButtonListener();
        controller.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                newGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void newGame(){
        this.recreate();
    }
    @Override
    protected void onPause(){
        super.onPause();
        ImageButton pause_button = (ImageButton) findViewById(R.id.button_pause);
        pause_button.setImageResource(R.drawable.resume);
        controller.processEvent(TetrisController.InputEvent.PAUSE);
    }
    @Override
    protected void onResume(){
        super.onResume();
        ImageButton pause_button = (ImageButton) findViewById(R.id.button_pause);
        pause_button.setImageResource(R.drawable.pause);
        controller.processEvent(TetrisController.InputEvent.RESUME);
    }
    // Initialize the button listener
    private void setUpButtonListener() {
//        ImageButton ibutton = (ImageButton) findViewById(R.id.button_left);
//        assert ibutton != null;
//        ibutton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                controller.processEvent(TetrisController.InputEvent.LEFT);
//            }
//        });
//        ibutton = (ImageButton) findViewById(R.id.button_right);
//        assert ibutton != null;
//        ibutton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                    controller.processEvent(TetrisController.InputEvent.RIGHT);
//            }
//        });
//        ibutton = (ImageButton) findViewById(R.id.button_down);
//        assert ibutton != null;
//        ibutton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    controller.processEvent(TetrisController.InputEvent.DOWN_START);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    controller.processEvent(TetrisController.InputEvent.DOWN_END);
//                }
//                return true;
//            }
//        });
//        ibutton = (ImageButton) findViewById(R.id.button_rotate_left);
//        assert ibutton != null;
//        ibutton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                    controller.processEvent(TetrisController.InputEvent.ROTATE_LEFT);
//                }
//        });
//        ibutton = (ImageButton) findViewById(R.id.button_rotate_right);
//        assert ibutton != null;
//        ibutton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                controller.processEvent(TetrisController.InputEvent.ROTATE_RIGHT);
//            }
//        });
        final ImageButton pause_button = (ImageButton) findViewById(R.id.button_pause);
        assert pause_button != null;
        pause_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(controller.isPause()){
                    controller.processEvent(TetrisController.InputEvent.RESUME);
                    pause_button.setImageResource(R.drawable.pause);
                }
                else {
                    controller.processEvent(TetrisController.InputEvent.PAUSE);
                    pause_button.setImageResource(R.drawable.resume);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GridView g = (GridView) findViewById(R.id.tetris_view);
        gestureListener.setStepSize(g.getCellSize() * 1.3F);
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void update(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                GridView g = (GridView) findViewById(R.id.tetris_view);
                g.invalidate();
                TextView t = (TextView) findViewById(R.id.score_num);
                t.setText(Integer.toString(model.getScore()));
                NextFigureView nf = (NextFigureView) findViewById(R.id.next_figure);
                nf.setCellSize(g.getCellSize());
                nf.invalidate();
            }
        });
    }

    public void game_over() {
        TextView t = (TextView) findViewById(R.id.game_over);
        String text = getApplicationContext().getResources().getString(R.string.game_over);
        t.setText(text);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        final int FLING_TIME_THRSH = 300;

        float step_size;
        float distX;
        float distY;

        MyGestureListener(){
            super();
            distX = 0;
            distY = 0;
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            if(Math.abs(distanceY) > Math.abs(distanceX)){
                distY += distanceY;
                if(distY <= - step_size){
                    controller.processEvent(TetrisController.InputEvent.DOWN);
                    distY += step_size;
                }
            }
            else{
                distX += distanceX;
                if(distX >= step_size){
                    controller.processEvent(TetrisController.InputEvent.LEFT);
                    distX -= step_size;
                }
                if(distX <= - step_size){
                    controller.processEvent(TetrisController.InputEvent.RIGHT);
                    distX += step_size;
                }
            }
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            if(event.getX() > getApplicationContext().getResources().getDisplayMetrics().widthPixels / 2)
                controller.processEvent(TetrisController.InputEvent.ROTATE_RIGHT);
            else
                controller.processEvent(TetrisController.InputEvent.ROTATE_LEFT);
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if(velocityY > velocityX && Math.abs(event2.getY() - event1.getY()) > 2 * step_size && event2.getEventTime() - event1.getEventTime() < FLING_TIME_THRSH){
                controller.processEvent(TetrisController.InputEvent.DOWN_BOTTOM);
            }
            return true;
        }
        public void setStepSize(float cs){
            step_size = cs;
        }
    }
}