package game.chess;

class King extends Piece {

    boolean inCheck;
    boolean performedCastle;
    boolean queenSideCastle;

    King(Color color, int row, int col) {
        super(color, "King", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        performedCastle = false;
        queenSideCastle = false;

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
        }

        int rowChange = Math.abs(currentRow - newRow);
        int colChange = Math.abs(currentCol - newCol);

        int homeRow = COLOR == Color.BLACK ? 0 : 7;

        if (rowChange > 1 || (colChange > 1 && currentRow != homeRow)) {
            System.out.println("king can only move one step at a time");
            return false;
        }

        //castling.
        if (colChange == 2) {
            boolean onQueenSide = newCol < 4;
            return checkCastling(gameBoard, onQueenSide);
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

    boolean isInCheck(Piece[][] gameBoard) {
        inCheck = isSquareUnderAttack(gameBoard, currentRow, currentCol, ENEMY_COLOR);
        return inCheck;
    }

    //castling is TECHNICALLY a King move
    //TODO: implement castling in King class
    private boolean checkCastling(Piece[][] gameBoard, boolean queenSide) {

        if (currentCol != 4)
            return false; //col. 4 is king's initial spot. castling requires both king & rook to not have moved
        if (timesMoved > 0) return false;

        int homeRow = COLOR == Color.BLACK ? 0 : 7;

        if (currentRow != homeRow) return false;

        int rookCol = 7; //default to king-side
        int newCol = 6;

        if (queenSide) {
            rookCol = 0;
            newCol = 2;
        }

        Piece rookToMove = gameBoard[currentRow][rookCol];

        if (!(rookToMove instanceof Rook)) return false;
        if (rookToMove.COLOR == ENEMY_COLOR) return false;
        if (rookToMove.timesMoved > 0) return false;

        if (isBlockedPath(gameBoard, currentRow, newCol)) return false;

        int newRookCol = 5;

        if (queenSide) {
            newRookCol = 3;
            queenSideCastle = true;
        }

        //the King is not allowed to land in a spot that results in Check (technically true of all King moves)
        if (isSquareUnderAttack(gameBoard, currentRow, newCol, ENEMY_COLOR)) {
            return false;
        }

        //the King "jumps over" the Rook's final position, but the King may not do so if that square can be attacked
        if (isSquareUnderAttack(gameBoard, currentRow, newRookCol, ENEMY_COLOR)) {
            return false;
        }

        //move king.
        gameBoard[currentRow][currentCol] = null;
        currentCol = newCol;
        gameBoard[currentRow][currentCol] = this;

        //move rook.
        gameBoard[currentRow][rookCol] = null;
        rookToMove.currentCol = newRookCol;
        gameBoard[currentRow][newRookCol] = rookToMove;

        ++timesMoved;
        ++rookToMove.timesMoved;
        performedCastle = true;
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
