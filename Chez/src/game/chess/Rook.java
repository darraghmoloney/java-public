package game.chess;


import java.util.ArrayList;

class Rook extends Piece {

    Rook(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Rook", row, col, gameBoard);
    }

//    Rook(Color color, String alphaLoc, Piece[][] gameBoard) {
//        super(color, "Rook", alphaLoc, gameBoard);
//    }

    @Override
    boolean move(int row, int col, int[] points) {

        if (Piece.outOfBounds(row) || Piece.outOfBounds(col)) {
            System.out.println("out of bounds");
            return false;
        }

        //can only change the col OR the row, NOT BOTH. straight lines only.
        if ((currentCol - col) != 0 && (currentRow - row) != 0) {
            System.out.println("rooks only move in straight lines");
            return false;
        }

        if (gameBoard[row][col] != null && gameBoard[row][col].COLOR == this.COLOR) {
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

//        ++timesMoved;
        return true;

    }



    @Override
    String getShortName() {
        return "R";
    }

    @Override
    int getPointsValue() {
        return 5;
    }

    @Override
    String getIcon() {
        return COLOR == Color.BLACK ? "♜" : "♖";
    }

    @Override
    ArrayList<Integer[]> getValidMoves(String lastMoveStr) {

        ArrayList<Integer[]> validMovesList = new ArrayList<>();

        int[][] moveOffsets = {
                {-1,0}, //up
                {+1,0}, //down
                {0,-1}, //left
                {0,+1}, //right
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
