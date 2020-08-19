package com.company;

import java.util.*;

public class Snakey {

    final static Scanner sc = new Scanner(System.in);

    private int numWalls;
    private int points = 0;

    private final Deque<PlaceMarker> snakePieces;

    private static final List<String> validChoices = new ArrayList<>(Arrays.asList("w", "s", "a", "d", "q"));

    private final char[][] board = {
            "------------".toCharArray(),
            "-----------|".toCharArray(),
            "-----------|".toCharArray(),
            "|-----------".toCharArray(),
            "|-----------".toCharArray(),
            "|-----------".toCharArray(),
            "----|-------".toCharArray(),
            "----|-------".toCharArray(),
            "------------".toCharArray(),
            "------------".toCharArray(),
            "--------|---".toCharArray(),
            "-----------|".toCharArray(),
    };

    public Snakey() {

        for (char[] row : board) {
            for (char col : row) {
                if (col == Icon.WALL.symbol) {
                    ++numWalls;
                }
            }
        }

        snakePieces = new ArrayDeque<>();

        snakePieces.add(new PlaceMarker(5, 3));
        snakePieces.add(new PlaceMarker(5, 2));
        snakePieces.add(new PlaceMarker(5, 1));
        snakePieces.add(new PlaceMarker(5, 0));

        addSnakeToBoard();
        addRandomFoodPiece();

    }


    private void addSnakeToBoard() {
        for (PlaceMarker piece : snakePieces) {

            int row = piece.getRow();
            int col = piece.getCol();

            board[row][col] = Icon.SNAKE.symbol;

        }
    }

    private void addRandomFoodPiece() {

        final int numFood = 1; //only 1 piece of food on the board at a time. might change in different modes in future.

        int totalFreeSpots = (board.length * board[0].length) - snakePieces.size() - numWalls - numFood;

        if (totalFreeSpots < 1) {
            System.out.println("game over. you won!"); //this method is only called if game over didn't already occur
            return;
        }

        // random row & col. could also use an int location from the board area
        // with division for row & mod for column but this is easier.
        int foodRow = (int) (Math.random() * board.length);
        int foodCol = (int) (Math.random() * board[0].length);

        while (board[foodRow][foodCol] != Icon.BLANK.symbol) {
            foodRow = (int) (Math.random() * board.length);
            foodCol = (int) (Math.random() * board[0].length);
        }

        board[foodRow][foodCol] = Icon.FOOD.symbol;

    }

    private void display() {

        for (char[] line : board) {
            for (char square : line) {
                System.out.print(square + " ");
            }
            System.out.println();
        }

    }

    public void play() {

        boolean keepPlaying = true; //won, or game over - updated by move method

        String previousMove = "d"; //default to moving right

        while (keepPlaying) {

            display();

            System.out.println("points: " + points);

            String choice;
            String directionStr = getDirString(previousMove);

            System.out.println("current move: " + previousMove + " (" + directionStr + ")"); //continue with last move if no new input (only enter)
            System.out.println("[enter] keep going");
            System.out.println("[w] up, [s] down, [a] left, [d] right");
            System.out.print("[q] quit: ");

            choice = sc.nextLine();

            if (choice.length() == 0 || !validChoices.contains(choice.substring(0, 1).toLowerCase())) {
                choice = previousMove;
                System.out.print("continuing ");
            }

            previousMove = choice;

            choice = choice.substring(0, 1).toLowerCase();

            if (choice.equals("q")) {
                keepPlaying = false;
            } else {

                System.out.print( "going " + getDirString(choice) + " " );

                int[] rowColChange = getMove(choice);
                int rowChange = rowColChange[0];
                int colChange = rowColChange[1];

                keepPlaying = moveSnake(rowChange, colChange);

                System.out.println();

            }

        }

    }

    private static String getDirString(String choice) {
        switch (choice) {
            case "w":
                return "up";
            case "s":
                return "down";
            case "a":
                return "left";
            case "d":
                return  "right";
        }

        return "";
    }

    /**
     * Provides the 2-D array offset for different directional moves, so that
     * the new location of the snake's head piece can be calculated and updated on the board.
     *
     * @param choice The String form of the desired move. Uses WSAD for Up, Down, Left and Right.
     *
     * @return An int array of length 2 with the change in row value in position 0 and the change in col
     * value in position 1.
     */
    private static int[] getMove(String choice) {

        int[] rowColChange = new int[2];

        switch (choice) {
            case "w": //up
                rowColChange[0] = -1;
                break;
            case "s": //down
                rowColChange[0] = 1;
                break;
            case "a": //left
                rowColChange[1] = -1;
                break;
            case "d": //right
                rowColChange[1] = 1;
                break;
        }

        return rowColChange;

    }

    /**
     *  Move the snake on the board by checking if the move wanted is valid,
     *  adding the location of the new head piece of the snake to its list,
     *  and removing the tail in the case that no food was eaten. If food
     *  was eaten, the tail is not removed and the snake increases in size by 1.
     *
     * @param rowChange An int representing the change in row. For example, -1 means move 1 row back.
     * @param colChange An int representing the change in column. For example, 1 means the column to the right.
     *
     * @return A boolean value representing the gameplay status. Any attempt to
     * move in an invalid way (into a wall, or the snake itself) returns false.
     * Likewise, a valid move returns true. These values are used directly to
     * break out of the gameplay loop in the main method.
     * */
    private boolean moveSnake(int rowChange, int colChange) {
        int min = 0;
        int max = board.length - 1; //NB: requiring a symmetrical board here (same min/max values for row & column)

        PlaceMarker head = snakePieces.getFirst();

        System.out.print("h: " + head.getRow() + ", " + head.getCol());

        int newRow = head.getRow() + rowChange;
        int newCol = head.getCol() + colChange;

        //fix at boundaries by overlapping.
        //WALLS are marked with a special character, if they exist.
        if (newRow < min) {
            newRow = max; //going up, back to bottom
        }

        if (newRow > max) {
            newRow = min; //going down, back to top
        }

        if (newCol < min) {
            newCol = max; //heading left, hit boundary
        }

        if (newCol > max) {
            newCol = min; //heading right, hit boundary
        }

        if (board[newRow][newCol] == Icon.SNAKE.symbol || board[newRow][newCol] == Icon.WALL.symbol) {
            System.out.println();
            display();

            String thingSnakeHit = board[newRow][newCol] == Icon.SNAKE.symbol ? "self" : "wall";

            System.out.println("bumped into " + thingSnakeHit + ". game over. you got " + points + " pts." );

            return false;
        }

        boolean snakeAte = board[newRow][newCol] == Icon.FOOD.symbol;

        PlaceMarker newHead = new PlaceMarker(newRow, newCol); //if snake didn't eat, could also change tail coordinates, & re-add it to first spot

        snakePieces.addFirst(newHead);
        board[newRow][newCol] = Icon.SNAKE.symbol;

        if (snakeAte) { //if food piece, no need to remove tail as snake has been lengthened by 1
            System.out.println("\tnyom nyom");
            ++points;
            addRandomFoodPiece(); //tries to add new piece of food, and declares win for player if it can't
        } else {
            System.out.println();
            PlaceMarker removedTail = snakePieces.removeLast(); //remove tail piece & mark board spot as empty
            board[removedTail.getRow()][removedTail.getCol()] = Icon.BLANK.symbol; //snake redraw doesn't redraw whole board, so this is necessary
        }


        return true;

    }

    enum Icon {

        BLANK('-'),
        FOOD('O'),
        SNAKE('s'),
        WALL('|');

        private final char symbol;

        Icon(char symbol) {
            this.symbol = symbol;
        }

    }


}
