package game.chess;


import java.util.ArrayList;

enum Color {
    WHITE,
    BLACK
}

abstract class Piece {

    final Color COLOR;
    final Color ENEMY_COLOR;
    final String ICON;
    final String NAME;
    final String SHORT_NAME;
    final int VALUE;

    protected int currentRow;
    protected int currentCol;
    protected int timesMoved;
    protected boolean captured = false;

    protected Piece[][] gameBoard = new Piece[8][8];


    Piece(Color color, String name) {

        this.COLOR = color;
        this.NAME = name;
        this.SHORT_NAME = getShortName();
        this.VALUE = getPointsValue();
        this.ICON = getIcon();
        this.ENEMY_COLOR = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

    }

    Piece(Color color, String name, int row, int col, Piece[][] gameBoard) {
        this(color, name);
        this.gameBoard = gameBoard;
        this.currentRow = row;
        this.currentCol = col;
        place(row, col);
        timesMoved = 0; //timesMoved increments on calls to place() method in gameplay. so reset to 0 in constructor call only.
    }

//    Piece(Color color, String name, String alphaLoc, Piece[][] gameBoard) {
//        this(color, name);
//        Integer[] rowCol = Piece.convertAlphanumericToRowCol(alphaLoc);
//        if (rowCol != null) {
//            this.gameBoard = gameBoard;
//            this.currentRow = rowCol[0];
//            this.currentCol = rowCol[1];
//            place(currentRow, currentCol);
//            timesMoved = 0;
//        }
//    }


    protected void place(int row, int col) {
        if (!outOfBounds(row) && !(outOfBounds(col))) {
            gameBoard[currentRow][currentCol] = null;
            currentRow = row;
            currentCol = col;
            gameBoard[row][col] = this;
            ++timesMoved;
        }
    }


    abstract boolean move(int row, int col, int[] points);

    protected static boolean outOfBounds(int rowOrCol) {
        return rowOrCol < 0 || rowOrCol >= 8;
    }

    protected boolean isBlockedPath(int newRow, int newCol) {

        int rowChange = Math.abs(currentRow - newRow);
        int colChange = Math.abs(currentCol - newCol);

        int moves = Math.max(rowChange, colChange); //works if one is 0, or both same (diagonal)

        int rowParity = 0; //change on each iteration. rowDeltaT might be a better name ?
        int colParity = 0;

        if (rowChange > 0) {
            rowParity = currentRow < newRow ? 1 : -1;
        }

        if (colChange > 0) {
            colParity = currentCol < newCol ? 1 : -1;
        }

        int checkRow = currentRow + rowParity;
        int checkCol = currentCol + colParity;

        for (int i = 0; i < (moves - 1); ++i) { //check to square just before final spot. this accounts for attacking moves, etc.
            if (gameBoard[checkRow][checkCol] != null) { //blocked.
//                System.out.println("can't move - blocked");
                return true;
            }
            checkRow += rowParity;
            checkCol += colParity;

        }

        return false;
    }

    static Piece findNearestPiece(Piece[][] gameBoard, Integer[] rowAndCol, int rChange, int cChange) {

        int row = rowAndCol[0] + rChange;
        int col = rowAndCol[1] + cChange;

        Piece nearest = null;

        while (!Piece.outOfBounds(row) && !Piece.outOfBounds(col)) {

            if (gameBoard[row][col] != null) {
                nearest = gameBoard[row][col];
                break;
            }

            row = row + rChange;
            col = col + cChange;

        }

        return nearest;
    }


    static boolean isSquareUnderAttack(Piece[][] gameBoard, int checkRow, int checkCol, Color otherTeamColor) {

        Integer[] checkRowAndCol = { checkRow, checkCol };
        ArrayList<Piece> attackingPieces = findAttackingPieces(gameBoard, checkRowAndCol, otherTeamColor, true);
        
        return attackingPieces.size() > 0;

    }

