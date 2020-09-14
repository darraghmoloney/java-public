package game.chess;


public class BoardBuilder {

    static Piece[][] makeBoard() {

        String[][] gameArray = {
                //a         b           c       d       e           f       g           h
        /* 8 */ {"Rb",    "Nb",     "Bb",     "Qb",     "Kb",     "Bb",     "Nb",     "Rb"}, /* 8 */
        /* 7 */ {" b",    " b",     " b",     " b",     " b",     " b",     " b",     " b"}, /* 7 */
        /* 6 */ {"--",    "--",     "--",     "--",     "--",     "--",     "--",     "--"}, /* 6 */
        /* 5 */ {"--",    "--",     "--",     "--",     "--",     "--",     "--",     "--"}, /* 5 */
        /* 4 */ {"--",    "--",     "--",     "--",     "--",     "--",     "--",     "--"}, /* 4 */
        /* 3 */ {"--",    "--",     "--",     "--",     "--",     "--",     "--",     "--"}, /* 3 */
        /* 2 */ {" w",    " w",     " w",     " w",     " w",     " w",     " w",     " w"}, /* 2 */
        /* 1 */ {"Rw",    "Nw",     "Bw",     "Qw",     "Kw",     "Bw",     "Nw",     "Rw"}, /* 1 */
                //a         b           c       d       e           f       g           h
        };

        return makeBoardFromArray(gameArray);

    }

    //creates & populates a game board of Pieces based on a String array containing their location and short names.
    // useful for testing, etc.
    private static Piece[][] makeBoardFromArray(String[][] pieceArray) {

        Piece[][] gameBoard = new Piece[8][8];

        for (int i = 0; i < gameBoard.length; ++i) {
            for (int j = 0; j < gameBoard[i].length; ++j) {

                String shortName = pieceArray[i][j];
                if (shortName == null || shortName.length() < 2) continue;

                makePieceFromShortName(shortName, i, j, gameBoard);
            }

        }

        return gameBoard;
    }

    //note: Pawn pieces are represented by a "space" in their short name, as they have no specific notation
    private static void makePieceFromShortName(String pieceShortName, int row, int col, Piece[][] gameBoard) {

        char shortName = pieceShortName.charAt(0);
        Color color = pieceShortName.charAt(1) == 'b' ? Color.BLACK : Color.WHITE;

        switch (shortName) {
            case 'K':
                new King(color, row, col, gameBoard);
                break;
            case 'Q':
                new Queen(color, row, col, gameBoard);
                break;
            case 'N':
                new Knight(color, row, col, gameBoard);
                break;
            case 'R':
                new Rook(color, row, col, gameBoard);
                break;
            case 'B':
                new Bishop(color, row, col, gameBoard);
                break;
            case ' ':
                new Pawn(color, row, col, gameBoard);
                break;
        }

    }

}
