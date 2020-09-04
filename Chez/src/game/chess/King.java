package game.chess;

class King extends Piece {


    King(Color color, int row, int col) {
        super(color, "King", row, col);
    }

    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

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

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
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
        return "K";
    }

    @Override
    int getPointsValue() {
        return 200; //in reality, King is both infinite & low value as capture ends the game but it has some limited attacking & defensive qualities.
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♚" : "♔";
    }
}
