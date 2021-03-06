package game.chess;


import java.util.*;
import java.util.regex.Pattern;

public class Chez {

    private final Piece[][] gameBoard;
    private boolean checkmated;
    private boolean stalemate;
    private boolean resigned;

    private Color currentPlayerColor = Color.WHITE;

    //for correct game record notation if choice made between multiple pieces to use
    private boolean multiPieceChoice;

    //for disambiguation of multiple pieces in user input
    private boolean multiChoiceSameCol;
    private boolean multiChoiceSameRow;

    private final ArrayList<String> moveList = new ArrayList<>(); //moves record
    private final int[] points = new int[2];
    private String lastMoveStr = null;

    private int[] bKingLoc = {0, 4}; //store king locations to easily verify if king is in check or not
    private int[] wKingLoc = {7, 4};

    private final LinkedList<Piece> activeWhitePieces = new LinkedList<>();
    private final LinkedList<Piece> activeBlackPieces = new LinkedList<>();

    public Chez() {
        gameBoard = BoardBuilder.makeBoard();

        for (Piece[] row : gameBoard) {
            for (Piece piece : row) {
                if (piece != null) {
                    if (piece.COLOR == Color.BLACK) {
                        activeBlackPieces.add(piece);
                    } else {
                        activeWhitePieces.add(piece);
                    }

                }
            }
        }

    }

