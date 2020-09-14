package game.chess;

import java.util.ArrayList;

class Bishop extends Piece {


    Bishop(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Bishop", row, col, gameBoard);
    }

//    Bishop(Color color, String alphaStr, Piece[][] gameBoard) {
//        super(color, "Bishop", alphaStr, gameBoard);
//    }

    @Override
    boolean move(int row, int col, int[] points) {

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            System.out.println("out of bounds");
            return false;
        }

        //check for same place - no movement, or straight-up / across movement.
        if (row == currentRow || col == currentCol) {
            return false;
        }

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) {
            return false;
        }

        int totalRowChange = Math.abs(currentRow - row);
        int totalColChange = Math.abs(currentCol - col);

        //as Bishop moves diagonally in straight lines, both row & col must change at 1:1 rate.
        if (totalRowChange != totalColChange) {
            return false;
        }

        if (isBlockedPath(row, col)) {
            return false;
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

    @Override
    String getShortName() {
        return "B";
    }

    @Override
    int getPointsValue() {
        return 3;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♝" : "♗";
    }

    @Override
    ArrayList<Integer[]> getValidMoves(String lastMoveStr) {

        ArrayList<Integer[]> validMovesList = new ArrayList<>();

        int[][] moveOffsets = {
                {-1,-1}, //top left direction
                {-1,+1}, //top right
                {+1,-1}, //bottom left
                {+1,+1}, //bottom right
        };

        for (int[] offsetRowCol : moveOffsets) {

            int checkRow = currentRow + offsetRowCol[0];
            int checkCol = currentCol + offsetRowCol[1];

            while (!Piece.outOfBounds(checkRow) && !Piece.outOfBounds(checkCol)) {

                //valid to move to empty space or enemy occupying square
                if (gameBoard[checkRow][checkCol] == null || gameBoard[checkRow][checkCol].COLOR == ENEMY_COLOR) {
                    Integer[] validMove = {checkRow, checkCol};
                    validMovesList.add(validMove);
                }

                //drop out of loop when either own piece is found (blocking) or enemy piece (captured in one move and stopping)
                if (gameBoard[checkRow][checkCol] != null) break;

                checkRow += offsetRowCol[0];
                checkCol += offsetRowCol[1];
            }


        }


        return validMovesList;
    }
}
