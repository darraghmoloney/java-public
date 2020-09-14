package game.chess;

import java.util.ArrayList;

class Queen extends Piece {


    Queen(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Queen", row, col, gameBoard);
    }

//    Queen(Color color, String alphaStr, Piece[][] gameBoard) {
//        super(color, "Queen", alphaStr, gameBoard);
//    }

    @Override
    boolean move(int row, int col, int[] points) {

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            return false;
        }

        int rowChange = Math.abs(currentRow - row); //absolute value as may move "back" in one of the directions
        int colChange = Math.abs(currentCol - col);

        if (rowChange != 0 && colChange != 0) { //diagonal
            if (rowChange != colChange) { //asymmetric change not possible
                System.out.println("straight-line diagonal moves only");
                return false;
            }
        }

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) { //same color piece in that spot
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
        return "Q";
    }

    @Override
    int getPointsValue() {
        return 9;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♛" : "♕";
    }

    @Override
    ArrayList<Integer[]> getValidMoves(String lastMoveStr) {

        ArrayList<Integer[]> validMovesList = new ArrayList<>();

        int[][] moveOffsets = {
                {-1,-1}, //top left direction
                {-1, 0},
                {-1,+1}, //top right
                {0, -1}, //left
                {0, +1}, //right
                {+1,-1}, //bottom left
                {+1, 0},
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
