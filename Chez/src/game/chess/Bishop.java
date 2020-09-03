package game.chess;

class Bishop extends Piece {

    Bishop(Color color) {
        super(color, "Bishop");
    }

    Bishop(Color color, int row, int col) {
        super(color, "Bishop", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int newRow, int newCol) {

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        //check for same place - no movement, or straight-up / across movement.
        if (newRow == currentRow || newCol == currentCol) {
            return false;
        }

        int totalRowChange = Math.abs(currentRow - newRow);
        int totalColChange = Math.abs(currentCol - newCol);

        //as Bishop moves diagonally in straight lines, both row & col must change at 1:1 rate.
        if (totalRowChange != totalColChange) {
            return false;
        }

        int rowParity = currentRow < newRow ? 1 : -1; //the change - down (+) or up (-)
        int colParity = currentCol < newCol ? 1 : -1;

        int checkRow = currentRow + rowParity;
        int checkCol = currentCol + colParity;

        for (int i = 0; i < totalRowChange - 1; ++i) {
            if (gameBoard[checkRow][checkCol] != null) {
                System.out.println("blocked at " + checkRow + "," + checkCol);
                return false;
            }
            checkRow += rowParity;
            checkCol += colParity;
        }

        //check final spot.
        if (gameBoard[checkRow][checkCol] == null) {
            gameBoard[currentRow][currentCol] = null;
            currentRow = checkRow;
            currentCol  = checkCol;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;
        }

        //attacking
        if (gameBoard[checkRow][checkCol].COLOR == ENEMY_COLOR) {
            System.out.println("attacking");
            gameBoard[checkRow][checkCol].captured = true;

            gameBoard[currentRow][currentCol] = null;
            currentRow = checkRow;
            currentCol = checkCol;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;

        }

        return false;
    }

    @Override
    String getShortName() {
        return "B";
    }

    @Override
    int getPointsValue() {
        return 3;
    }
}
