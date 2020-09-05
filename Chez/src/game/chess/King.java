package game.chess;

class King extends Piece {

    static int[] bKingLoc = {0, 4};
    static int[] wKingLoc = {7, 4};

    King(Color color, int row, int col) {
        super(color, "King", row, col);
    }

    @Override
    boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            System.out.println("out of bounds");
            return false;
        }

        int rowChange = Math.abs(currentRow - newRow);
        int colChange = Math.abs(currentCol - newCol);

        if (rowChange > 1 || colChange > 1) {
            System.out.println("king can only move one step at a time");
            return false;
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
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

        if (COLOR == Color.BLACK) {
            bKingLoc[0] = currentRow;
            bKingLoc[1] = currentCol;
        } else {
            wKingLoc[0] = currentRow;
            wKingLoc[1] = currentCol;
        }

        ++timesMoved;
        return true;
    }

    boolean isInCheck(Piece[][] gameBoard) {

        return isSquareUnderAttack(gameBoard, currentRow, currentCol, ENEMY_COLOR);

//        //  0   [1]   2   [3]   4
//        //  [5]   6   7   8   [9]
//        //  10  11  -12-  13  14
//        //  [15]  16  17  18  [19]
//        //  20  [21]  22  [23]  24
//
//
//        //knight check needs special treatment as knights can jump
//        int[][] validKnightSquares = {
//                { currentRow - 2, currentCol - 1 },
//                { currentRow - 2, currentCol + 1 },
//                { currentRow - 1, currentCol - 2},
//                { currentRow - 1, currentCol + 2},
//
//                { currentRow + 2, currentCol - 1 },
//                { currentRow + 2, currentCol + 1 },
//                { currentRow + 1, currentCol - 2},
//                { currentRow + 1, currentCol + 2},
//        };
//
//        for (int[] knightRowCol : validKnightSquares) {
//
//            int kRow = knightRowCol[0];
//            int kCol = knightRowCol[1];
//
//            if (kRow < 0 || kCol < 0 || kRow > 7 || kCol > 7) {
//                continue;
//            }
//
//            Piece checkPiece = gameBoard[kRow][kCol];
//
//            if (checkPiece != null && checkPiece.COLOR == ENEMY_COLOR && checkPiece instanceof Knight) {
//                return true;
//            }
//
//        }
//
//        int[] rowAndCol = { currentRow, currentCol };
//        int pieceIndex = 0;
//        int pawnIndexOffset = COLOR == Color.BLACK ? 6 : 0; //as pawns can only attack forward, only need to check one side
//
//
//        //check all surrounds until piece found or bounds reached.
//        //this loop checks directly around square 0,0.
//        //      c-1 c0 c1
//        //r-1:  -1  0   1
//        //r0:   -1  0   1
//        //r1:   -1  0   1
//        for (int rChange = -1; rChange < 2; ++rChange) {
//
//            for (int cChange = -1; cChange < 2; ++cChange) {
//                ++pieceIndex;
//
//                if (rChange == 0 && cChange == 0) {
//                    continue; //self
//                }
//
//                int[] checkRowCol = { rowAndCol[0], rowAndCol[1] } ;
//                Piece nextNearPiece = findNearestPiece(gameBoard, checkRowCol, rChange, cChange);
//
//                if (nextNearPiece != null && nextNearPiece.COLOR == this.ENEMY_COLOR) {
//
//                    //queen attacks in any direction, any number of squares
//                    if (nextNearPiece instanceof Queen) {
//                        return true;
//                    }
//
//                    //bishop - diagonal, unlimited
//                    if (pieceIndex % 2 == 1) {
//                        if (nextNearPiece instanceof Bishop) {
//                            return true;
//                        }
//                    }
//
//                    //rook - straight, unlimited
//                    if (pieceIndex % 2 == 0) {
//                        if (nextNearPiece instanceof Rook) {
//                            return true;
//                        }
//                    }
//
//                    //king check. single square in any direction.
//                    if (nextNearPiece instanceof King) {
//                        if ( Math.abs(nextNearPiece.currentRow - this.currentRow) <= 1 && //max 1 col, 1 row away to attack
//                                Math.abs(nextNearPiece.currentCol - this.currentCol) <= 1) {
//                            return true;
//                        }
//                    }
//
//                    //pawn check. attacks one square forward on left or right side.
//                    if (pieceIndex == pawnIndexOffset || pieceIndex == 2 + pawnIndexOffset) {
//                        if (nextNearPiece instanceof Pawn) {
//                            return true;
//                        }
//                    }
//
//                }
//            }
//        }
//
//
//
//
//        return false;
    }




    @Override
    String getShortName() {
        return "K";
    }

    @Override
    int getPointsValue() {
        return 200; //in reality, King is both infinite & low value as capture ends the game but it has some limited attacking & defensive qualities.
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♚" : "♔";
    }
}
