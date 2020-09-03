package game.chess;

import java.util.Scanner;

class Pawn extends Piece {

    final static  int WHITE_HOME_ROW = 7;
    final static int BLACK_HOME_ROW = 0;

    Pawn(Color color) {
        super(color, "Pawn");
    }

    Pawn(Color color, int row, int col) {
        super(color, "Pawn", row, col);
    }


    @Override
    public boolean move(Piece[][] gameBoard, int newRow, int newCol) {


        if (Piece.outOfBounds(newRow) || Piece.outOfBounds(newCol)) {
            return false;
        }

        if (Math.abs(currentRow - newRow) < 1 || Math.abs(currentRow - newRow) > 2) {
            return false;
        }

        if (Math.abs(currentRow - newRow) == 2 && timesMoved != 0) {
            System.out.println("can only move 2 squares on first move");
            return false;
        }

        if (newCol == currentCol) { //straight line progression
            

            if (gameBoard[newRow][newCol] != null) {
                System.out.println("can't move pawn - blocked");
                return false;
            }

            if (this.COLOR == Color.WHITE && newRow < currentRow) { //moving UP board

                gameBoard[currentRow][currentCol] = null;

                //en passant check & handling.
                checkEnPassant(gameBoard, newRow, newCol);

                //convert to queen at enemy home row.
                //NB player can technically convert pawn to ANY piece, but the queen is the most powerful so why
                //would anyone choose a different one?
                if (newRow == BLACK_HOME_ROW) {
                    Piece newQueen = new Queen(Color.WHITE, newRow, newCol);
                    gameBoard[newRow][newCol] = newQueen;

                    newQueen.timesMoved = timesMoved;
                    return true;
                }

                currentRow = newRow;
                gameBoard[currentRow][currentCol] = this;

                ++timesMoved;
                return true;
            }

            if (this.COLOR == Color.BLACK && newRow > currentRow) {
                gameBoard[currentRow][currentCol] = null;

                //en passant check & handling.

                checkEnPassant(gameBoard, newRow, newCol);

                //queenifying
                if (newRow == WHITE_HOME_ROW) {
                    Piece newQueen = new Queen(Color.BLACK, newRow, newCol);
                    gameBoard[newRow][newCol] = newQueen;

                    newQueen.timesMoved = timesMoved;
                    return true;
                }


                currentRow = newRow;
                gameBoard[currentRow][currentCol] = this;

                ++timesMoved;
                return true;
            }

        }

        //attacking to board left.
        if (newCol == currentCol - 1) {

            if (gameBoard[newRow][newCol] == null) {
                System.out.println("cannot attack - no enemy piece at " + newRow + " " + newCol);
                return false;
            }

            if (this.COLOR == Color.WHITE && newRow == currentRow - 1) {

                if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

                    gameBoard[newRow][newCol].captured = true;
                    gameBoard[currentRow][currentCol] = null;

                    //convert to queen if enemy home row
                    if (newRow == BLACK_HOME_ROW) {
                        Piece newQueen = new Queen(Color.WHITE, newRow, newCol);
                        gameBoard[newRow][newCol] = newQueen;

                        newQueen.timesMoved = timesMoved;
                        return true;
                    }


                    currentRow = newRow;
                    currentCol = newCol;
                    gameBoard[currentRow][currentCol] = this;

                    ++timesMoved;
                    return true;

                }
            }

            if (this.COLOR == Color.BLACK && newRow == currentRow + 1) {

                if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

                    gameBoard[newRow][newCol].captured = true;
                    gameBoard[currentRow][currentCol] = null;

                    //queenifying
                    if (newRow == WHITE_HOME_ROW) {
                        Piece newQueen = new Queen(Color.BLACK, newRow, newCol);
                        gameBoard[newRow][newCol] = newQueen;

                        newQueen.timesMoved = timesMoved;
                        return true;
                    }

                    currentRow = newRow;
                    currentCol = newCol;
                    gameBoard[currentRow][currentCol] = this;

                    ++timesMoved;
                    return true;

                }
            }

        }

        //attacking to board right.
        if (newCol == currentCol + 1) {

            if (gameBoard[newRow][newCol] == null) {
                System.out.println("cannot attack - no enemy piece at " + newRow + " " + newCol);
                return false;
            }

            if (this.COLOR == Color.WHITE && newRow == currentRow - 1) {

                if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

                    gameBoard[currentRow][currentCol] = null;

                    if (newRow == BLACK_HOME_ROW) {
                        Piece newQueen = new Queen(Color.WHITE, newRow, newCol);
                        gameBoard[newRow][newCol] = newQueen;

                        newQueen.timesMoved = timesMoved;
                        return true;
                    }

                    currentRow = newRow;
                    currentCol = newCol;
                    gameBoard[currentRow][currentCol] = this;

                    ++timesMoved;
                    return true;
                }
            }

            if (this.COLOR == Color.BLACK && newRow == currentRow + 1) {

                if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

                    gameBoard[currentRow][currentCol] = null;

                    if (newRow == WHITE_HOME_ROW) {
                        Piece newQueen = new Queen(Color.BLACK, newRow, newCol);
                        gameBoard[newRow][newCol] = newQueen;

                        newQueen.timesMoved = timesMoved;
                        return true;
                    }

                    currentRow = newRow;
                    currentCol = newCol;
                    gameBoard[currentRow][currentCol] = this;

                    ++timesMoved;
                    return true;
                }
            }

        }

        return false;
    }

    private void checkEnPassant(Piece[][]gameBoard, int newRow, int newCol) {
        boolean enemyOnLeft = false;
        boolean enemyOnRight = false;

        if ((newCol - 1) >= 0 &&
                gameBoard[newRow][newCol - 1] != null &&
                gameBoard[newRow][newCol - 1].COLOR == ENEMY_COLOR) {
            enemyOnLeft = true;
        }

        if ((newCol + 1) < 8 &&
                gameBoard[newRow][newCol + 1] != null &&
                gameBoard[newRow][newCol + 1].COLOR == ENEMY_COLOR) {
            enemyOnRight = true;
        }

        //en passant enemy on both sides, choose highest value enemy, else prompt if same.
        if (enemyOnLeft && enemyOnRight) {

            Piece leftEnemy = gameBoard[newRow][newCol - 1];
            Piece rightEnemy = gameBoard[newRow][newCol + 1];

            if (leftEnemy.VALUE > rightEnemy.VALUE) {
                leftEnemy.captured = true;
                gameBoard[newRow][newCol - 1] = null;
            } else if (rightEnemy.VALUE > leftEnemy.VALUE) {
                rightEnemy.captured = true;
                gameBoard[newRow][newCol + 1] = null;
            } else {
                System.out.print("en passant. capture left or right? (l/r): ");
                Scanner sc = new Scanner(System.in);
                String captureChoice = sc.next();

                if (captureChoice.length() > 0) {

                    char choice = captureChoice.toLowerCase().charAt(0);

                    if (choice == 'l') {
                        leftEnemy.captured = true;
                        gameBoard[newRow][newCol - 1] = null;
                    }
                    else if (choice == 'r') {
                        rightEnemy.captured = true;
                        gameBoard[newRow][newCol + 1] = null;
                    }
                    else {
                        if (Math.random() < 0.5) {
                            leftEnemy.captured = true;
                            gameBoard[newRow][newCol - 1] = null;
                        } else {
                            rightEnemy.captured = true;
                            gameBoard[newRow][newCol + 1] = null;
                        }
                    }
                }
            }
        }

        //en passant - enemy on one side only.
        if (enemyOnLeft & !enemyOnRight) {
            gameBoard[newRow][newCol - 1].captured = true;
            gameBoard[newRow][newCol - 1] = null;
        }

        if (enemyOnRight & !enemyOnLeft) {
            gameBoard[newRow][newCol + 1].captured = true;
            gameBoard[newRow][newCol + 1] = null;
        }

    }


    @Override
    String getShortName() {
        return " ";
    }

    @Override
    int getPointsValue() {
        return 1;
    }

}