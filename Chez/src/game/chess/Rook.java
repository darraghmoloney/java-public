package game.chess;

import java.util.Scanner;

class Rook extends Piece {

    Rook(Color color, int row, int col) {
        super(color, "Rook", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        //can only change the col OR the row, NOT BOTH. straight lines only.
        if ((currentCol - newCol) != 0 && (currentRow - newRow) != 0) {
            System.out.println("rooks only move in straight lines");
            return false;
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
        }

        if (isBlockedPath(gameBoard, newRow, newCol)) {
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
        return "R";
    }

    @Override
    int getPointsValue() {
        return 5;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♜" : "♖";
    }

}
