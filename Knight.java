package game.chess;

class Knight extends Piece {


    Knight(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Knight", row, col, gameBoard);
    }


    @Override
    boolean move(int row, int col, int[] points) {

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            System.out.println("out of bounds");
            return false;
        }

        //must move 1-2 rows up or down
        int rowChange = Math.abs(currentRow - row);
        int colChange = Math.abs(currentCol - col);

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

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) {
            return false;
        }

        //NB knights can jump over other pieces, so blocking checks are not required.

        //attacking move
        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == ENEMY_COLOR) {

            gameBoard[currentRow][currentCol].captured = true;

            if (COLOR == Color.WHITE) {
                points[0] += gameBoard[row][col].VALUE;
            } else {
                points[1] += gameBoard[row][col].VALUE;
            }

        }

        //update board
        place(row, col);

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
