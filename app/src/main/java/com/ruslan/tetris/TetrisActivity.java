package com.ruslan.tetris;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TetrisActivity extends AppCompatActivity {
    TetrisController controller;
    TetrisModel model;

    boolean pause;

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
        pause = false;
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
        controller.processEvent(TetrisController.InputEvent.PAUSE);
    }
    @Override
    protected void onResume(){
        super.onResume();
        controller.processEvent(TetrisController.InputEvent.RESUME);
    }
    // Initialize the button listener, also use lock object to synchronize the action of the game and the user
    private void setUpButtonListener() {
        ImageButton ibutton = (ImageButton) findViewById(R.id.button_left);
        assert ibutton != null;
        ibutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controller.processEvent(TetrisController.InputEvent.LEFT);
            }
        });
        ibutton = (ImageButton) findViewById(R.id.button_right);
        assert ibutton != null;
        ibutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    controller.processEvent(TetrisController.InputEvent.RIGHT);
            }
        });
        ibutton = (ImageButton) findViewById(R.id.button_down);
        assert ibutton != null;
        ibutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    controller.processEvent(TetrisController.InputEvent.DOWN_START);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    controller.processEvent(TetrisController.InputEvent.DOWN_END);
                }
                return true;
            }
        });
        ibutton = (ImageButton) findViewById(R.id.button_rotate_left);
        assert ibutton != null;
        ibutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    controller.processEvent(TetrisController.InputEvent.ROTATE_LEFT);
                }
        });
        ibutton = (ImageButton) findViewById(R.id.button_rotate_right);
        assert ibutton != null;
        ibutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controller.processEvent(TetrisController.InputEvent.ROTATE_RIGHT);
            }
        });
        final ImageButton pause_button = (ImageButton) findViewById(R.id.button_pause);
        assert pause_button != null;
        pause_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pause){
                    controller.processEvent(TetrisController.InputEvent.RESUME);
                    pause_button.setImageResource(R.drawable.pause);
                }
                else {
                    controller.processEvent(TetrisController.InputEvent.PAUSE);
                    pause_button.setImageResource(R.drawable.resume);
                }
                pause = !pause;
            }
        });
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

}