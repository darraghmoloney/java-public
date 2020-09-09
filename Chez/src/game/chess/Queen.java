package game.chess;

class Queen extends Piece {


    Queen(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Queen", row, col, gameBoard);
    }

    @Override
    boolean move(int row, int col, int[] points) {

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            return false;
        }

        int rowChange = Math.abs(currentRow - row); //absolute value as may move "back" in one of the directions
        int colChange = Math.abs(currentCol - col);

        if (rowChange != 0 && colChange != 0) { //diagonal
            if (rowChange != colChange) { //asymmetric change not possible
                System.out.println("straight-line diagonal moves only");
                return false;
            }
        }

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) { //same color piece in that spot
            return false;
        }

        if (isBlockedPath(row, col)) {
            return false;
        }

        //attacking
        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == ENEMY_COLOR) {

            gameBoard[row][col].captured = true;

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
