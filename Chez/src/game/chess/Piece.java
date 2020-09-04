package game.chess;


enum Color {
    WHITE,
    BLACK
}

abstract class Piece {

    final Color COLOR;
    final Color ENEMY_COLOR;
    final String ICON;
    final String NAME;
    final String SHORT_NAME;
    final int VALUE;

    int currentRow;
    int currentCol;
    int timesMoved = 0;
    boolean captured = false;


    Piece(Color color, String name) {

        this.COLOR = color;
        this.NAME = name;
        this.SHORT_NAME = getShortName();
        this.VALUE = getPointsValue();
        this.ICON = getIcon();
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


    abstract boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points);

    protected static boolean outOfBounds(int rowOrCol) {
        return rowOrCol < 0 || rowOrCol >= 8;
    }

    boolean checkClearPath(Piece[][] gameBoard, int newRow, int newCol) {

        int rowChange = Math.abs(currentRow - newRow);
        int colChange = Math.abs(currentCol - newCol);

        int moves = Math.max(rowChange, colChange); //works if one is 0, or both same (diagonal)

        int rowParity = 0; //change on each iteration. rowDeltaT might be a better name ?
        int colParity = 0;

        if (rowChange > 0) {
            rowParity = currentRow < newRow ? 1 : -1;
        }

        if (colChange > 0) {
            colParity = currentCol < newCol ? 1 : -1;
        }

        int checkRow = currentRow + rowParity;
        int checkCol = currentCol + colParity;

        for (int i = 0; i < (moves - 1); ++i) { //check to square just before final spot.
            if (gameBoard[checkRow][checkCol] != null) { //blocked.
                System.out.println("can't move - blocked");
                return false;
            }
            checkRow += rowParity;
            checkCol += colParity;

        }

        return true;
    }

    abstract String getShortName();

    abstract int getPointsValue();

    abstract String getIcon();

    @Override
    public String toString() {
        String colorStr = this.COLOR == Color.WHITE ? "w" : "b";
        return SHORT_NAME + colorStr;
    }

}


