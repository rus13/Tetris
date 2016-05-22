package com.ruslan.tetris;

public class TetrisModel {
    private int score;

    private int rows;
    private int columns;

    private Block[][] grid;
    private Figure cur_fig;
    private Figure next_fig;

    TetrisModel(int row, int column){
        this.rows = row;
        this.columns = column;
        score = 0;
        for(int i = 0; i < 2; ++i)
            updateFigures();
        grid = new Block[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                grid[r][c] = new Block();
                grid[r][c].setOccupied(false);
            }
        }
    }
    // After the current figure is placed on the grid, a new figure is created
    private void updateFigures(){
        cur_fig = next_fig;
        next_fig = Figure.randomFigure();
        int fig_x = columns / 2 - 1;
        int fig_y = rows - next_fig.getHeight();
        next_fig.setPosition(fig_x,fig_y);
    }
    private boolean isFullRow(int row){
        for(int c = 0; c < columns; c++){
            if(!grid[row][c].isOccupied())
                return false;
        }
        return true;
    }
    private void deleteRow(int row){
        for(int c = 0; c < columns; c++)
            grid[row][c].setOccupied(false);
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
    private void updateGrid(){
        int fig_y = cur_fig.getPosY();
        int fig_x = cur_fig.getPosX();
        if(feasiblePlacement()){
            for(int r = fig_y; r < fig_y + cur_fig.getHeight(); r++){
                for(int c = fig_x; c < fig_x + cur_fig.getWidth(); c++){
                    if(cur_fig.occipiesPosition(r,c)){
                        grid[r][c].setColor(cur_fig.getColor());
                        grid[r][c].setOccupied(true);
                    }
                }
            }
        }
    }
    /*
    * The method checks whether the position of the current figure is feasible, i.e. no overlapping with full Blocks
    * */
    private boolean feasiblePlacement(){
        int fig_y = cur_fig.getPosY();
        int fig_x = cur_fig.getPosX();
        //out of bounds
        if(fig_x < 0 || fig_y < 0 || fig_x + cur_fig.getWidth() > columns || fig_y + cur_fig.getHeight() > rows)
            return false;
        //overlapping with some block
        for(int r = fig_y; r < fig_y + cur_fig.getHeight(); r++){
            for(int c = fig_x; c < fig_x + cur_fig.getWidth(); c++){
                if(cur_fig.occipiesPosition(r,c) && grid[r][c].isOccupied())
                    return false;
            }
        }
        return true;
    }

    public boolean isGameOver(){
        return !feasiblePlacement();
    }
    public void placeCurrentFigure(){
        updateGrid();
        checkFullRows();
        updateFigures();
    }
    /*
    * Methods for changing the position or the rotation shape of the figure
    * The changes are done only if its a feasible change, i.e. no overlapping with blocks
    * */
    public void rotate(){
        rotateFigureRight();
    }
    public void rotateFigureLeft(){
        cur_fig.rotate();
        if(!feasiblePlacement())
            cur_fig.rotateBack();
    }
    public void rotateFigureRight(){
        cur_fig.rotateBack();
        if(!feasiblePlacement())
            cur_fig.rotate();
    }
    public void moveFigureLeft(){
        if( cur_fig.getPosX() > 0){
            cur_fig.moveLeft();
            if(!feasiblePlacement())
                cur_fig.moveRight();
        }
    }
    public void moveFigureRight(){
        if( cur_fig.getPosX() < columns - 1){
            cur_fig.moveRight();
            if(!feasiblePlacement())
                cur_fig.moveLeft();
        }
    }
    public boolean moveFigureDown(){
        if(cur_fig.getPosY() <= 0)
            return false;
        cur_fig.moveDown();
        boolean feasible = feasiblePlacement();
        if(!feasible)
            cur_fig.moveUp();
        return feasible;
    }

    public boolean isOccupiedAt(int r, int c){
        if(r > rows || c > columns || r < 0 || c < 0)
            throw new IndexOutOfBoundsException();
        return grid[r][c].isOccupied() || cur_fig.occipiesPosition(r,c);
    }
    public int colorAt(int r, int c){
        if(r > rows || c > columns || r < 0 || c < 0)
            throw new IndexOutOfBoundsException();
        if(cur_fig.occipiesPosition(r,c))
            return cur_fig.getColor();
        else
            return grid[r][c].getColor();
    }
    public int getScore(){
        return score;
    }
    public int getNextFigureColor(){
        return next_fig.getColor();
    }
    public int getNextFigureXDim(){
        return next_fig.getWidth();
    }
    public int getNextFigureYDim(){
        return next_fig.getHeight();
    }
    public boolean nextFigureSetAt(int r, int c){
        return next_fig.occipiesLocalPosition(r,c);
    }
}