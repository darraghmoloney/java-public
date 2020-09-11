package game.chess;

public class BoardBuilder {

    static Piece[][] makeBoard() {

        Piece[][] gameBoard = new Piece[8][8];

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

        return gameBoard;

    }

}
