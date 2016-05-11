package com.ruslan.tetris;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class TetrisView extends View {
    private int default_cell_size ;
    private int blank_color;

    private Paint fill_paint;
    private Paint stroke_paint;

    private int columns;
    private int rows;

    private int cell_size;
    private int offset_x;
    private int offset_y;

    private Block[][] grid;
    private Figure fig;

    public TetrisView(Context context) {
        super(context);
        init(null, 0);
    }
    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public TetrisView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        Context cont = getContext();
        final TypedArray a = cont.obtainStyledAttributes(attrs, R.styleable.TetrisView, defStyle, 0);
        columns = a.getInt(R.styleable.TetrisView_blocksX, 10);
        rows = a.getInt(R.styleable.TetrisView_blocksY, 20);
        a.recycle();
        // Set up colors and default values
        default_cell_size = cont.getResources().getInteger(R.integer.default_cell_size);
        blank_color = cont.getResources().getInteger(R.integer.blank_color);
        int stroke_color = cont.getResources().getInteger(R.integer.stroke_color);
        int stroke_width = cont.getResources().getInteger(R.integer.stroke_width);
        // Set up a default fill Paint
        fill_paint = new Paint();
        fill_paint.setAntiAlias(true);
        fill_paint.setColor(blank_color);
        fill_paint.setStyle(Paint.Style.FILL);
        // Set up a default stroke Paint
        stroke_paint = new Paint();
        stroke_paint.setAntiAlias(true);
        stroke_paint.setColor(stroke_color);
        stroke_paint.setStyle(Paint.Style.STROKE);
        stroke_paint.setStrokeWidth(stroke_width);
        // Setup the grid cells
        grid = new Block[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                grid[r][c] = new Block();
                freeBlock(r,c);
            }
        }
    }
    public void setBlock(int r, int c, int color) {
        if (!(0 <= c&& c < columns))
            throw new IllegalArgumentException("setBlock: x coordinate out of range");
        if (!(0 <= r && r < rows))
            throw new IllegalArgumentException("setBlock: y coordinate out of range");
        grid[r][c].setColor(color);
        grid[r][c].setOccupied(true);
    }
    public void freeBlock(int r, int c) {
        if (!(0 <= c && c < columns))
            throw new IllegalArgumentException("setBlock: x coordinate out of range");
        if (!(0 <= r && r < rows))
            throw new IllegalArgumentException("setBlock: y coordinate out of range");
        grid[r][c].setOccupied(false);
        grid[r][c].setColor(blank_color);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMsMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMsSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMsMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMsSize = MeasureSpec.getSize(heightMeasureSpec);
        // Determine view columns and rows: either default size or passed size
        int vw = (widthMsMode == MeasureSpec.UNSPECIFIED) ? columns * default_cell_size : widthMsSize;
        int vh = (heightMsMode == MeasureSpec.UNSPECIFIED) ? rows * default_cell_size : heightMsSize;
        // Determine cell columns and rows
        double cw = vw / columns;
        double ch = vh / rows;
        double size = Math.min(cw, ch);
        cell_size = (int) Math.floor(size);
        // Determine offset
        offset_x = (vw - cell_size * columns) / 2;
        offset_y = (vh - cell_size * rows) / 2;
        // Satisfy contract by calling setMeasuredDimension
        setMeasuredDimension(2 * offset_x + columns * cell_size, 2 * offset_y + rows * cell_size);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw grid && figure
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int dx = c * cell_size + offset_x;
                int dy = (rows - r - 1)* cell_size + offset_y;
                Rect rect = new Rect(dx + 1, dy + 1, dx + cell_size - 2, dy + cell_size - 2);
                if(fig.occipiesPosition(r,c))
                    fill_paint.setColor(fig.getColor());
                else
                    fill_paint.setColor(grid[r][c].getColor());
                canvas.drawRect(rect, fill_paint);
                canvas.drawRect(rect, stroke_paint);
            }
        }
    }
    public void setFigure(Figure f){
        fig = f;
    }
    public int getColumns(){
        return columns;
    }
    public int getRows(){
        return rows;
    }
    public Block[][] getGrid() {
        return grid;
    }
}
