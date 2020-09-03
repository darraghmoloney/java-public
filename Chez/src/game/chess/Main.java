package game.chess;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here

        Piece[][] gameBoard = new Piece[8][8];

        //pawns
        for (int square = 0; square < 8; ++square) {
            gameBoard[1][square] = new Pawn(Color.BLACK, 1, square);

            gameBoard[6][square] = new Pawn(Color.WHITE, 6, square);
        }

        //rooks
        gameBoard[0][0] = new Rook(Color.BLACK, 0, 0);
        gameBoard[0][7] = new Rook(Color.BLACK, 0, 7);

        gameBoard[7][0] = new Rook(Color.WHITE, 7, 0);
        gameBoard[7][7] = new Rook(Color.WHITE, 7, 7);

        //knights
        gameBoard[0][1] = new Knight(Color.BLACK, 0, 1);
        gameBoard[0][6] = new Knight(Color.BLACK, 0, 6);

        gameBoard[7][1] = new Knight(Color.WHITE, 7, 1);
        gameBoard[7][6] = new Knight(Color.WHITE, 7, 6);

        //bishops
        gameBoard[0][2] = new Bishop(Color.BLACK, 0 , 2);
        gameBoard[0][5] = new Bishop(Color.BLACK, 0, 5);

        gameBoard[7][2] = new Bishop(Color.WHITE, 7 , 2);
        gameBoard[7][5] = new Bishop(Color.WHITE, 7, 5);

        //queens
        gameBoard[0][3] = new Queen(Color.BLACK, 0, 3);

        gameBoard[7][3] = new Queen(Color.WHITE, 7, 3);

        //kings
        gameBoard[0][4] = new King(Color.BLACK, 0 ,4);

        gameBoard[7][4] = new King(Color.WHITE, 7, 4);



        showBoard(gameBoard);

        boolean gameOver = false;

        while (!gameOver && !Piece.checkmated) {

            System.out.print("enter move WITHOUT piece notation (e.g. 'a2 a3'), q to quit: ");

            Scanner sc = new Scanner(System.in);

            String pieceStr = sc.next();
            char rowChar = pieceStr.charAt(0);

            if (rowChar == 'q') {
                break;
            }

            if ( (int) rowChar < 'a' || (int) rowChar > 'h' ) continue;
            if (pieceStr.length() < 2) continue;

            int pieceCol = rowChar - 'a';
            int pieceRow = -1;

            try {
                pieceRow = 8 - Integer.parseInt(pieceStr.substring(1, 2)); //invert numbers as chess grid is displayed upside-down
            }
            catch (NumberFormatException nfe) {
                continue;
            }

            String moveStr = sc.next();
            char moveChar = moveStr.charAt(0);

            if ( (int) moveChar < 'a' || (int) moveChar > 'h' ) continue;
            if (moveStr.length() < 2) continue;

            int moveCol = moveChar - 'a';
            int moveRow = -1;

            try {
                moveRow = 8 - Integer.parseInt(moveStr.substring(1, 2));
            }
            catch (NumberFormatException nfe) {
                continue;
            }


            System.out.println();

            boolean attackingKing = false;

            Color playerColor = null;

            if (!Piece.outOfBounds(pieceRow) && !Piece.outOfBounds(pieceCol)) {
                if (gameBoard[pieceRow][pieceCol] != null) {
                    playerColor = gameBoard[pieceRow][pieceCol].COLOR;
                }
            }

            if (!Piece.outOfBounds(moveRow) && ! Piece.outOfBounds(moveCol)) {
                if (gameBoard[moveRow][moveCol] instanceof King) {
                    attackingKing = true;
                }
            }

            if (gameBoard[pieceRow][pieceCol] == null) {
                System.out.println("no piece found at " + pieceRow + "," + pieceCol);
                continue;
            }


            boolean validMove = gameBoard[pieceRow][pieceCol].move(gameBoard, moveRow, moveCol);

            if (attackingKing && validMove) {
                System.out.println("checkmate");
                System.out.println((playerColor == Color.WHITE ? "white" : "black") + " wins");
                gameOver = true;
            }

            showBoard(gameBoard);
        }

    }

    public static void showBoard(Piece[][] gameBoard) {

        String blackSquare = "''";
        String whiteSquare = "--";

        System.out.print("   ");

        for (int i = 0; i < 8; ++i) {
            System.out.print("_" + (char) ('a' + i) + "_");
        }
        System.out.println();

        int rowNum = 8;

        int kingsCount = 0;

        for (Piece[] row : gameBoard) {

            System.out.print(rowNum-- + "| ");

            int colNum = -1;

            for (Piece square : row) {
                ++colNum;
                if (square == null) {

                    if ( (rowNum + 1) % 2 == 0 && colNum % 2 == 1 ||
                            (rowNum + 1) % 2 == 1 && colNum % 2 == 0)
                             {
                        System.out.print(blackSquare + " ");
                    } else {
                        System.out.print(whiteSquare + " ");
                    }
                    continue;
                }
                System.out.print(square + " ");

                if (square instanceof King) {
                    ++kingsCount;
                }

            }
            System.out.println();
        }

        if (kingsCount < 2) {
            Piece.checkmated = true; //confirm gameover if king was captured en passant, etc. (very unlikely, but...)
        }

    }
}
