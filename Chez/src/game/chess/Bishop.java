package game.chess;

class Bishop extends Piece {


    Bishop(Color color, int row, int col) {
        super(color, "Bishop", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        //check for same place - no movement, or straight-up / across movement.
        if (newRow == currentRow || newCol == currentCol) {
            return false;
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
        }

        int totalRowChange = Math.abs(currentRow - newRow);
        int totalColChange = Math.abs(currentCol - newCol);

        //as Bishop moves diagonally in straight lines, both row & col must change at 1:1 rate.
        if (totalRowChange != totalColChange) {
            return false;
        }

        if (!checkClearPath(gameBoard, newRow, newCol)) {
            return false;
        }

        //attacking
        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

            gameBoard[newRow][newCol].captured = true;

            if (COLOR == Color.WHITE) {
                points[0] += gameBoard[newRow][newCol].VALUE;
            } else {
                points[1] += gameBoard[newRow][newCol].VALUE;
            }

        }

        //update board
        gameBoard[currentRow][currentCol] = null;
        currentRow = newRow;
        currentCol = newCol;
        gameBoard[currentRow][currentCol] = this;

        ++timesMoved;
        return true;

    }

    @Override
    String getShortName() {
        return "B";
    }

    @Override
    int getPointsValue() {
        return 3;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♝" : "♗";
    }
}