    public void play() {

        showBoard();

        boolean gameOver = false;

        Queue<String> movesQueue = new ArrayDeque<>();

        while (!gameOver && !checkmated) {

            updateKingPositions();
            printMovesList();

            //TODO: only allow moves that remove check condition if king in check (requires surrender or automatic checkmate if not possible)

            System.out.print("[" + points[0] + ":" + points[1] + "] ");

            Scanner sc = new Scanner(System.in);
            String moveStr;

            if (movesQueue.size() == 0) {

                System.out.print("enter move for " + currentPlayerColor + " (e4, Nh6, 0-0...) - re[s]ign - [q]uit: ");
                moveStr = sc.next();

            } else {

                //if moves are pre-loaded, display them one-by-one after a short delay
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException ie) {
//                    ie.printStackTrace();
//                }

                moveStr = movesQueue.poll();

            }

            String[] moveInfo; //stores start position of piece if provided for disambiguation [0], and the move itself [1]

            if (moveStr == null) continue;
            if (moveStr.length() <= 0) continue;

            if (moveStr.charAt(0) == 'q') {
                break;
            }
            else if (moveStr.equals("resign") || moveStr.toLowerCase().charAt(0) == 's') {
                System.out.println(currentPlayerColor + " resigns");
                resigned = true;
                showBoard();
                printMovesList();
                break;

            }
            //find all possible moves for a given piece based on its board location (short name can be included but not needed)
            else if (moveStr.toLowerCase().equals("valid") || moveStr.toLowerCase().charAt(0) == 'v') {
                String pieceToFindMovesFor = sc.next();

                if (pieceToFindMovesFor.length() > 0) {

                    pieceToFindMovesFor = pieceToFindMovesFor.substring(pieceToFindMovesFor.length() - 2);
                    Integer[] findRowCol = Piece.convertAlphanumericToRowCol(pieceToFindMovesFor);

                    if (findRowCol != null) {
                        int findRow = findRowCol[0];
                        int findCol = findRowCol[1];

                        if (!Piece.outOfBounds(findRow) && !Piece.outOfBounds(findCol)) {

                            Piece found = gameBoard[findRow][findCol];

                            if (found != null) {

                                System.out.println("valid moves for " + found + ": ");
                                ArrayList<Integer[]> validMovesList = found.getValidMoves(lastMoveStr);
                                System.out.print("\t");

                                for (Integer[] validRowCol : validMovesList) {
                                    System.out.print("-");

                                    if (!(found instanceof Pawn)) {
                                        System.out.print(found.getShortName());
                                    }

                                    Piece moveSpot = gameBoard[validRowCol[0]][validRowCol[1]];

                                    //print 'x' notation if move is attack
                                    if (moveSpot != null && moveSpot.COLOR == found.ENEMY_COLOR) {

                                        if (found instanceof Pawn) {
                                            System.out.print((char) (findCol + 'a'));
                                        }

                                        System.out.print("x");
                                    }

                                    System.out.print(Piece.convertRowColToAlphanumeric(validRowCol[0], validRowCol[1]) + " ");
                                }

                                System.out.println();
                            }

                        }
                    }

                }
                continue;

            }
            else if (moveStr.equals("load") || moveStr.toLowerCase().charAt(0) == 'l') {

                sc.nextLine();
                System.out.print("enter moves: ");
                String allGameMoves = sc.nextLine();

                movesQueue = parseFullGameNotation(allGameMoves);
                continue;


            }
            else {

                if (moveStr.length() < 2) continue;

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

            if (chosenPiece instanceof Pawn) {
                ((Pawn) chosenPiece).setPieceToPromoteTo(moveInfo[2]); //if promotion info is included in notation string, add it
            }


            String rowColStr = moveStr;
            if (moveStr.length() > 2) rowColStr = rowColStr.substring(1); //location is after the piece short name

            Integer[] moveRowCol = Piece.convertAlphanumericToRowCol(rowColStr);


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
            Piece previousPiece = gameBoard[moveRow][moveCol];

            //for extra disambiguation in notation if necessary
            char chosenPieceCol = chosenPiece.getAlphanumericLoc().charAt(0);
            char chosenPieceRow = chosenPiece.getAlphanumericLoc().charAt(1);

            boolean validMove = chosenPiece.move(moveRow, moveCol, points);

            //after successful move, king will be at the new spot
            boolean kingMoved = gameBoard[moveRow][moveCol] instanceof King;

            if (validMove) {

                ++chosenPiece.timesMoved;

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

                //valid move to attack an enemy piece
                if (attackAttempt && previousPiece != null) {
                    if (currentPlayerColor == Color.BLACK) {
                        activeWhitePieces.remove(previousPiece);
                    } else {
                        activeBlackPieces.remove(previousPiece);
                    }

                }

                //notation to clarify which piece was chosen in case of multiple options. e.g. Nge7 means the knight at g was moved to e7.
                //an attacking pawn will already record its move from the start col, so its notation doesn't need to change.
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
                        gameBoard[moveRow][moveCol + 1].timesMoved++; //increment the rook's move count
                    } else {
                        gameBoard[moveRow][moveCol - 1].timesMoved++;
                    }
                }

                //valid attack on king -> checkmate. NB technically impossible to capture King in real game
                // and should be handled already through checkmate check method.
                if (attackingKing) {

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
                lastMoveStr = moveNotation;

            }

            showBoard();

            if (checkmated) {
                printMovesList();
                System.out.println("[" + points[0] + ":" + points[1] + "] ");
                break;
            }

            if (validMove) {
                currentPlayerColor = currentPlayerColor == Color.WHITE ? Color.BLACK : Color.WHITE;
            }


            checkStalemate(currentPlayerColor);

            if (stalemate) {
                System.out.println("stalemate");
                printMovesList();
                System.out.println("[" + points[0] + ":" + points[1] + "] ");
                break;
            }



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


    private Piece findPieceToMove(String movementStr, Color playerColor, String extraMoveInfo) {

        multiPieceChoice = false; //to record move notation correctly when the choice of piece is ambiguous

        //movementStr is like e5 or Nc3 or Qf7 etc.
        char pieceShortName;
        String alphanumericStr;

        //special handling for castling.
        if (movementStr.equals("0-0") || movementStr.equals("O-O") || movementStr.equals("0-0-0") || movementStr.equals("O-O-O")) {

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

        Integer[] rowAndCol = Piece.convertAlphanumericToRowCol(alphanumericStr);

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

            //attacking en passant. (capturing pieces that have moved 2 squares on their first move, as if they had only moved 1)
            if (playerColor == Color.WHITE && destRow == 2 || playerColor == Color.BLACK && destRow == 5) {
                Piece epPiece = gameBoard[destRow + pawnRowOffset][destCol];


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
        } else if (stalemate) {
            System.out.print("½-½");
        } else if (resigned) {
            System.out.print((currentPlayerColor == Color.WHITE) ? "0-1" : "1-0");
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
    //and any pawn promotion choice in [2]
    private static String[] parseNotation(String inputStr) {

        String[] moveInfo = new String[3];

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
            char currentChar = inputStr.charAt(i);

            if (currentChar >= 'a' && currentChar <= 'h') {
                tempInputStr.append(currentChar);
            }

            if (Character.isDigit(currentChar)) {
                tempInputStr.append(currentChar);
            }

            //add promotion info if pawn only. add the first one found.
            if (pawn && currentChar != 'K' && pieceShortNames.contains(currentChar) && moveInfo[2] == null) {
                moveInfo[2] = currentChar + "";
            }

        }

        String destStr = tempInputStr.toString();

        if (destStr.length() > 0) {
            //remove potential en passant notation. as 'p' will not be processed, an erroneous 'e' might remain.
            //in any case, the last char must always be a digit.
            char last = destStr.charAt(destStr.length() - 1);

            //remove anything before the final digit. if a pawn promotion occurs, the new piece type will be noted after the number,
            //so store it too if found.
            while (!Character.isDigit(last)) {
                destStr = destStr.substring(0, destStr.length() - 1);
                if (destStr.length() == 0) break;

                last = destStr.charAt(destStr.length() - 1);
            }

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

    //convert an entire noted game into a list of moves to execute. assumes basic formatting but advanced checks will
    //be handled by the moveStr processing method anyway.
    private static Queue<String> parseFullGameNotation(String gameStr) {

        Queue<String> moveInfoArray = new ArrayDeque<>();

        System.out.println(gameStr);

        String[] allNotations = gameStr.split(" ");

        for (String move : allNotations) {
            if (move.length() < 2) continue;
            if (Pattern.matches("\\d+\\.", move)) continue; //remove numbers from move list.

            moveInfoArray.offer(move);

        }

        return moveInfoArray;
    }


    //check if the game has reached a stalemate. this is a situation where a player's king is not in check, but the
    // player cannot move any piece in any way without the move resulting in checkmate. this ends the game in a draw.
    private void checkStalemate(Color currentPlayerColor) {

        stalemate = true;

        Piece playerKing = currentPlayerColor == Color.BLACK ?
                gameBoard[bKingLoc[0]][bKingLoc[1]] : gameBoard[wKingLoc[0]][wKingLoc[1]];


        //check all possible moves. find a piece, get its possible moves, one by one move the piece by
        // updating the board temporarily, preserving the initial row and col of the moved piece,
        // and any captured piece. check if the newly updated board has a checkmated king.
        LinkedList<Piece> allPlayerPieces = currentPlayerColor == Color.BLACK ? activeBlackPieces : activeWhitePieces;

        boolean safeMoveFound = false;

        for (Piece playerPiece : allPlayerPieces) {

            int startRow = playerPiece.currentRow;
            int startCol = playerPiece.currentCol;

            ArrayList<Integer[]> validMovesList = playerPiece.getValidMoves(lastMoveStr);


            for (Integer[] rowCol : validMovesList) {

                int moveRow = rowCol[0];
                int moveCol = rowCol[1];

                Piece castledRook = null;
                int castledRookCol = moveCol > startCol ? 7 : 0;

                //castling check. store rook too if necessary
                if (playerPiece instanceof King && Math.abs(moveCol - startCol) > 1) {
                    castledRook = gameBoard[startRow][castledRookCol];
                }

                //store Piece at location to move to in case it's an attack, so it can be restored later
                Piece pieceAtMoveLoc = gameBoard[moveRow][moveCol];

                playerPiece.move(moveRow, moveCol, points); //assuming that the move is successful as already in valid move list

                //if after the move the king is not checkmated, stalemate has not occurred
                if (!(Piece.isSquareUnderAttack(gameBoard, playerKing.currentRow, playerKing.currentCol, playerPiece.ENEMY_COLOR))) {
                    safeMoveFound = true;
                    stalemate = false;
                }

                //move checked piece back to its original spot.
                playerPiece.revertMove(startRow, startCol, pieceAtMoveLoc, points);

                if (castledRook != null) {
                    castledRook.place(startRow, castledRookCol);
                }

                //jump out as early as possible. NB using return above won't work as pieces have to be moved back first
                if (safeMoveFound) {
                    return;
                }

            }

        }

    }


}
