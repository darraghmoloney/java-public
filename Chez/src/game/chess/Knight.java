package game.chess;

class Knight extends Piece {

    Knight(Color color) {
        super(color, "Knight");
    }

    Knight(Color color, int row, int col) {
        super(color, "Knight", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int newRow, int newCol) {

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        //must move 1-2 rows up or down
        int rowChange = Math.abs(currentRow - newRow);
        int colChange = Math.abs(currentCol - newCol);

        if (rowChange + colChange != 3) { //2 row or col changes plus 1 of the other for knight movement (L-shape)
            System.out.println("knights must only move in an L-shape");
            return false;
        }

        //valid rowChange is 1 or 2. total changes sum of 3 is checked above,
        //so colChange is also logically evaluated.
        if (rowChange < 1 || rowChange > 2) {
            System.out.println("knights must only move in an L-shape. 2 rows/cols + 1 row/col respectively");
            return false;
        }

        //NB knights can jump over other pieces, so blocking checks are not required.
        //free space move
        if (gameBoard[newRow][newCol] == null) {

            gameBoard[currentRow][currentCol] = null;

            currentRow = newRow;
            currentCol = newCol;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;
        }


        //attacking move
        if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {
            System.out.println("attacking");
            gameBoard[currentRow][currentCol].captured = true;
            gameBoard[currentRow][currentCol] = null;

            currentRow = newRow;
            currentCol = newCol;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;
        }

        return false;
    }

    @Override
    String getShortName() {
        return "N";
    }

    @Override
    int getPointsValue() {
        return 3;
    }
}