    /**
     * Find all the enemy pieces attacking a particular square of the board. Also used to determine if a square is being attacked
     * by any piece, by optionally returning immediately when the first attacking piece is found.
     *
     * @param gameBoard The game board which is a 2-d array of Piece objects.
     * @param checkRowAndCol An Integer array containing the row and column to check in position 0 and 1 respectively.
     * @param otherTeamColor The enemy piece's color.
     * @param quickCheck Toggle immediate (short-circuit) return on discovery of any attacking piece (e.g. for determining if a King is in check).
     * @return An ArrayList of all the pieces attacking that square.
     */
    static ArrayList<Piece> findAttackingPieces(Piece[][] gameBoard, Integer[] checkRowAndCol, Color otherTeamColor, boolean quickCheck) {

        ArrayList<Piece> attackingPieces = new ArrayList<>();

        int checkRow = checkRowAndCol[0];
        int checkCol = checkRowAndCol[1];

        //knight check needs special treatment as knights can jump
        for (int[] knightRowCol : Knight.knightMoveOffsets) {

            int nRow = checkRow + knightRowCol[0];
            int nCol = checkCol + knightRowCol[1];

            if (nRow < 0 || nCol < 0 || nRow > 7 || nCol > 7) {
                continue;
            }

            Piece checkPiece = gameBoard[nRow][nCol];

            if (checkPiece instanceof Knight && checkPiece.COLOR == otherTeamColor) {
                attackingPieces.add(checkPiece);
                
                if (quickCheck) {
                    return attackingPieces;
                }
            }

        }


        int[] rowAndCol = { checkRow, checkCol };
        int pieceIndex = 0;

        //check pawns in 2 corner locations coming from the direction of the other team's home row
        int pawnAttackRow = checkRow - (otherTeamColor == Color.WHITE ? -1 : 1 );

        if (!Piece.outOfBounds(pawnAttackRow)) {

            if (!Piece.outOfBounds(checkCol - 1)) {
                Piece leftPossPawn = gameBoard[pawnAttackRow][checkCol - 1];


                if (leftPossPawn instanceof Pawn && leftPossPawn.COLOR == otherTeamColor) {
                    attackingPieces.add(leftPossPawn);

                    if (quickCheck) {
                        return attackingPieces;
                    }
                }
            }

            if (!Piece.outOfBounds(checkCol + 1)) {
                Piece rightPossPawn = gameBoard[pawnAttackRow][checkCol + 1];

                if (rightPossPawn instanceof Pawn && rightPossPawn.COLOR == otherTeamColor) {
                    attackingPieces.add(rightPossPawn);

                    if (quickCheck) {
                        return attackingPieces;
                    }
                }

            }
        }

        //check all surrounds until piece found or bounds reached.
        //this loop checks directly around square 0,0.
        //      c-1 c0 c1
        //r-1:  -1  0   1
        //r0:   -1  0   1
        //r1:   -1  0   1
        for (int rChange = -1; rChange < 2; ++rChange) {

            for (int cChange = -1; cChange < 2; ++cChange) {
                ++pieceIndex;

                if (rChange == 0 && cChange == 0) {
                    continue; //self
                }

                Integer[] checkRowCol = { rowAndCol[0], rowAndCol[1] } ;
                Piece nextNearPiece = findNearestPiece(gameBoard, checkRowCol, rChange, cChange);
                int piecesSize = attackingPieces.size();

                if (nextNearPiece != null && nextNearPiece.COLOR == otherTeamColor) {

                    //queen attacks in any direction, any number of squares
                    if (nextNearPiece instanceof Queen) {
                        attackingPieces.add(nextNearPiece);
                    }

                    //bishop - diagonal, unlimited
                    if (pieceIndex % 2 == 1) {
                        if (nextNearPiece instanceof Bishop) {
                            attackingPieces.add(nextNearPiece);
                        }
                    }

                    //rook - straight, unlimited
                    if (pieceIndex % 2 == 0) {
                        if (nextNearPiece instanceof Rook) {
                            attackingPieces.add(nextNearPiece);
                        }
                    }

                    //king check. single square in any direction.
                    if (nextNearPiece instanceof King) {
                        if ( Math.abs(nextNearPiece.currentRow - checkRow) <= 1 && //max 1 col, 1 row away to attack
                                Math.abs(nextNearPiece.currentCol - checkCol) <= 1) {
                            attackingPieces.add(nextNearPiece);
                        }
                    }
                }

                if (piecesSize < attackingPieces.size() && quickCheck) {
                    return attackingPieces;
                }
            }
        }


        return attackingPieces;


    }

    abstract String getShortName();

    abstract int getPointsValue();

    abstract String getIcon();

    abstract ArrayList<Integer[]> getValidMoves(String lastMoveStr);

    public String getAlphanumericLoc() {
        return (char) (currentCol + 'a') + "" + (8 - currentRow);
    }

    static String convertRowColToAlphanumeric(int row, int col) {
        return (char) (col + 'a') + "" + (8 - row);
    }

    //change co-ordinates String like "e4" to correct array row & col numbering.
    static Integer[] convertAlphanumericToRowCol(String alphaStr) {

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



    //for checking stalemate condition by testing piece moves and then changing back to the original state of the board.
    //could also adapt for regular game but must take into account the record of game moves and update the notation
    //of the game correctly.
    void revertMove(int prevRow, int prevCol, Piece capturedPiece, int[] points) {

        if (Piece.outOfBounds(prevRow) || Piece.outOfBounds(prevCol)) {
            return;
        }

        gameBoard[prevRow][prevCol] = this;
        --timesMoved;

        if (capturedPiece != null) {
            capturedPiece.captured = false;
            capturedPiece.currentRow = this.currentRow;
            capturedPiece.currentCol = this.currentCol;
            gameBoard[currentRow][currentCol] = capturedPiece;

            //a captured white piece means black points increased, and vice versa
            if (capturedPiece.COLOR == Color.WHITE) {
                points[1] -= capturedPiece.getPointsValue();
            } else {
                points[0] -= capturedPiece.getPointsValue();
            }

        } else {
            gameBoard[currentRow][currentCol] = null;
        }

        this.currentRow = prevRow;
        this.currentCol = prevCol;

    }

    @Override
    public String toString() {
        String colorStr = this.COLOR == Color.WHITE ? "w" : "b";
        return SHORT_NAME + colorStr + "(" + convertRowColToAlphanumeric(currentRow,currentCol) + ")";
    }

}


