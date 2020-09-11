package game.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Chez {

    private final Piece[][] gameBoard;
    private boolean checkmated;
    private int moveCount = 1;
    private Color currentPlayerColor = Color.WHITE;

    private boolean multiPieceChoice;
    private boolean multiChoiceSameCol;
    private boolean multiChoiceSameRow;

    private final ArrayList<String> moveList = new ArrayList<>(); //moves record
    private final int[] points = new int[2];

    private int[] bKingLoc = {0, 4}; //store king locations to easily verify if king is in check or not
    private int[] wKingLoc = {7, 4};

    public Chez() {

        gameBoard = new Piece[8][8];

        //pawns
        for (int square = 0; square < 8; ++square) {
            gameBoard[1][square] = new Pawn(Color.BLACK, 1, square, gameBoard);
            gameBoard[6][square] = new Pawn(Color.WHITE, 6, square, gameBoard);
        }

        //rooks
        gameBoard[0][0] = new Rook(Color.BLACK, 0, 0, gameBoard);
        gameBoard[0][7] = new Rook(Color.BLACK, 0, 7, gameBoard);
        gameBoard[7][0] = new Rook(Color.WHITE, 7, 0, gameBoard);
        gameBoard[7][7] = new Rook(Color.WHITE, 7, 7, gameBoard);

        //knights
        gameBoard[0][1] = new Knight(Color.BLACK, 0, 1, gameBoard);
        gameBoard[0][6] = new Knight(Color.BLACK, 0, 6, gameBoard);
        gameBoard[7][1] = new Knight(Color.WHITE, 7, 1, gameBoard);
        gameBoard[7][6] = new Knight(Color.WHITE, 7, 6, gameBoard);

        //bishops
        gameBoard[0][2] = new Bishop(Color.BLACK, 0, 2, gameBoard);
        gameBoard[0][5] = new Bishop(Color.BLACK, 0, 5, gameBoard);
        gameBoard[7][2] = new Bishop(Color.WHITE, 7, 2, gameBoard);
        gameBoard[7][5] = new Bishop(Color.WHITE, 7, 5, gameBoard);

        //queens
        gameBoard[0][3] = new Queen(Color.BLACK, 0, 3, gameBoard);
        gameBoard[7][3] = new Queen(Color.WHITE, 7, 3, gameBoard);

        //kings
        gameBoard[0][4] = new King(Color.BLACK, 0, 4, gameBoard);
        gameBoard[7][4] = new King(Color.WHITE, 7, 4, gameBoard);

    }

    public void play() {

        showBoard();

        boolean gameOver = false;

        while (!gameOver && !checkmated) {

            printMovesList();

            //TODO: add surrendering.
            //TODO: (maybe) only allow moves that remove check condition if king in check (requires surrender or automatic checkmate if not possible)

            System.out.print("[" + points[0] + ":" + points[1] + "] ");

            Scanner sc = new Scanner(System.in);

            System.out.print("enter a move for " + currentPlayerColor + " (e.g. e4, Nh6, 0-0...), or q to quit: ");
            String moveStr = sc.next();
            String[] moveInfo; //stores start position of piece if provided for disambiguation [0], and the move itself [1]

            if (moveStr.length() > 0 && moveStr.charAt(0) == 'q') {
                break;
            } else {
                //sanitize move str input - for piece choice handling, rather than safety, but it also helps.
                moveInfo = parseNotation(moveStr);
                moveStr = moveInfo[1];
            }

            Piece chosenPiece = findPieceToMove(moveStr, currentPlayerColor, moveInfo[0]);

            //handle king-side castling.
            if (moveStr.equals("0-0") || moveStr.equals("O-O")) {
                if (currentPlayerColor == Color.BLACK) {
                    moveStr = "Kg8";
                } else {
                    moveStr = "Kg1";
                }
            }

            //handle queen-side castling.
            if (moveStr.equals("0-0-0") || moveStr.equals("O-O-O")) {

                if (currentPlayerColor == Color.BLACK) {
                    moveStr = "Kc8";
                } else {
                    moveStr = "Kc1";
                }
            }

            if (chosenPiece == null) {
                System.out.println("no valid piece found to make that move");
                continue;
            }


            String rowColStr = moveStr;
            if (moveStr.length() > 2) rowColStr = rowColStr.substring(1); //location is after the piece short name

            Integer[] moveRowCol = convertAlphanumericToRowCol(rowColStr);


            if (moveRowCol == null) {
                System.out.println("couldn't find that row and column");
                continue;
            }

            int moveRow = moveRowCol[0];
            int moveCol = moveRowCol[1];

            System.out.println();

            boolean attackingKing = false;
            String moveNotation;

            if (!Piece.outOfBounds(moveRow) && !Piece.outOfBounds(moveCol)) {
                if (gameBoard[moveRow][moveCol] instanceof King) {
                    attackingKing = true;
                }
            }

            String pieceStr = chosenPiece.getAlphanumericLoc();

            boolean wKingWasInCheck = ((King) gameBoard[wKingLoc[0]][wKingLoc[1]]).isInCheck();
            boolean bKingWasInCheck = ((King) gameBoard[bKingLoc[0]][bKingLoc[1]]).isInCheck();

            boolean attackAttempt = gameBoard[moveRow][moveCol] != null;

            //for extra disambiguation in notation if necessary
            char chosenPieceCol = chosenPiece.getAlphanumericLoc().charAt(0);
            char chosenPieceRow = chosenPiece.getAlphanumericLoc().charAt(1);

            boolean validMove = chosenPiece.move(moveRow, moveCol, points);

            //after successful move, king will be there
            boolean kingMoved = gameBoard[moveRow][moveCol] instanceof King;

            if (validMove) {

                ++moveCount;

                if (kingMoved) {

                    int[] newKingLoc = {chosenPiece.currentRow, chosenPiece.currentCol};

                    if (currentPlayerColor == Color.WHITE) {
                        wKingLoc = newKingLoc;
                    } else {
                        bKingLoc = newKingLoc;
                    }
                }

                moveNotation = moveStr;

                if (attackAttempt || chosenPiece instanceof Pawn && ((Pawn) chosenPiece).isEnPassantCapturing()) {
                    if (chosenPiece instanceof Pawn) {
                        moveNotation = pieceStr.charAt(0) + "x" + moveStr;

                        if (((Pawn) chosenPiece).isEnPassantCapturing()) {
                            moveNotation += "e.p.";
                        }

                    } else {
                        moveNotation = moveStr.charAt(0) + "x" + moveStr.substring(1);
                    }
                }


                //notation to clarify which piece was chosen in case of multiple options. e.g. Nge7 means the knight at g was moved to e7.
                //an attacking pawn will already record its move from the start col, so it's notation doesn't need to change.
                if (multiPieceChoice && !(chosenPiece instanceof Pawn && (attackAttempt || ((Pawn) chosenPiece).isEnPassantCapturing()))) {

                    String clarifyStr = "" + chosenPieceCol;

                    if (multiChoiceSameCol && !multiChoiceSameRow) {
                        clarifyStr = "" + chosenPieceRow;
                    }

                    if (multiChoiceSameRow && multiChoiceSameCol) {
                        clarifyStr = chosenPieceCol + "" + chosenPieceRow;
                    }

                    moveNotation = moveNotation.charAt(0) + "" + clarifyStr + moveStr.substring(1);
                }


                if (chosenPiece instanceof Pawn && ((Pawn) chosenPiece).isPromoted()) {
                    moveNotation += gameBoard[moveRow][moveCol].SHORT_NAME;
                }

                if (chosenPiece instanceof King && ((King) chosenPiece).isCastled()) {
                    moveNotation = "0-0";
                    if (((King) chosenPiece).isQueenSideCastled()) {
                        moveNotation += "-0";
                    }
                }

                if (attackingKing) { //valid attack on king -> checkmate.

                    checkmated = true;

                } else {

                    King wKing = (King) gameBoard[wKingLoc[0]][wKingLoc[1]];
                    King bKing = (King) gameBoard[bKingLoc[0]][bKingLoc[1]];

                    if (wKing == null || bKing == null) {
                        updateKingPositions();
                        wKing = (King) gameBoard[wKingLoc[0]][wKingLoc[1]];
                        bKing = (King) gameBoard[bKingLoc[0]][bKingLoc[1]];
                    }

                    if (wKing.isInCheck()) {
                        System.out.println("check for w. king");

                        if (wKing.isCheckmated()) {
                            checkmated = true;
                        }

                        if (!wKingWasInCheck && currentPlayerColor == Color.BLACK) {
                            moveNotation += "+";
                        }
                    }

                    if (bKing.isInCheck()) {
                        System.out.println("check for b. king");

                        if (bKing.isCheckmated()) {
                            checkmated = true;
                        }

                        if (!bKingWasInCheck && currentPlayerColor == Color.WHITE) {
                            moveNotation += "+";
                        }
                    }
                }

                if (checkmated) {
                    //if the current move was already noted as check, update it
                    if (moveNotation.charAt(moveNotation.length() - 1) == '+') {
                        moveNotation = moveNotation.substring(0, moveNotation.length() - 1);
                    }
                    moveNotation += "#";
                    System.out.println("CHECKMATE for " + currentPlayerColor);
                    gameOver = true;

                }

                moveList.add(moveNotation);




            }

            showBoard();

            if (checkmated) {
                printMovesList();
            }

            currentPlayerColor = currentPlayerColor == Color.WHITE ? Color.BLACK : Color.WHITE;

        }

    }

    private void showBoard() {

        String blackSquare = "_";
        String whiteSquare = " ";

        System.out.print("   ");


        //a to h chars at the top of the board.
        for (int i = 0; i < 8; ++i) {
            System.out.print("_" + (char) ('a' + i) + "_");
        }
        System.out.println();

        int kingsCount = 0;

        for (int row = 0; row < 8; ++row) {
            System.out.print((8 - row) + " |"); //countdown rows from 8.

            for (int col = 0; col < 8; ++col) {
                Piece square = gameBoard[row][col];

                if (square == null) {
                    if (row % 2 != col % 2) { //odd - even squares are different.
                        System.out.print(" " + blackSquare + "|");
                    } else {
                        System.out.print(" " + whiteSquare + " ");
                    }
                    continue;
                }

                System.out.print(" " + square.ICON + " ");

                if (square instanceof King) {
                    if (square.COLOR == Color.WHITE) {
                        wKingLoc[0] = row; //check & update in case of king movement e.g. castling, etc.
                        wKingLoc[1] = col;
                    } else {
                        bKingLoc[0] = row;
                        bKingLoc[1] = col;
                    }
                    ++kingsCount;
                }

            }
            System.out.println(" | ");
        }

        System.out.println("   --------------------------");


        if (kingsCount < 2) {
            checkmated = true; //confirm game over if king was captured en passant, etc. (very unlikely, but...)
        }

    }

    //change co-ordinates String like "e4" to correct array row & col numbering.
    private static Integer[] convertAlphanumericToRowCol(String alphaStr) {

        char rowChar = alphaStr.charAt(0);

        //error cases
        if (rowChar < 'a' || rowChar > 'h') return null;
        if (alphaStr.length() < 2) return null;

        Integer[] rowCol = new Integer[2];
        int col = rowChar - 'a';
        int row;

        try {
            row = 8 - Integer.parseInt(alphaStr.substring(1, 2)); //invert numbers as chess grid is displayed upside-down
            rowCol[0] = row;
            rowCol[1] = col;
        } catch (NumberFormatException nfe) {
            System.out.println("invalid row number");
            return null;
        }

        return rowCol;
    }

    private Piece findPieceToMove(String movementStr, Color playerColor, String extraMoveInfo) {

        multiPieceChoice = false; //to record move notation correctly when the choice of piece is ambiguous

        //movementStr is like e5 or Nc3 or Qf7 etc.
        char pieceShortName;
        String alphanumericStr;

        //special handling for castling.
        if (movementStr.equals("0-0") || movementStr.equals("0-0-0")) {

            int homeRow = 0;
            if (playerColor == Color.WHITE) {
                homeRow = 7;
            }

            if (gameBoard[homeRow][4] instanceof King) {
                return gameBoard[homeRow][4];
            } else {
                return null;
            }

        }

        if (movementStr.length() == 0) return null;

        if (Character.isUpperCase(movementStr.charAt(0))) { //non-pawn moves note the type of piece to be moved.
            pieceShortName = movementStr.charAt(0);
            alphanumericStr = movementStr.substring(1);
        } else {
            pieceShortName = 'P';
            alphanumericStr = movementStr;
        }

        Integer[] rowAndCol = convertAlphanumericToRowCol(alphanumericStr);

        if (rowAndCol == null || rowAndCol[0] == null || rowAndCol[1] == null) return null;

        int destRow = rowAndCol[0];
        int destCol = rowAndCol[1];

        ArrayList<Piece> pieceChoices = new ArrayList<>();

        //PAWN - 1 or 2 rows behind if normal move, 1 row & 1 col behind if attacking
        if (pieceShortName == 'P') {

            int pawnRowOffset = playerColor == Color.BLACK ? -1 : 1;
            int prevPawnRow = destRow + pawnRowOffset;

            //attacking check.
            if (gameBoard[destRow][destCol] != null && gameBoard[destRow][destCol].COLOR != playerColor) {

                if (!Piece.outOfBounds(destCol - 1)) {

                    Piece leftPiece = gameBoard[prevPawnRow][destCol - 1];

                    if (leftPiece != null && leftPiece.COLOR == playerColor && leftPiece instanceof Pawn) {
                        pieceChoices.add(leftPiece);
                    }

                }

                if (!Piece.outOfBounds(destCol + 1)) {

                    Piece rightPiece = gameBoard[prevPawnRow][destCol + 1];

                    if (rightPiece != null && rightPiece.COLOR == playerColor && rightPiece instanceof Pawn) {
                        pieceChoices.add(rightPiece);
                    }

                }

            }

            Piece epPiece = gameBoard[destRow + pawnRowOffset][destCol];

            //attacking en passant.
            if (gameBoard[destRow][destCol] == null && epPiece instanceof Pawn && epPiece.COLOR != playerColor) {

                String previousMoveStr = null;

                if (moveList.size() > 0) {
                    previousMoveStr = moveList.get(moveList.size() - 1);
                }

                if (previousMoveStr != null && previousMoveStr.length() == 2) { //length 2 move string implies pawn. (just col & row)

                    int pawnTwoStepRow = epPiece.COLOR == Color.BLACK ? 3 : 4; //2 rows after the initial pawn starting row
                    int previousMoveRow = 8 - (Integer.parseInt(previousMoveStr.substring(1))); //convert from display row to array index (inverted).

                    //can only move en passant IMMEDIATELY after opponent moved 2 squares forward in initial pawn movement.
                    if (previousMoveRow == pawnTwoStepRow) {

                        if (epPiece.timesMoved == 1 && epPiece.currentRow == pawnTwoStepRow) {

                            if (destCol - 1 >= 0) {
                                Piece leftPiece = gameBoard[prevPawnRow][destCol - 1];

                                if (leftPiece instanceof Pawn && leftPiece.COLOR == playerColor) {
                                    pieceChoices.add(leftPiece);
                                }

                            }

                            if (destCol + 1 < 8) {
                                Piece rightPiece = gameBoard[prevPawnRow][destCol + 1];

                                if (rightPiece instanceof Pawn && rightPiece.COLOR == playerColor) {
                                    pieceChoices.add(rightPiece);
                                }

                            }

                        }
                    }

                }

            }


            //regular straight line movement
            if (gameBoard[destRow][destCol] == null) {

                if (gameBoard[prevPawnRow][destCol] instanceof Pawn && gameBoard[prevPawnRow][destCol].COLOR == playerColor) {
                    pieceChoices.add(gameBoard[prevPawnRow][destCol]);

                } else if (destRow == 3 && playerColor == Color.BLACK || destRow == 4 && playerColor == Color.WHITE) {
                    //checking for 2-square move (permitted on first pawn move only)
                    prevPawnRow += pawnRowOffset;

                    if (gameBoard[prevPawnRow][destCol] instanceof Pawn && gameBoard[prevPawnRow][destCol].COLOR == playerColor) {
                        pieceChoices.add(gameBoard[prevPawnRow][destCol]);
                    }
                }
            }
        }

        //KING - adjacent (one move only) squares in any direction
        if (pieceShortName == 'K') {

            for (int rowNum = destRow - 1; rowNum < destRow + 2; ++rowNum) {
                for (int colNum = destCol - 1; colNum < destCol + 2; ++colNum) {

                    if (Piece.outOfBounds(rowNum) || Piece.outOfBounds(colNum)) {
                        continue;
                    }

                    if (rowNum == 0 && colNum == 0) continue;

                    Piece nextPiece = gameBoard[rowNum][colNum];

                    if (nextPiece instanceof King && nextPiece.COLOR == playerColor) {
                        pieceChoices.add(gameBoard[rowNum][colNum]);
                    }
                }
            }

        }

        //KNIGHT - specific squares with a row / col difference of 3
        if (pieceShortName == 'N') {

            for (int[] rowCol : Knight.knightMoveOffsets) {

                int nRow = destRow + rowCol[0];
                int nCol = destCol + rowCol[1];

                if (Piece.outOfBounds(nRow) || Piece.outOfBounds(nCol)) {
                    continue;
                }

                Piece nextPiece = gameBoard[nRow][nCol];

                if (nextPiece instanceof Knight && nextPiece.COLOR == playerColor) {
                    pieceChoices.add(gameBoard[nRow][nCol]);
                }

            }

        }

        //BISHOP, ROOK, QUEEN - check surrounding squares to max possible extent if necessary.
        if (pieceShortName == 'B' || pieceShortName == 'R' || pieceShortName == 'Q') {

            //get nearest piece in all directions.
            for (int rowChange = -1; rowChange < 2; ++rowChange) {
                for (int colChange = -1; colChange < 2; ++colChange) {

                    if (rowChange == 0 && colChange == 0) continue; //skip the location to move to

                    //skip diagonals for rook, skip straight lines for bishop
                    if (pieceShortName == 'R' && !(rowChange == 0 || colChange == 0)) continue;
                    if (pieceShortName == 'B' && (rowChange == 0 || colChange == 0)) continue;

                    Piece nextNearestPiece = Piece.findNearestPiece(gameBoard, rowAndCol, rowChange, colChange);

                    if (nextNearestPiece != null && nextNearestPiece.COLOR != currentPlayerColor) continue;

                    //queen -all directions possible
                    if (pieceShortName == 'Q' && nextNearestPiece instanceof Queen) {
                        pieceChoices.add(nextNearestPiece);
                    }

                    //rook -straight lines - checked above
                    if (pieceShortName == 'R') {
                        if (nextNearestPiece instanceof Rook) {
                            pieceChoices.add(nextNearestPiece);
                        }
                    }

                    //bishop -diagonals - checked above
                    if (pieceShortName == 'B') {
                        if (nextNearestPiece instanceof Bishop) {
                            pieceChoices.add(nextNearestPiece);
                        }
                    }
                }

            }
        }


        if (pieceChoices.size() == 0) {
            return null;
        }

        if (pieceChoices.size() == 1) {
            return pieceChoices.get(0);
        }

        multiPieceChoice = true;

        //if disambiguation was already provided, use it to find the correct piece to move.
        //this should work whether the row, col or both are provided.
        if (extraMoveInfo != null) {

            for (Piece p : pieceChoices) {
                String alphaColRow = p.getAlphanumericLoc();

                if (alphaColRow.contains(extraMoveInfo)) {
                    return p;
                }

            }
        }

        System.out.println("choose piece to move: ");

        ArrayList<Integer> pieceChoiceRows = new ArrayList<>();
        ArrayList<Integer> pieceChoiceCols = new ArrayList<>();

        for (int i = 0; i < pieceChoices.size(); ++i) {
            Piece p = pieceChoices.get(i);

            if (!pieceChoiceRows.contains(p.currentRow)) {
                pieceChoiceRows.add(p.currentRow);
            } else {
                multiChoiceSameRow = true;
            }

            if (!pieceChoiceCols.contains(p.currentCol)) {
                pieceChoiceCols.add(p.currentCol);
            } else {
                multiChoiceSameCol = true;
            }

            System.out.print("[" + (i + 1) + "] " + p.getAlphanumericLoc() + ", ");
        }

        Scanner sc = new Scanner(System.in);
        String input = sc.next();
        int chosenNumber = 1;

        if (input.length() != 0) {
            try {
                chosenNumber = Integer.parseInt(input);
            } catch (NumberFormatException nfe) {
                System.out.println("invalid input - setting choice to 1");
            }
        }

        return pieceChoices.get(chosenNumber - 1);
    }

    private void printMovesList() {
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

        if (checkmated) {
            System.out.print((currentPlayerColor == Color.WHITE) ? "1-0" : "0-1");
        }

        System.out.println();
    }

    private void updateKingPositions() {

        for (Piece[] row : gameBoard) {
            for (Piece piece : row) {

                if (piece instanceof King) {

                    if (piece.COLOR == Color.BLACK) {
                        bKingLoc[0] = piece.currentRow;
                        bKingLoc[1] = piece.currentCol;
                    } else {
                        wKingLoc[0] = piece.currentRow;
                        wKingLoc[1] = piece.currentCol;
                    }

                }

            }
        }

    }

    //clean up the input String for use in the findPieceToMove() method which requires just the piece short name
    //(if not a pawn) and the final board destination.
    //returns any extra disambiguation info in array position [0] and the regular move String in array[1]
    private String[] parseNotation(String inputStr) {

        String[] moveInfo = new String[2];

        if (inputStr == null) {
            return moveInfo;
        }

        //handle special case of castling.
        if (inputStr.equals("0-0") || inputStr.equals("O-O") || inputStr.equals("0-0-0") || inputStr.equals("O-O-O")) {
            moveInfo[1] = inputStr;
            return moveInfo;
        }

        //filter out any capital letters that aren't piece names.
        List<Character> pieceShortNames = Arrays.asList('K', 'Q', 'N', 'B', 'R');
        StringBuilder tempInputStr = new StringBuilder();

        char firstChar = inputStr.charAt(0);
        boolean pawn = true;

        //pawns are not specifically noted with a capital letter.
        if (pieceShortNames.contains(firstChar)) {
            tempInputStr.append(firstChar);
            pawn = false;
        }

        //filter out any rows outside of a - h chars, and add numbers. anything else is dropped.
        // (e.g. # ! ? notation symbols used for checkmate, comments, etc)
        for (int i = 0; i < inputStr.length(); ++i) {

            char current = inputStr.charAt(i);

            if (current >= 'a' && current <= 'h') {
                tempInputStr.append(current);
            }

            if (Character.isDigit(current)) {
                tempInputStr.append(current);
            }

        }

        String destStr = tempInputStr.toString();

        //remove potential en passant notation. as 'p' will not be processed, an erroneous 'e' might remain.
        //in any case, the last char must always be a digit.
        char last = destStr.charAt(destStr.length() - 1);

        if (!Character.isDigit(last)) {
            destStr = destStr.substring(0, destStr.length() - 1);
        }

        //handle disambiguation strings where extra information about the exact piece to move is given.
        //e.g. Ngf3 means move the Knight at col 'g' to location 'f3'. or here: exd5 -> sanitized to -> ed5 for pawn attack, etc.
        if (destStr.length() == 3 && pawn || destStr.length() > 3 && !pawn) {

            int firstRowColIndex = 1; //the first additional char found. named pieces will have 'K' etc as the first char

            if (pawn) {
                firstRowColIndex = 0;
            }

            if (destStr.charAt(firstRowColIndex) >= 'a' && destStr.charAt(firstRowColIndex) <= 'h') {
                moveInfo[0] = destStr.charAt(firstRowColIndex) + "";
                destStr = destStr.substring(0, firstRowColIndex) + destStr.substring(firstRowColIndex + 1);
            }
        }

        moveInfo[1] = destStr;

        return moveInfo;
    }


}
