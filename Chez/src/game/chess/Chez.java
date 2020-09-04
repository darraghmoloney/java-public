package game.chess;

import java.util.ArrayList;
import java.util.Scanner;

public class Chez {

    Piece[][] gameBoard;
    boolean checkmated;

    //TODO: record moves made using standard notation.
    static ArrayList<String> moveList = new ArrayList<>();
    int[] points = new int[2];

    public Chez() {

        gameBoard = new Piece[8][8];

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
        gameBoard[0][2] = new Bishop(Color.BLACK, 0, 2);
        gameBoard[0][5] = new Bishop(Color.BLACK, 0, 5);
        gameBoard[7][2] = new Bishop(Color.WHITE, 7, 2);
        gameBoard[7][5] = new Bishop(Color.WHITE, 7, 5);

        //queens
        gameBoard[0][3] = new Queen(Color.BLACK, 0, 3);
        gameBoard[7][3] = new Queen(Color.WHITE, 7, 3);

        //kings
        gameBoard[0][4] = new King(Color.BLACK, 0, 4);
        gameBoard[7][4] = new King(Color.WHITE, 7, 4);

    }

    public void play() {

        showBoard(gameBoard);

        boolean gameOver = false;

        while (!gameOver && !checkmated) {
            System.out.print("[" + points[0] + ":" + points[1] + "] enter move (e.g. 'a2 a3'), q to quit: ");

            Scanner sc = new Scanner(System.in);
            String pieceStr = sc.next();
            char rowChar = pieceStr.charAt(0);

            if (rowChar == 'q') {
                break;
            }

            if ((int) rowChar < 'a' || (int) rowChar > 'h') continue;
            if (pieceStr.length() < 2) continue;

            int pieceCol = rowChar - 'a';
            int pieceRow;

            try {
                pieceRow = 8 - Integer.parseInt(pieceStr.substring(1, 2)); //invert numbers as chess grid is displayed upside-down
            } catch (NumberFormatException nfe) {
                continue;
            }

            String moveStr = sc.next();
            char moveChar = moveStr.charAt(0);

            if ((int) moveChar < 'a' || (int) moveChar > 'h') continue;
            if (moveStr.length() < 2) continue;

            int moveCol = moveChar - 'a';
            int moveRow;

            try {
                moveRow = 8 - Integer.parseInt(moveStr.substring(1, 2));
            } catch (NumberFormatException nfe) {
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

            if (!Piece.outOfBounds(moveRow) && !Piece.outOfBounds(moveCol)) {
                if (gameBoard[moveRow][moveCol] instanceof King) {
                    attackingKing = true;
                }
            }

            if (gameBoard[pieceRow][pieceCol] == null) {
                System.out.println("no piece found at " + pieceRow + "," + pieceCol);
                continue;
            }

            int[] moveRowAndCol = {moveRow, moveCol};

            boolean validMove = gameBoard[pieceRow][pieceCol].move(gameBoard, moveRowAndCol, points);

            if (attackingKing && validMove) {
                System.out.println("checkmate");
                System.out.println((playerColor == Color.WHITE ? "white" : "black") + " wins");
                gameOver = true;
            }

            showBoard(gameBoard);
        }

    }

    private void showBoard(Piece[][] gameBoard) {

        String blackSquare = " ";
//        blackSquare = "■";
        String whiteSquare = "-";
//        whiteSquare = "□";

        System.out.print("   ");

        //a to h chars at the top of the board.
        for (int i = 0; i < 8; ++i) {
            System.out.print("_" + (char) ('a' + i) + "_");
        }
        System.out.println();

        int kingsCount = 0;

        for (int row = 0; row < 8; ++row) {
            System.out.print((8 - row) + "| "); //countdown rows from 8.

            for (int col = 0; col < 8; ++col) {
                Piece square = gameBoard[row][col];

                if (square == null) {
                    if (row % 2 != col % 2) { //odd - even squares are different.
                        System.out.print(" " + blackSquare + " ");
                    } else {
                        System.out.print(" " + whiteSquare + " ");
                    }
                    continue;
                }

                System.out.print(" " + square.ICON + " ");

                if (square instanceof King) {
                    ++kingsCount;
                }

            }
            System.out.println();
        }

        if (kingsCount < 2) {
            checkmated = true; //confirm gameover if king was captured en passant, etc. (very unlikely, but...)
        }

    }

}
