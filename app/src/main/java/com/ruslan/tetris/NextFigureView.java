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
public class NextFigureView extends View {
    private Paint fill_paint;
    private Paint stroke_paint;

    private int cell_size;
    TetrisModel model;

    public NextFigureView(Context context) {
        super(context);
        init(null, 0);
    }

    public NextFigureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NextFigureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Set up a default fill Paint
        Context cont = getContext();
        int fill_color = cont.getResources().getInteger(R.integer.blank_color);
        int stroke_color = cont.getResources().getInteger(R.integer.stroke_color);
        int stroke_width = cont.getResources().getInteger(R.integer.stroke_width);
        fill_paint = new Paint();
        fill_paint.setAntiAlias(true);
        fill_paint.setColor(fill_color);
        fill_paint.setStyle(Paint.Style.FILL);
        // Set up a default stroke Paint
        stroke_paint = new Paint();
        stroke_paint.setAntiAlias(true);
        stroke_paint.setColor(stroke_color);
        stroke_paint.setStyle(Paint.Style.STROKE);
        stroke_paint.setStrokeWidth(stroke_width);
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

        int rows = model.getNextFigureYDim();
        int columns = model.getNextFigureXDim();
        int color = model.getNextFigureColor();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int dx = c * cell_size;
                int dy = (rows - r - 1)* cell_size;
                Rect rect = new Rect(dx + 1, dy + 1, dx + cell_size - 2, dy + cell_size - 2);
                if(model.nextFigureSetAt(r,c)) {
                    fill_paint.setColor(color);
                    canvas.drawRect(rect, fill_paint);
                    canvas.drawRect(rect, stroke_paint);
                }
            }
        }
    }

    public void setModel(TetrisModel m){
        model = m;
    }

    public void setCellSize(int s){
        cell_size = s;
    }

}
