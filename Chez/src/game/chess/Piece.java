package game.chess;

import java.lang.reflect.Array;
import java.util.ArrayList;

enum Color {
    WHITE,
    BLACK
}

abstract class Piece {

    final Color COLOR;
    final Color ENEMY_COLOR;
    //    final String icon;
    final String NAME;
    final String SHORT_NAME;
    final int VALUE;

    int currentRow;
    int currentCol;
    int timesMoved = 0;
    boolean captured = false;

    //TODO: record moves made using standard notation.
//    final static ArrayList<String> moveList = new ArrayList<>();

    static boolean checkmated;

    Piece(Color color, String name) {

        this.COLOR = color;
        this.NAME = name;
        this.SHORT_NAME = getShortName();
        this.VALUE = getPointsValue();
        this.ENEMY_COLOR = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

    }

    Piece(Color color, String name, int row, int col) {
        this(color, name);
        place(row, col);
    }

    public void place(int row, int col) {
        if (!outOfBounds(row) && !(outOfBounds(col))) {
            currentRow = row;
            currentCol = col;
        }
    }


    abstract boolean move(Piece[][] gameBoard, int newRow, int newCol);

    protected static boolean outOfBounds(int rowOrCol) {
        return rowOrCol < 0 || rowOrCol >= 8;
    }

    abstract String getShortName();

    abstract int getPointsValue();

    @Override
    public String toString() {
        String colorStr = this.COLOR == Color.WHITE ? "w" : "b";
        return SHORT_NAME + colorStr;
    }

}


