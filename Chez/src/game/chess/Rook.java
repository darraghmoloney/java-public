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

        if (!checkClearPath(gameBoard, newRow, newCol)) {
            return false;
        }


        //castling check
        if (currentCol - newCol != 0 && checkCastling(gameBoard, newCol)) {
            return true;
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

    private boolean checkCastling(Piece[][] gameBoard, int newCol) {

        if ((currentCol == 0 && newCol == 3 || currentCol == 7 && newCol == 5) &&
                gameBoard[currentRow][4] != null &&
                gameBoard[currentRow][4] instanceof King
        ) {

            Piece kingPiece = gameBoard[currentRow][4];

            if (kingPiece.COLOR != ENEMY_COLOR &&
                    (COLOR == Color.WHITE && currentRow == 7 || COLOR == Color.BLACK && currentRow == 0)) {

                if (timesMoved == 0 && kingPiece.timesMoved == 0) {

                    System.out.print("do you want to castle? (y/n): ");

                    Scanner sc = new Scanner(System.in);
                    String castleInput = sc.next();

                    if (castleInput.length() > 0 && castleInput.toLowerCase().charAt(0) == 'y') {

                        int colParity = currentCol < newCol ? 1 : -1;

                        //move rook.
                        gameBoard[currentRow][currentCol] = null;
                        currentCol = newCol;
                        gameBoard[currentRow][currentCol] = this;

                        //move king.
                        gameBoard[currentRow][4] = null;
                        kingPiece.currentCol = newCol - colParity;
                        gameBoard[currentRow][kingPiece.currentCol] = kingPiece;

                        ++timesMoved;
                        ++kingPiece.timesMoved;
                        return true;

                    }
                }
            }
        }

        return false;
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
