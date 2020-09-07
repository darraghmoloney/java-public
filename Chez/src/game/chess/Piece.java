package game.chess;


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

    int currentRow;
    int currentCol;
    int timesMoved = 0;
    boolean captured = false;


    Piece(Color color, String name) {

        this.COLOR = color;
        this.NAME = name;
        this.SHORT_NAME = getShortName();
        this.VALUE = getPointsValue();
        this.ICON = getIcon();
        this.ENEMY_COLOR = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;

    }

    Piece(Color color, String name, int row, int col) {
        this(color, name);
        place(row, col);
    }


    public void place(int row, int col) {
        if (!outOfBounds(row) && !(outOfBounds(col))) {
            currentRow = row;
            currentCol = col;
        }
    }


    abstract boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points);

    protected static boolean outOfBounds(int rowOrCol) {
        return rowOrCol < 0 || rowOrCol >= 8;
    }

    boolean isBlockedPath(Piece[][] gameBoard, int newRow, int newCol) {

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

        for (int i = 0; i < (moves - 1); ++i) { //check to square just before final spot.
            if (gameBoard[checkRow][checkCol] != null) { //blocked.
                System.out.println("can't move - blocked");
                return true;
            }
            checkRow += rowParity;
            checkCol += colParity;

        }

        return false;
    }

    protected static Piece findNearestPiece(Piece[][] gameBoard, Integer[] rowAndCol, int rChange, int cChange) {

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

        //  0   [1]   2   [3]   4
        //  [5]   6   7   8   [9]
        //  10  11  -12-  13  14
        //  [15]  16  17  18  [19]
        //  20  [21]  22  [23]  24


        //knight check needs special treatment as knights can jump
        int[][] validKnightMoveSquares = {
                { checkRow - 2, checkCol - 1 },
                { checkRow - 2, checkCol + 1 },
                { checkRow - 1, checkCol - 2},
                { checkRow - 1, checkCol + 2},

                { checkRow + 2, checkCol - 1 },
                { checkRow + 2, checkCol + 1 },
                { checkRow + 1, checkCol - 2},
                { checkRow + 1, checkCol + 2},
        };

        for (int[] knightRowCol : validKnightMoveSquares) {

            int nRow = knightRowCol[0];
            int nCol = knightRowCol[1];

            if (nRow < 0 || nCol < 0 || nRow > 7 || nCol > 7) {
                continue;
            }

            Piece checkPiece = gameBoard[nRow][nCol];

            if (checkPiece != null && checkPiece.COLOR == otherTeamColor && checkPiece instanceof Knight) {
                return true;
            }

        }

        int[] rowAndCol = { checkRow, checkCol };
        int pieceIndex = 0;

        //check pawns in 2 corner locations coming from the direction of the other team's home row
        int pawnAttackRow = checkRow - (otherTeamColor == Color.WHITE ? -1 : 1 );

        Piece leftPossPawn = gameBoard[pawnAttackRow][checkCol - 1];
        Piece rightPossPawn = gameBoard[pawnAttackRow][checkCol + 1];

        if (leftPossPawn != null && leftPossPawn.COLOR == otherTeamColor && leftPossPawn instanceof Pawn) {
            return true;
        }

        if (rightPossPawn != null && rightPossPawn.COLOR == otherTeamColor && rightPossPawn instanceof Pawn) {
            return true;
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

                if (nextNearPiece != null && nextNearPiece.COLOR == otherTeamColor) {

                    //queen attacks in any direction, any number of squares
                    if (nextNearPiece instanceof Queen) {
                        return true;
                    }

                    //bishop - diagonal, unlimited
                    if (pieceIndex % 2 == 1) {
                        if (nextNearPiece instanceof Bishop) {
                            return true;
                        }
                    }

                    //rook - straight, unlimited
                    if (pieceIndex % 2 == 0) {
                        if (nextNearPiece instanceof Rook) {
                            return true;
                        }
                    }

                    //king check. single square in any direction.
                    if (nextNearPiece instanceof King) {
                        if ( Math.abs(nextNearPiece.currentRow - checkRow) <= 1 && //max 1 col, 1 row away to attack
                                Math.abs(nextNearPiece.currentCol - checkCol) <= 1) {
                            return true;
                        }
                    }
                }
            }
        }


        return false;
    }

    abstract String getShortName();

    abstract int getPointsValue();

    abstract String getIcon();

    public String getAlphanumericLoc() {
        return (char) (currentCol + 'a') + "" + (8 - currentRow);
    }

    @Override
    public String toString() {
        String colorStr = this.COLOR == Color.WHITE ? "w" : "b";
        return SHORT_NAME + colorStr;
    }

}


