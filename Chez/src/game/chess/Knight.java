package game.chess;

class Knight extends Piece {


    Knight(Color color, int row, int col) {
        super(color, "Knight", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

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

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
        }

        //NB knights can jump over other pieces, so blocking checks are not required.

        //attacking move
        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

            gameBoard[currentRow][currentCol].captured = true;

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
        return "N";
    }

    @Override
    int getPointsValue() {
        return 3;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♞" : "♘";
    }
}
