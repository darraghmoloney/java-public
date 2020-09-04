package game.chess;

import java.util.Scanner;

class Pawn extends Piece {

    final static int WHITE_HOME_ROW = 7;
    final static int BLACK_HOME_ROW = 0;

    Pawn(Color color) {
        super(color, "Pawn");
    }

    Pawn(Color color, int row, int col) {
        super(color, "Pawn", row, col);
    }


    @Override
    public boolean move(Piece[][] gameBoard, int[] rowAndCol, int[] points) {

        int newRow = rowAndCol[0];
        int newCol = rowAndCol[1];

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

        if (COLOR == Color.WHITE && newRow >= currentRow ||
                COLOR == Color.BLACK && newRow <= currentRow
        ) {
            return false;
        }

        if (gameBoard[newRow][newCol] != null && gameBoard[newRow][newCol].COLOR == this.COLOR) {
            return false;
        }

        int enemyHomeIndex = COLOR == Color.WHITE ? BLACK_HOME_ROW : WHITE_HOME_ROW;

        if (newCol == currentCol) { //straight line progression


            if (gameBoard[newRow][newCol] != null) {
                System.out.println("can't move pawn - blocked");
                return false;
            }

            gameBoard[currentRow][currentCol] = null;


            //en passant check & handling.
            int passPts = checkEnPassant(gameBoard, newRow, newCol);

            if (passPts > 0) {
                if (COLOR == Color.WHITE) {
                    points[0] += passPts;
                } else {
                    points[1] += passPts;
                }
            }

            //convert to queen at enemy home row.
            //NB player can technically convert pawn to ANY piece, but the queen is the most powerful so why
            //would anyone choose a different one?
            if (newRow == enemyHomeIndex) {
                Piece newQueen = new Queen(this.COLOR, newRow, newCol);
                gameBoard[newRow][newCol] = newQueen;

                newQueen.timesMoved = timesMoved;
                return true;
            }

            currentRow = newRow;
            gameBoard[currentRow][currentCol] = this;

            ++timesMoved;
            return true;

        }

        //attacking to board left / right.
        if (newCol == currentCol - 1 || newCol == currentCol + 1) {

            if (gameBoard[newRow][newCol] == null) {
                System.out.println("cannot attack - no enemy piece at " + newRow + " " + newCol);
                return false;
            }

            if (this.COLOR == Color.WHITE && newRow == currentRow - 1 ||
                    this.COLOR == Color.BLACK && newRow == currentRow + 1) {

                if (gameBoard[newRow][newCol].COLOR == ENEMY_COLOR) {

                    gameBoard[newRow][newCol].captured = true;

                    if (COLOR == Color.WHITE) {
                        points[0] += gameBoard[newRow][newCol].VALUE;
                    } else {
                        points[1] += gameBoard[newRow][newCol].VALUE;
                    }

                    gameBoard[currentRow][currentCol] = null;

                    //convert to queen if enemy home row
                    if (newRow == enemyHomeIndex) {
                        Piece newQueen = new Queen(this.COLOR, newRow, newCol);
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

    private int checkEnPassant(Piece[][] gameBoard, int newRow, int newCol) {
        boolean enemyOnLeft = false;
        boolean enemyOnRight = false;

        int enemyValue = 0;

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

        if (!enemyOnLeft && !enemyOnRight) { return 0; }

        //en passant enemy on both sides, choose highest value enemy, else prompt if same.
        if (enemyOnLeft && enemyOnRight) {

            Piece leftEnemy = gameBoard[newRow][newCol - 1];
            Piece rightEnemy = gameBoard[newRow][newCol + 1];

            char choice = 'l';

            if (rightEnemy.VALUE > leftEnemy.VALUE) {
                choice = 'r';
            } else if (rightEnemy.VALUE == leftEnemy.VALUE) {
                System.out.print("en passant. capture left or right? (l/r): ");

                Scanner sc = new Scanner(System.in);
                String captureChoice = sc.next();

                if (captureChoice.length() > 0) {
                    choice = captureChoice.toLowerCase().charAt(0);

                    if (choice != 'l' && choice != 'r') {
                        choice =  Math.random() < 0.5 ? 'l' : 'r';
                    }
                }
            }

            if (choice == 'l') {
                leftEnemy.captured = true;
                enemyValue = leftEnemy.VALUE;
                gameBoard[newRow][newCol - 1] = null;
            } else {
                rightEnemy.captured = true;
                enemyValue = rightEnemy.VALUE;
                gameBoard[newRow][newCol + 1] = null;
            }
            return enemyValue;
        }

        //en passant - enemy on left side only.
        if (enemyOnLeft) {
            gameBoard[newRow][newCol - 1].captured = true;
            enemyValue = gameBoard[newRow][newCol].VALUE;
            gameBoard[newRow][newCol - 1] = null;
            return enemyValue;
        }

        //right side only.
        gameBoard[newRow][newCol + 1].captured = true;
        enemyValue = gameBoard[newRow][newCol].VALUE;
        gameBoard[newRow][newCol + 1] = null;

        return enemyValue;

    }


    @Override
    String getShortName() {
        return " ";
    }

    @Override
    int getPointsValue() {
        return 1;
    }

    @Override
    String getIcon() { return COLOR == Color.BLACK ? "♟︎" : "♙"; }

}