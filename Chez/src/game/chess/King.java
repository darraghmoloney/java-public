package game.chess;

class King extends Piece {

    King(Color color) { super(color, "King"); }
    King(Color color, int row, int col) {super(color, "King", row, col); }

    @Override
    boolean move(Piece[][] gameBoard, int newRow, int newCol) {

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        int rowChange = Math.abs(currentRow - newRow);
        int colChange = Math.abs(currentCol - newCol);

        if (rowChange > 1 || colChange > 1) {
            System.out.println("king can only move one step at a time");
            return false;
        }

        if (gameBoard[newRow][newCol] == null) {
            gameBoard[currentRow][currentCol] = null;
            currentRow = newRow;
            currentCol = newCol;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;
        }

        if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {
            gameBoard[newRow][newCol].captured = true;
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
        return "K";
    }

    @Override
    int getPointsValue() {
        return 200; //in reality, King is both infinite & low value as capture ends the game but it has some limited attacking & defensive qualities.
    }
}
