package game.chess;

import java.util.ArrayList;
import java.util.Scanner;

class Pawn extends Piece {

    final static int WHITE_HOME_ROW = 7;
    final static int BLACK_HOME_ROW = 0;

    private boolean enPassantCapturing; //for game record notation
    private boolean promoted;

    private String pieceToPromoteTo;

    Pawn(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Pawn", row, col, gameBoard);
    }

//    Pawn(Color color, String alphaStr, Piece[][] gameBoard) {
//        super(color, "Pawn", alphaStr, gameBoard);
//    }

    @Override
    public boolean move(int row, int col, int[] points) {

        enPassantCapturing = false;
        promoted = false;

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            return false;
        }

        if (Math.abs(currentRow - row) < 1 || Math.abs(currentRow - row) > 2) {
            return false;
        }

        if (Math.abs(currentRow - row) == 2 && timesMoved != 0) {
            System.out.println("can only move 2 squares on first move");
            return false;
        }

        if (COLOR == Color.WHITE && row >= currentRow ||
                COLOR == Color.BLACK && row <= currentRow
        ) {
            return false;
        }

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) {
            return false;
        }

        int enemyHomeIndex = COLOR == Color.WHITE ? BLACK_HOME_ROW : WHITE_HOME_ROW;

        if (col == currentCol) { //straight line progression

            if (gameBoard[row][col] != null) {
                System.out.println("can't move pawn - blocked");
                return false;
            }

        }

        //attacking to board left / right.
        if (col == currentCol - 1 || col == currentCol + 1) {


            Piece attackedPiece = null;

            if (this.COLOR == Color.WHITE && row != currentRow - 1 ||
                    this.COLOR == Color.BLACK && row != currentRow + 1) { //EXACT check. general check of greater row & correct direction already done
                return false;
            }

            if (gameBoard[row][col] == null) {

                //en passant. capture a Pawn if it just moved 2 squares forward from its home row.
                if (gameBoard[currentRow][col] instanceof Pawn && gameBoard[currentRow][col].COLOR == ENEMY_COLOR) {

                    int epCaptureRow = COLOR == Color.BLACK ? 5 : 2; //i.e. the row in front of the pawn's starting row.
                    if (row != epCaptureRow) return false;

                    attackedPiece = gameBoard[currentRow][col];

                    if (attackedPiece.timesMoved > 1) return false;


                    enPassantCapturing = true;

                }

            } else if (gameBoard[row][col].COLOR == ENEMY_COLOR) {
                attackedPiece = gameBoard[row][col];
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

        }

        place(row, col);

        //promote at enemy home row.
        if (row == enemyHomeIndex) {
            promotePawn(row, col);
        }

        return true;

    }

    private void promotePawn(int newRow, int newCol) {
        promoted = true;

        if (pieceToPromoteTo == null) {

            System.out.print("promotion. choose [Q]ueen, k[N]ight, [R]ook, or [B]ishop: ");

            Scanner sc = new Scanner(System.in);
            String choiceStr = sc.next();
            pieceToPromoteTo = "Q";

            if (choiceStr.length() > 0) {
                pieceToPromoteTo = choiceStr.toUpperCase().substring(0, 1);
            }

        }

        Piece newPiece;

        switch (pieceToPromoteTo) {
            case "N":
                newPiece = new Knight(this.COLOR, newRow, newCol, gameBoard);
                break;
            case "R":
                newPiece = new Rook(this.COLOR, newRow, newCol, gameBoard);
                break;
            case "B":
                newPiece = new Bishop(this.COLOR, newRow, newCol, gameBoard);
                break;
            default:
                newPiece = new Queen(this.COLOR, newRow, newCol, gameBoard);
        }

        gameBoard[newRow][newCol] = newPiece;

        newPiece.timesMoved = timesMoved;
    }

    public boolean isEnPassantCapturing() {
        return enPassantCapturing;
    }

    public boolean isPromoted() {
        return promoted;
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

//    String getPieceToPromoteTo() {
//        return pieceToPromoteTo;
//    }

    void setPieceToPromoteTo(String pieceToPromoteTo) {
        this.pieceToPromoteTo = pieceToPromoteTo;
    }

    @Override
    ArrayList<Integer[]> getValidMoves(String lastMoveStr) {

        ArrayList<Integer[]> validMovesList = new ArrayList<>();

        //check double step
        if (timesMoved == 0 && (COLOR == Color.BLACK && currentRow == 1) || (COLOR == Color.WHITE && currentRow == 6)) {

            int checkRow = COLOR == Color.BLACK ? 3 : 4;

            Piece checkPiece = COLOR == Color.BLACK ? gameBoard[checkRow][currentCol] : gameBoard[checkRow][currentCol];


            if (checkPiece == null) {
                Integer[] validMove = {checkRow, currentCol};
                validMovesList.add(validMove);
            }

        }

        int nextRow = COLOR == Color.BLACK ? currentRow + 1 : currentRow - 1;

        //check straight ahead
        if (!Piece.outOfBounds(nextRow) && gameBoard[nextRow][currentCol] == null) {
            Integer[] validMove = {nextRow, currentCol};
            validMovesList.add(validMove);
        }


        //check attack straight left and right, and en passant
        if (!Piece.outOfBounds(nextRow)) {

            int epCaptureRow = COLOR == Color.BLACK ? 5 : 2; //i.e. the row in front of the pawn's starting row.

            //top left
            if (!Piece.outOfBounds(currentCol - 1)) {
                Piece topLeftPiece = gameBoard[nextRow][currentCol - 1];

                if (topLeftPiece != null && topLeftPiece.COLOR == ENEMY_COLOR) {

                    Integer[] validMove = {nextRow, currentCol - 1};
                    validMovesList.add(validMove);

                }

                //en passant
                if (topLeftPiece == null && nextRow == epCaptureRow && lastMoveStr.length() == 2) {

                    if (lastMoveStr.equals(convertRowColToAlphanumeric(currentRow, currentCol -1))) {
                        Piece epLeftPiece = gameBoard[currentRow][currentCol - 1];

                        //some of these checks are technically redundant assuming a correctly formatted move String,
                        //but good to keep for error cases
                        if (epLeftPiece instanceof Pawn && epLeftPiece.COLOR == ENEMY_COLOR && epLeftPiece.timesMoved == 1) {
                            Integer[] validMove = {currentRow, currentCol - 1};
                            validMovesList.add(validMove);
                        }

                    }

                }

            }

            //top right
            if (!Piece.outOfBounds(currentCol + 1)) {
                Piece topRightPiece = gameBoard[nextRow][currentCol + 1];

                if (topRightPiece != null && topRightPiece.COLOR == ENEMY_COLOR) {

                    Integer[] validMove = {nextRow, currentCol + 1};
                    validMovesList.add(validMove);

                }

                //en passant
                if (topRightPiece == null && nextRow == epCaptureRow && lastMoveStr.length() == 2) {

                    if (lastMoveStr.equals(convertRowColToAlphanumeric(currentRow, currentCol +1))) {
                        Piece epRightPiece = gameBoard[currentRow][currentCol + 1];

                        if (epRightPiece instanceof Pawn && epRightPiece.COLOR == ENEMY_COLOR && epRightPiece.timesMoved == 1) {
                            Integer[] validMove = {currentRow, currentCol + 1};
                            validMovesList.add(validMove);
                        }

                    }

                }
            }


        }





        return validMovesList;
    }
}