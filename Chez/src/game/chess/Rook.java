package game.chess;

import java.util.Scanner;

class Rook extends Piece {

    Rook(Color color) {
        super(color, "Rook");
    }

    Rook(Color color, int row, int col) {
        super(color, "Rook", row, col);
    }


    @Override
    boolean move(Piece[][] gameBoard, int newRow, int newCol) {

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        //can only change the col OR the row, NOT BOTH. straight lines only.
        if ((currentCol - newCol) != 0 && (currentRow - newRow) != 0) {
            System.out.println("rooks only move in straight lines");
            return false;
        }

        //horizontal move
        if (currentCol - newCol != 0) {
            System.out.println("horizontal");
            int colChange = Math.abs(currentCol - newCol); //number of spaces to move.
            int parity = (currentCol < newCol) ? 1 : -1;

            //castling check.
            if (newCol + parity >= 0 && newCol + parity < 8 &&
                    gameBoard[currentRow][newCol + parity] != null &&
                    gameBoard[currentRow][newCol + parity] instanceof King) {

                Piece kingPiece = gameBoard[currentRow][newCol + parity];

                if (kingPiece.COLOR != ENEMY_COLOR &&
                        (COLOR == Color.WHITE && currentRow == 7 ||
                                COLOR == Color.BLACK && currentRow == 0)) {

                    if (timesMoved == 0 && kingPiece.timesMoved == 0) {

                        int checkCol = currentCol + parity;

                        boolean freePathToNewSquare = true;

                        //check squares are free between both.
                        for (int i = 0; i < colChange; ++i) {
                            if (gameBoard[currentRow][checkCol] != null) {
                                freePathToNewSquare = false;
                            }
                            checkCol += parity;
                        }


                        if (freePathToNewSquare) {
                            System.out.print("do you want to castle? (y/n): ");

                            Scanner sc = new Scanner(System.in);
                            String castleInput = sc.next();

                            if (castleInput.length() > 0 && castleInput.toLowerCase().charAt(0) == 'y') {

                                //move rook.
                                gameBoard[currentRow][currentCol] = null;
                                currentCol = newCol;
                                gameBoard[currentRow][currentCol] = this;

                                //move king.
                                gameBoard[currentRow][newCol + parity] = null;
                                kingPiece.currentCol = newCol - parity;
                                gameBoard[currentRow][kingPiece.currentCol] = kingPiece;

                                ++timesMoved;
                                ++kingPiece.timesMoved;
                                return true;

                            }
                        }
                    }
                }
            }

            int checkCol = currentCol + parity;

            //checking square by square until the spot before.
            for (int i = 0; i < (colChange - 1); ++i) {
                if (gameBoard[currentRow][checkCol] != null) { //blocked.
                    System.out.println("can't move - blocked");
                    return false;
                }
                checkCol += parity;
            }

            //checking actual spot for attacking etc.
            if (gameBoard[currentRow][newCol] == null) {

                gameBoard[currentRow][currentCol] = null;
                currentCol = newCol;
                gameBoard[currentRow][currentCol] = this;
                ++timesMoved;
                return true;

            }

            if (gameBoard[currentRow][newCol].COLOR == ENEMY_COLOR) {

                gameBoard[currentRow][newCol].captured = true;
                gameBoard[currentRow][currentCol] = null;
                currentCol = newCol;
                gameBoard[currentRow][currentCol] = this;
                ++timesMoved;
                return true;

            }

            return false;

        }

        //normal move - vertical
        if (currentRow - newRow != 0) {

            int rowChange = Math.abs(currentRow - newRow);
            int parity = (currentRow < newRow) ? 1 : -1;

            int checkRow = currentRow + parity;

            for (int i = 0; i < (rowChange - 1); ++i) {
                if (gameBoard[checkRow][currentCol] != null) {
                    System.out.println(checkRow + " " + currentCol + " blocked");
                    return false;
                }
                checkRow += parity;
            }

            if (gameBoard[checkRow][currentCol] == null) {

                gameBoard[currentRow][currentCol] = null;
                System.out.println(currentRow + "," + currentCol + ": " + gameBoard[currentRow][currentCol]);

                currentRow = newRow;
                gameBoard[currentRow][currentCol] = this;
                System.out.println(currentRow + "," + currentCol + ": " + gameBoard[currentRow][currentCol]);
                ++timesMoved;
                return true;
            }

            if (gameBoard[checkRow][currentCol].COLOR == ENEMY_COLOR) {

                gameBoard[checkRow][currentCol].captured = true;

                gameBoard[currentRow][currentCol] = null;
                currentRow = newRow;
                gameBoard[currentRow][currentCol] = this;
                ++timesMoved;
                return true;
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

}
