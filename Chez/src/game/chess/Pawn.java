package game.chess;

import java.util.Scanner;

class Pawn extends Piece {

    final static int WHITE_HOME_ROW = 7;
    final static int BLACK_HOME_ROW = 0;

    boolean enPassantCapture; //for game record notation

    Pawn(Color color, int row, int col) {
        super(color, "Pawn", row, col);
    }


    @Override
    public boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

        enPassantCapture = false;

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            return false;
        }

        if (Math.abs(currentRow - newRow) < 1 || Math.abs(currentRow - newRow) > 2) {
            return false;
        }

        if (Math.abs(currentRow - newRow) == 2 && timesMoved != 0) {
            System.out.println("can only move 2 squares on first move");
            return false;
        }

        if (COLOR == Color.WHITE && newRow >= currentRow ||
                COLOR == Color.BLACK && newRow <= currentRow
        ) {
            return false;
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
        }

        int enemyHomeIndex = COLOR == Color.WHITE ? BLACK_HOME_ROW : WHITE_HOME_ROW;

        if (newCol == currentCol) { //straight line progression

            if (gameBoard[newRow][newCol] != null) {
                System.out.println("can't move pawn - blocked");
                return false;
            }

            gameBoard[currentRow][currentCol] = null;


            //convert to queen at enemy home row.
            //NB player can technically convert pawn to ANY piece, but the queen is the most powerful so why
            //would anyone choose a different one?
            if (newRow == enemyHomeIndex) {
                Piece newQueen = new Queen(this.COLOR, newRow, newCol);
                gameBoard[newRow][newCol] = newQueen;

                newQueen.timesMoved = timesMoved;
                return true;
            }

            currentRow = newRow;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;

        }

        //attacking to board left / right.
        if (newCol == currentCol - 1 || newCol == currentCol + 1) {

            Piece attackedPiece = null;

            if (this.COLOR == Color.WHITE && newRow != currentRow - 1 ||
                    this.COLOR == Color.BLACK && newRow != currentRow + 1) {
                return false;
            }

            if (gameBoard[newRow][newCol] == null) {
                //en passant.
                if (gameBoard[currentRow][newCol] != null &&
                        gameBoard[currentRow][newCol] instanceof Pawn &&
                        gameBoard[currentRow][newCol].COLOR == ENEMY_COLOR) {

                    attackedPiece = gameBoard[currentRow][newCol];
                    enPassantCapture = true;

                }

            } else if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {
                attackedPiece = gameBoard[newRow][newCol];
            }

            if (attackedPiece == null) {
                return false;
            }

            attackedPiece.captured = true;
            gameBoard[attackedPiece.currentRow][attackedPiece.currentCol] = null;

            if (COLOR == Color.WHITE) {
                points[0] += attackedPiece.VALUE;
            } else {
                points[1] += attackedPiece.VALUE;
            }

            gameBoard[currentRow][currentCol] = null;

            //convert to queen if enemy home row
            if (newRow == enemyHomeIndex) {
                Piece newQueen = new Queen(this.COLOR, newRow, newCol);
                gameBoard[newRow][newCol] = newQueen;

                newQueen.timesMoved = timesMoved;
                return true;
            }


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
        return " ";
    }

    @Override
    int getPointsValue() {
        return 1;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♟︎" : "♙";
    }

}