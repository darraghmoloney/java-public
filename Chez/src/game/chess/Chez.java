package game.chess;

import java.util.ArrayList;

import java.util.Scanner;

public class Chez {

    Piece[][] gameBoard;
    boolean checkmated;
    int moveCount = 1;
    Color currentPlayerColor = Color.WHITE;

    //TODO: record moves made using standard notation.
    ArrayList<String> moveList = new ArrayList<>();
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

        showBoard();

        boolean gameOver = false;

        while (!gameOver && !checkmated) {


            int moveListCount = 0;

            for (String s : moveList) {
                if (moveListCount % 2 == 0) {
                    System.out.print((moveListCount / 2 + 1) + ". ");
                }
                System.out.print(s + " ");
                if (moveListCount % 2 == 1) {
                    System.out.print("  ");
                }
                ++moveListCount;
            }

            System.out.println();

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
            String thisMoveStr = "";

            if (!Piece.outOfBounds(pieceRow) && !Piece.outOfBounds(pieceCol)) {

                if (gameBoard[pieceRow][pieceCol] == null) {
                    System.out.println("no piece found at " + pieceRow + "," + pieceCol);
                    continue;
                }

                if (gameBoard[pieceRow][pieceCol].COLOR != currentPlayerColor) {
                    System.out.println("choose piece to move for " + currentPlayerColor);
                    continue;
                }

                if (!(gameBoard[pieceRow][pieceCol] instanceof Pawn)) {
                    thisMoveStr += gameBoard[pieceRow][pieceCol].SHORT_NAME + "";
                }

            }

            if (!Piece.outOfBounds(moveRow) && !Piece.outOfBounds(moveCol)) {
                if (gameBoard[moveRow][moveCol] instanceof King) {
                    attackingKing = true;
                }
            }

            int[] moveRowAndCol = {moveRow, moveCol};
            boolean attackAttempt = gameBoard[moveRow][moveCol] != null;

            Piece chosenPiece = gameBoard[pieceRow][pieceCol];
            boolean validMove = chosenPiece.move(gameBoard, moveRowAndCol, points);

            if (validMove) {

                ++moveCount;

                if (attackAttempt) {
                    if (chosenPiece instanceof Pawn) {
                        thisMoveStr += rowChar;
                    }
                    thisMoveStr += "x";
                }
                thisMoveStr += moveStr;

                if (chosenPiece instanceof Pawn && ((Pawn)chosenPiece).enPassantCapture) {
                    thisMoveStr =  rowChar + "x" + moveStr + "e.p.";
                }


                if (chosenPiece instanceof Rook && ((Rook)chosenPiece).performedCastle) {
                    thisMoveStr = "0-0";
                    if (((Rook)chosenPiece).queenSideCastle) {
                        thisMoveStr += "-0";
                    }
                }


                if (attackingKing) { //valid attack on king -> checkmate.
                    System.out.println("checkmate");
                    System.out.println(currentPlayerColor + " wins");
                    thisMoveStr += "#";
                    gameOver = true;
                } else {

                    King wKing = (King) gameBoard[King.wKingLoc[0]][King.wKingLoc[1]];
                    King bKing = (King) gameBoard[King.bKingLoc[0]][King.bKingLoc[1]];

                    if (wKing.isInCheck(gameBoard)) {
                        System.out.println("check for w. king");
                        thisMoveStr += "+";
                    }

                    if (bKing.isInCheck(gameBoard)) {
                        System.out.println("check for b. king");
                        thisMoveStr += "+";
                    }
                }

                moveList.add(thisMoveStr);

                currentPlayerColor = currentPlayerColor == Color.WHITE ? Color.BLACK : Color.WHITE;

            }

            showBoard();

        }

    }

    private void showBoard() {

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
            System.out.print((8 - row) + "|"); //countdown rows from 8.

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
            checkmated = true; //confirm game over if king was captured en passant, etc. (very unlikely, but...)
        }

    }

}
