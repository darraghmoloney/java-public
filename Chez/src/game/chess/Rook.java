package game.chess;


class Rook extends Piece {

    Rook(Color color, int row, int col, Piece[][] gameBoard) {
        super(color, "Rook", row, col, gameBoard);
    }


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

        ++timesMoved;
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

}
