package game.chess;

class Queen extends Piece {

    Queen(Color color) {
        super(color, "Queen");
    }

    Queen(Color color, int row, int col) {
        super(color, "Queen", row, col);
    }

    @Override
    boolean move(Piece[][] gameBoard, int newRow, int newCol) {

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

        int moves = Math.max(rowChange, colChange);

        int rowParity = 0;
        int colParity = 0;

        if (currentRow < newRow) {
            rowParity = 1;
        } else if (currentRow > newRow) {
            rowParity = -1;
        }

        if (currentCol < newCol) {
            colParity = 1;
        } else if (currentCol > newCol) {
            colParity = -1;
        }

        int checkRow = currentRow + rowParity;
        int checkCol = currentCol + colParity;


        //blocking check.
        for (int i = 0; i < (moves - 1); ++i) {

            if (gameBoard[checkRow][checkCol] != null) {
                System.out.println("move blocked at " + checkRow + "," + checkCol);
                return false;
            }

            checkRow += rowParity;
            checkCol += colParity;

        }


        //final spot check.
        if (gameBoard[checkRow][checkCol] == null) {
            gameBoard[currentRow][currentCol] = null;
            currentRow = checkRow;
            currentCol = checkCol;
            gameBoard[currentRow][currentCol] = this;
            return true;
        }

        //attacking
        if (gameBoard[checkRow][checkCol].COLOR == ENEMY_COLOR) {
            System.out.println("attacking" + checkRow + "," + checkCol);
            gameBoard[checkRow][checkCol].captured = true;

            System.out.println(currentRow + "," + currentCol);

            gameBoard[currentRow][currentCol] = null;
            currentRow = checkRow;
            currentCol = checkCol;
            gameBoard[currentRow][currentCol] = this;
            return true;

        }


        return false; //if path is unblocked but own piece occupies that spot, etc.
    }

    @Override
    String getShortName() {
        return "Q";
    }

    @Override
    int getPointsValue() {
        return 9;
    }
}
