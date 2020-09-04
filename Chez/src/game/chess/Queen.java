package game.chess;

class Queen extends Piece {


    Queen(Color color, int row, int col) {
        super(color, "Queen", row, col);
    }

    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            return false;
        }

        int rowChange = Math.abs(currentRow - newRow); //absolute value as may move "back" in one of the directions
        int colChange = Math.abs(currentCol - newCol);

        if (rowChange != 0 && colChange != 0) { //diagonal
            if (rowChange != colChange) { //asymmetric change not possible
                System.out.println("straight-line diagonal moves only");
                return false;
            }
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) { //same color piece in that spot
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
        return "Q";
    }

    @Override
    int getPointsValue() {
        return 9;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♛" : "♕";
    }
}
