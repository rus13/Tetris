package com.ruslan.tetris;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 */
public class GridView extends View {
    private Paint fill_paint;
    private Paint stroke_paint;

    private int cell_size;
    private int offset_x;
    private int offset_y;

    private TetrisModel model;
    int rows;
    int columns;

    public GridView(Context context) {
        super(context);
        init(null, 0);
    }
    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        Context cont = getContext();
        rows = cont.getResources().getInteger(R.integer.blocksY);
        columns = cont.getResources().getInteger(R.integer.blocksX);
        // Set up colors and default values
        // Set up a default fill Paint
        fill_paint = new Paint();
        fill_paint.setAntiAlias(true);
        fill_paint.setStyle(Paint.Style.FILL);
        // Set up a default stroke Paint
        stroke_paint = new Paint();
        stroke_paint.setAntiAlias(true);
        stroke_paint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int widthMsMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMsSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMsMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMsSize = MeasureSpec.getSize(heightMeasureSpec);
        // Determine controller columns and rows: either default size or passed size
        int default_cell_size = getContext().getResources().getInteger(R.integer.default_cell_size);
        int vw = (widthMsMode == MeasureSpec.UNSPECIFIED) ? columns * default_cell_size : widthMsSize;
        int vh = (heightMsMode == MeasureSpec.UNSPECIFIED) ? rows * default_cell_size : heightMsSize;
        // Determine cell columns and rows
        double cw = vw / columns;
        double ch = vh / rows;
        double size = Math.min(cw, ch);
        cell_size = (int) Math.floor(size);
        // Determine offset
//        offset_x = (vw - cell_size * columns) / 2;
//        offset_y = (vh - cell_size * rows) / 2;
        // Satisfy contract by calling setMeasuredDimension
        setMeasuredDimension(columns * cell_size, rows * cell_size);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw grid && figure
        int blank_color = getContext().getResources().getInteger(R.integer.blank_color);
        int stroke_color = getContext().getResources().getInteger(R.integer.stroke_color);
        int stroke_width = getContext().getResources().getInteger(R.integer.stroke_width);
        stroke_paint.setColor(stroke_color);
        stroke_paint.setStrokeWidth(stroke_width);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int dx = c * cell_size;
                int dy = (rows - r - 1)* cell_size;
                Rect rect = new Rect(dx + 1, dy + 1, dx + cell_size - 2, dy + cell_size - 2);
                if(model.isOccupiedAt(r,c))
                    fill_paint.setColor(model.colorAt(r,c));
                else
                    fill_paint.setColor(blank_color);
                canvas.drawRect(rect, fill_paint);
                canvas.drawRect(rect, stroke_paint);
            }
        }
        //draw border
        Rect rect = new Rect(1, 1, columns * cell_size - 2, rows * cell_size - 2);
        int black_color =  getContext().getResources().getInteger(R.integer.black_color);
        stroke_paint.setColor(black_color);
        stroke_paint.setStrokeWidth(stroke_width * 3);
        canvas.drawRect(rect, stroke_paint);
    }
    public void setModel(TetrisModel m){
        model = m;
    }
    public int getCellSize(){
        return cell_size;
    }
}