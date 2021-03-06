package game.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class King extends Piece {

    private boolean castled;
    private boolean queenSideCastled;

    King(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "King", row, col, gameBoard);
    }

//    King(Color color, String alphaStr, Piece[][] gameBoard) {
//        super(color, "King", alphaStr, gameBoard);
//    }

    @Override
    boolean move(int row, int col, int[] points) {

        castled = false;
        queenSideCastled = false;

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            System.out.println("out of bounds");
            return false;
        }

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) {
            return false;
        }

        int rowChange = Math.abs(currentRow - row);
        int colChange = Math.abs(currentCol - col);

        int homeRow = COLOR == Color.BLACK ? 0 : 7;

        if (rowChange > 1 || (colChange > 1 && currentRow != homeRow)) {
            System.out.println("king can only move one step at a time");
            return false;
        }

        //castling.
        if (colChange == 2) {
            boolean onQueenSide = col < 4;
            return checkCastling(onQueenSide);
        }

        //attacking
        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == ENEMY_COLOR) {

            gameBoard[row][col].captured = true;

            if (COLOR == Color.WHITE) {
                points[0] += gameBoard[row][col].VALUE;
            } else {
                points[1] += gameBoard[row][col].VALUE;
            }
        }

        //update board
        place(row, col);

        return true;
    }

    boolean isInCheck() {
        return isSquareUnderAttack(gameBoard, currentRow, currentCol, ENEMY_COLOR);
    }

    //castling is TECHNICALLY a King move, even though it also involves a Rook
    private boolean checkCastling(boolean queenSide) {


        if (currentCol != 4)
            return false; //col. 4 is king's initial spot. castling requires both king & rook to not have moved
        if (timesMoved > 0) return false;

        int homeRow = COLOR == Color.BLACK ? 0 : 7;

        if (currentRow != homeRow) return false;

        //blocked path checks up to the square before castling is possible, as it allows for the final square being
        //occupied by an enemy piece. so the specific final king position should be checked separately too.
//        if (queenSide) {
//            if (gameBoard[currentRow][2] != null) return false;
//        } else if (gameBoard[currentRow][6] != null) {
//            return false;
//        }
//

        int rookCol = 7; //default to king-side
        int newCol = 6;

        if (queenSide) {
            rookCol = 0;
            newCol = 2;
        }

        Piece rookToMove = gameBoard[currentRow][rookCol];

        if (!(rookToMove instanceof Rook)) return false;
        if (rookToMove.COLOR == ENEMY_COLOR) return false;
        if (rookToMove.timesMoved > 0) return false;

        if (isBlockedPath(currentRow, rookCol)) return false;

        int newRookCol = 5;

        if (queenSide) {
            newRookCol = 3;
            queenSideCastled = true;
        }


        //the King is not allowed to land in a spot that results in Check (technically true of all King moves)
        if (isSquareUnderAttack(gameBoard, currentRow, newCol, ENEMY_COLOR)) {
            return false;
        }

        //the King "jumps over" the Rook's final position, but the King may not do so if that square can be attacked
        if (isSquareUnderAttack(gameBoard, currentRow, newRookCol, ENEMY_COLOR)) {
            return false;
        }

        //move king.
        this.place(currentRow, newCol);

        //move rook.
        rookToMove.place(currentRow, newRookCol);

        castled = true;

        return true;


    }

    //determine whether the King has no possible way to escape from check
    boolean isCheckmated() {

        int adjacentSquares = 0;
        int friendlySquares = 0;

        //1. see if the king can move to an adjacent space that isn't under attack.
        for (int checkRow = currentRow - 1; checkRow < currentRow + 2; ++checkRow) {
            for (int checkCol = currentCol - 1; checkCol < currentCol + 2; ++checkCol) {

                if (Piece.outOfBounds(checkRow) || Piece.outOfBounds(checkCol)) continue;
                if (checkRow == currentRow && checkCol == currentCol) continue; //self spot

                ++adjacentSquares;

                //own color pieces block movement
                if (gameBoard[checkRow][checkCol] != null && gameBoard[checkRow][checkCol].COLOR == this.COLOR) {
                    ++friendlySquares;
                    continue;
                }

                if (!isSquareUnderAttack(gameBoard, checkRow, checkCol, ENEMY_COLOR)) {
                    return false; //a spot that is in bounds, not the same spot, free or an enemy piece, and not being attacked.
                }

            }
        }

        //all squares are blocked by own team pieces
        if (adjacentSquares == friendlySquares) {
            return false;
        }

        //2. determine the pieces that are attacking the king. if there is  only 1 attacking piece, check if that piece can be attacked.
        Integer[] checkRowAndCol = {currentRow, currentCol};
        ArrayList<Piece> attackers = findAttackingPieces(gameBoard, checkRowAndCol, ENEMY_COLOR, false);


        if (attackers.size() == 1) {
            Piece attackPiece = attackers.get(0);

            if (isSquareUnderAttack(gameBoard, attackPiece.currentRow, attackPiece.currentCol, this.COLOR)) {

                Integer[] attackerRowCol = {attackPiece.currentRow, attackPiece.currentCol};
                ArrayList<Piece> defenders = findAttackingPieces(gameBoard, attackerRowCol, this.COLOR, false);

                //if the only defending piece is the King, and the attack move is to a square that can be attacked by the enemy,
                //the King is checkmated.
                if (defenders.size() == 1 && defenders.get(0) == this) {
                    return isSquareUnderAttack(gameBoard, attackPiece.currentRow, attackPiece.currentCol, ENEMY_COLOR);
                }

                //if there is 1 defender that isn't the king, or more defenders possibly including the king
                if (defenders.size() > 0) {
                    return false;
                }

            }
//        }

            //3. if there is 1 piece only attacking the king, for the line of attack, see if any pieces can be moved into it
            // to block the move. (check each square of the line of attack to see if it can be attacked by king's color pieces.)

//        if (attackers.size() == 1) {
//            Piece attackPiece = attackers.get(0);

            //knight can jump over pieces so cannot be blocked
            if (attackPiece instanceof Knight) return true;

            int rowChange = attackPiece.currentRow < this.currentRow ? 1 : -1;
            int colChange = attackPiece.currentCol < this.currentCol ? 1 : -1;

            int nextAttRow = attackPiece.currentRow + rowChange;
            int nextAttCol = attackPiece.currentCol + colChange;

            //check one step forward at a time, moving towards the King. stop at King's location.
            while (nextAttRow != this.currentRow && nextAttCol != this.currentCol) {

                if (isSquareUnderAttack(gameBoard, nextAttRow, nextAttCol, this.COLOR)) {
                    Integer[] nextRowCol = {nextAttRow, nextAttCol};

                    //because pawn's attack movement is different, need to filter them out as this is a check
                    //for pieces to block an EMPTY space. pawns attack to a diagonal corner.
                    ArrayList<Piece> defenders = findAttackingPieces(gameBoard, nextRowCol, this.COLOR, false);

                    List<Piece> defendersWithoutPawn = defenders.stream()
                            .filter(d -> !(d instanceof Pawn || d == this)) //also filter out King, as it will be registered as attacking final adjacent square
                            .collect(Collectors.toList());

                    int totalDefenders = defendersWithoutPawn.size();

                    //check and add any pawns that can move straight forward to the current check spot
                    int pawnPrecedingRow = COLOR == Color.BLACK ? nextAttRow - 1 : nextAttRow + 1;

                    if (!Piece.outOfBounds(pawnPrecedingRow) && gameBoard[pawnPrecedingRow][nextAttCol] instanceof Pawn) {
                        if (gameBoard[pawnPrecedingRow][nextAttCol].COLOR == this.COLOR) {
                            return false;
                        }
                    }

                    if (totalDefenders > 0) {
                        return false;
                    }

                }

                nextAttRow += rowChange;
                nextAttCol += colChange;

            }

        }

        //if there are more than 2 pieces attacking the king, the only possible escape is to move the king. if we
        //have reached this point, checkmate is true.

        return true;
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

    public boolean isCastled() {
        return castled;
    }

    public boolean isQueenSideCastled() {
        return queenSideCastled;
    }

    @Override
    ArrayList<Integer[]> getValidMoves(String lastMoveStr) {

        ArrayList<Integer[]> validMovesList = new ArrayList<>();

        int[][] moveOffsets = {
                {-1, -1}, //top left direction
                {-1, 0},
                {-1, +1}, //top right
                {0, -1}, //left
                {0, +1}, //right
                {+1, -1}, //bottom left
                {+1, 0},
                {+1, +1}, //bottom right
        };

        for (int[] offsetRowCol : moveOffsets) {

            int checkRow = currentRow + offsetRowCol[0]; //NB as King moves one square only, this value is changed only once.
            int checkCol = currentCol + offsetRowCol[1];

            if (Piece.outOfBounds(checkRow) || Piece.outOfBounds(checkCol)) continue;

            //valid to move to empty space or enemy occupying square
            if (gameBoard[checkRow][checkCol] == null || gameBoard[checkRow][checkCol].COLOR == ENEMY_COLOR) {

                //king may not move to a spot that is being attacked
                if (!isSquareUnderAttack(gameBoard, checkRow, checkCol, ENEMY_COLOR)) {
                    Integer[] validMove = {checkRow, checkCol};
                    validMovesList.add(validMove);
                }
            }

        }

        if (checkCastling(true)) {
            Integer[] validMove = {currentRow, 2};
            validMovesList.add(validMove);
        }
        if (checkCastling(false)) {
            Integer[] validMove = {currentRow, 6};
            validMovesList.add(validMove);
        }


        return validMovesList;
    }
}
