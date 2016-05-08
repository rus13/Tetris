package com.ruslan.tetris;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class TetrisView extends View {
    private int default_cell_size = 20;
    private Paint blank_paint;
    private Paint stroke_paint;

    private int width;
    private int height;

    private int cell_size;
    private int offset_x;
    private int offset_y;

    private Paint[][] grid;

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
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TetrisView, defStyle, 0);

        width = a.getInt(R.styleable.TetrisView_blocksX, 10);
        height = a.getInt(R.styleable.TetrisView_blocksY, 20);
        a.recycle();

        // Set up a default Paint object
        blank_paint = new Paint();
        blank_paint.setAntiAlias(true);
        blank_paint.setColor(Color.LTGRAY);
        blank_paint.setStyle(Paint.Style.FILL);
        stroke_paint = new Paint();
        stroke_paint.setAntiAlias(true);
        stroke_paint.setColor(Color.WHITE);
        stroke_paint.setStyle(Paint.Style.STROKE);
        stroke_paint.setStrokeWidth(2.2F);
        // Setup the grid cells
        grid = new Paint[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Paint(blank_paint);
            }
        }
    }

    public void setCell(int x, int y, int color) {
        if (!(0 <= x && x < width))
            throw new IllegalArgumentException("setCell: x coordinate out of range");
        if (!(0 <= y && y < height))
            throw new IllegalArgumentException("setCell: y coordinate out of range");
        grid[x][y].setColor(color);
        invalidate();
    }
    //    public int getCell(int x, int y) {
//        if (!(0 <= x && x < width))
//            throw new IllegalArgumentException("getCell: x coordinate out of range");
//        if (!(0 <= y && y < height))
//            throw new IllegalArgumentException("getCell: y coordinate out of range");
//        return grid[x][y];
//    }
    public void freeCell(int x, int y) {
        if (!(0 <= x && x < width))
            throw new IllegalArgumentException("setCell: x coordinate out of range");
        if (!(0 <= y && y < height))
            throw new IllegalArgumentException("setCell: y coordinate out of range");
        grid[x][y].set(blank_paint);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMsMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMsSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMsMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMsSize = MeasureSpec.getSize(heightMeasureSpec);
        // Determine view width and height: either default size or passed size
        int vw = (widthMsMode == MeasureSpec.UNSPECIFIED) ? width * default_cell_size : widthMsSize;
        int vh = (heightMsMode == MeasureSpec.UNSPECIFIED) ? height * default_cell_size : heightMsSize;
        // Determine cell width and height
        double cw = vw / width;
        double ch = vh / height;
        double size = Math.min(cw, ch);
        cell_size = (int) Math.floor(size);
        // Determine offset
        offset_x = (vw - cell_size * width) / 2;
        offset_y = (vh - cell_size * height) / 2;
        // Satisfy contract by calling setMeasuredDimension
        setMeasuredDimension(2 * offset_x + width * cell_size, 2 * offset_y + height * cell_size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Draw a rectangle
                int dx = x * cell_size + offset_x;
                int dy = y * cell_size + offset_y;
                Rect r = new Rect(dx + 1, dy + 1, dx + cell_size - 2, dy + cell_size - 2);
                canvas.drawRect(r, grid[y][x]);
                canvas.drawRect(r, stroke_paint);
            }
        }
    }

    public int getWidthCells(){
        return width;
    }
}
