package com.company;

import java.util.*;

public class Snakey {

    final static Scanner sc = new Scanner(System.in);

    private int points = 0;

    private final Deque<PlaceMarker> snakePieces;

    private final List<PlaceMarker> wallLocations; //to reset board to initial state later.
    private final List<PlaceMarker> tunnelLocations;
    private final Deque<PlaceMarker> startSnakeLocations;
    private final List<PlaceMarker> freeSpots; //for adding food pieces efficiently.
    private final int longestRowLength;

    private static final List<String> validChoices = new ArrayList<>(Arrays.asList("w", "s", "a", "d", "q"));
    private static final Random rand = new Random();


    private final char[][] board = {

            "------------".toCharArray(),
            "------".toCharArray(),
            "-----------#".toCharArray(),
            "-----------#".toCharArray(),
            "------".toCharArray(),
            "#-----------".toCharArray(),
            "#-----------".toCharArray(),
            "#-----------".toCharArray(),
            "----#-------".toCharArray(),
            "----#-------".toCharArray(),
            "------".toCharArray(),
            "  ----------".toCharArray(),
            "  ----------".toCharArray(),
            "------".toCharArray(),
            "------".toCharArray(),
            "---- ---#---".toCharArray(),
            "-----------#".toCharArray(),
            "------".toCharArray(),

    };

    public Snakey() {

        wallLocations = new ArrayList<>();
        tunnelLocations = new ArrayList<>();
        startSnakeLocations = new ArrayDeque<>();
        freeSpots = new LinkedList<>();

        int longest = 0;

        //records the wall locations on the pre-made map, for future board resets.
        //could also dynamically add them to the board, like the snake pieces.
        for (int i = 0; i < board.length; ++i) {

            if (board[i].length > longest) {
                longest = board[i].length;
            }

            for (int j = 0; j < board[i].length; ++j) {

                PlaceMarker nextPlace = new PlaceMarker(i, j);

                if (board[i][j] == Icon.WALL.symbol) {
                    wallLocations.add(nextPlace);
                } else if (board[i][j] == Icon.TUNNEL.symbol) {
                    tunnelLocations.add(nextPlace);
                } else {
                    freeSpots.add(nextPlace);
                }

            }

        }

        longestRowLength = longest;

        snakePieces = new ArrayDeque<>();
/*
        snakePieces.add(new PlaceMarker(5, 3)); //head of snake
        snakePieces.add(new PlaceMarker(5, 2));
        snakePieces.add(new PlaceMarker(5, 1));
        snakePieces.add(new PlaceMarker(5, 0));
*/

        snakePieces.add(new PlaceMarker(0, 1));
        snakePieces.add(new PlaceMarker(0, 0));

        startSnakeLocations.addAll(snakePieces); //for board reset

        addSnakeToBoard();
        addRandomFoodPiece();

    }


    private void addSnakeToBoard() {
        addItemsToBoard(snakePieces, Icon.SNAKE_BODY);

        PlaceMarker snakeHeadLoc = snakePieces.getFirst();
        board[snakeHeadLoc.getRow()][snakeHeadLoc.getCol()] = Icon.SNAKE_HEAD.symbol;
    }

    private void addWallsToBoard() {
        addItemsToBoard(wallLocations, Icon.WALL);
    }

    private void addTunnelsToBoard() {
        addItemsToBoard(tunnelLocations, Icon.TUNNEL);
    }

    private void addItemsToBoard(Collection<PlaceMarker> placeMarkers, Icon icon) {
        for (PlaceMarker piece : placeMarkers) {

            int row = piece.getRow();
            int col = piece.getCol();

            board[row][col] = icon.symbol;

            if (icon != Icon.BLANK) {
                freeSpots.remove(new PlaceMarker(row, col));
            }

        }
    }

    private void resetBoard() {

        for (char[] chars : board) {
            Arrays.fill(chars, Icon.BLANK.symbol);
        }

        addWallsToBoard();
        addTunnelsToBoard();

        snakePieces.clear();
        snakePieces.addAll(startSnakeLocations);

        addSnakeToBoard();
        addRandomFoodPiece();

    }

    private boolean addRandomFoodPiece() {

        if (freeSpots.size() < 1) {
            System.out.println();
            display();
            System.out.println("game over. you won! you got " + points + " pts."); //this method is only called if game over didn't already occur
            return false;
        }

        int nextFoodIndex = rand.nextInt(freeSpots.size()); //random only generated for available spots. no wasted / looping random calls
        PlaceMarker foodPlace = freeSpots.get(nextFoodIndex);
        board[foodPlace.getRow()][foodPlace.getCol()] = Icon.FOOD.symbol;

        return true;
    }

    private void display() {

        System.out.print("   ");

        for (int i = 0; i < longestRowLength; ++i) { //col markers a,b,c...z
            System.out.print("_" + (char) (i % 26 + 'a'));
        }

        System.out.println();

        int i = -1;

        for (char[] line : board) {
            System.out.print((++i < 10 ? " " : "") + i + "| "); //row markers 0-9 (repeat at 10)

            for (char square : line) {
                System.out.print(square + " ");
            }

            System.out.println();

        }

    }

    public void play() {

        boolean keepPlaying = true; //won, or game over - updated by move method
        boolean playerQuitGame = false; //prompt to play again only in game over condition

        String previousMove = "d"; //default to moving right


        while (keepPlaying) {

            display();

            PlaceMarker first = snakePieces.getFirst();
            int currentRow = first.getRow();
            char currentCol = (char) (first.getCol() % 26 + 'a');

            System.out.print("points: " + points + ", location: " + currentCol + ", " + currentRow);

            boolean inTunnel = (first.getCol() >= board[currentRow].length) || (board[first.getRow()][first.getCol()] == Icon.TUNNEL.symbol);

            if (inTunnel) {
                System.out.print(" [IN TUNNEL]");
            }

            System.out.println();

            String choice;
            String directionStr = getDirString(previousMove);

            System.out.println("current move: " + previousMove + " (" + directionStr + ")"); //continue with last move if no new input (only enter)
            System.out.println("[enter] keep going");

            if (!inTunnel) {
                System.out.println("[w] up, [s] down, [a] left, [d] right");
            } else {
                System.out.println();
            }

            System.out.print("[q] quit: ");

            choice = sc.nextLine();

            if (choice.length() == 0 || !validChoices.contains(choice.substring(0, 1).toLowerCase())) {
                choice = previousMove;
            }

            //disallow changing direction in tunnel as player cannot see snake
            if (inTunnel) {
                choice = previousMove;
            }

            previousMove = choice;

            choice = choice.substring(0, 1).toLowerCase();


            if (choice.equals("q")) {
                keepPlaying = false;
                playerQuitGame = true;
            } else {

                int[] rowColChange = getMove(choice);
                int rowChange = rowColChange[0];
                int colChange = rowColChange[1];

                keepPlaying = moveSnake(rowChange, colChange);

                System.out.println();

            }

        }

        if (!playerQuitGame) {

            System.out.print("play again? (Y/n) ");

            String againChoice = sc.nextLine();

            boolean playAgain = true;

            if (againChoice.length() > 0) {
                playAgain = againChoice.toLowerCase().charAt(0) == 'y';
            }

            if (playAgain) {
                resetBoard();
                play();
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
                return "right";
        }

        return "";
    }

    /**
     * Provides the 2-D array offset for different directional moves, so that
     * the new location of the snake's head piece can be calculated and updated on the board.
     *
     * @param choice The String form of the desired move. Uses W,S,A,D for Up, Down, Left and Right.
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
     * Move the snake on the board by checking if the move wanted is valid,
     * adding the location of the new head piece of the snake to its list,
     * and removing the tail in the case that no food was eaten. If food
     * was eaten, the tail is re-added and the snake increases in size by 1.
     *
     * @param rowChange An int representing the change in row. For example, -1 means move 1 row back.
     * @param colChange An int representing the change in column. For example, 1 means the column to the right.
     * @return A boolean value representing the gameplay status. Any attempt to
     * move in an invalid way (into a wall, or the snake itself) returns false.
     * Likewise, a valid move returns true. These values are used directly to
     * break out of the gameplay loop in the main method.
     */
    private boolean moveSnake(int rowChange, int colChange) {

        boolean continuePlay = true;

        PlaceMarker head = snakePieces.getFirst();

        //fix at boundaries by wrapping around.
        //WALLS are marked with a special character, if they exist.
        int newRow = head.getRow();

        if (rowChange != 0) {
            newRow = wrapNextLocation(head.getRow() + rowChange, board.length - 1);
        }

        int newCol = head.getCol();

        if (colChange != 0) {
            newCol = wrapNextLocation(head.getCol() + colChange, longestRowLength - 1);
        }

        PlaceMarker newHead = new PlaceMarker(newRow, newCol); //if snake didn't eat, could also change tail coordinates, & re-add it to first spot
        snakePieces.addFirst(newHead);

        PlaceMarker removedTail = snakePieces.removeLast(); //remove tail piece & mark board spot as empty


        //Handling jagged game board dimensions by only drawing visible spots for the snake.
        //(treat the snake as if it is in a tunnel rather than teleporting it to the closest valid location
        //instantly)
        //NB "tunnels" do not cause GAME OVER collisions - this would be unfair.
        boolean snakeAte = false;

        if (newCol < board[newRow].length && board[newRow][newCol] != Icon.TUNNEL.symbol) {

            //game over check
            if (board[newRow][newCol] == Icon.SNAKE_BODY.symbol ||
                    board[newRow][newCol] == Icon.SNAKE_HEAD.symbol || //(<- unlikely, but if snake is length 1 this is possible)
                    board[newRow][newCol] == Icon.WALL.symbol) {
                System.out.println();
                display();

                String thingSnakeHit = board[newRow][newCol] == Icon.WALL.symbol ? "wall" : "self";

                System.out.println("bumped into " + thingSnakeHit + ". game over. you got " + points + " pts.");

                return false;
            }

            snakeAte = (board[newRow][newCol] == Icon.FOOD.symbol);

            freeSpots.remove(newHead);
            board[newRow][newCol] = Icon.SNAKE_HEAD.symbol;

            if (snakeAte) { //if food piece, no need to remove tail as snake has been lengthened by 1
                System.out.println("\tnyom nyom");
                ++points;
                continuePlay = addRandomFoodPiece(); //tries to add new piece of food, and declares win for player if it can't
                snakePieces.add(removedTail);
            } else {
                System.out.println();
            }

        }

        if (head.getCol() < board[head.getRow()].length && board[head.getRow()][head.getCol()] != Icon.TUNNEL.symbol) {
            //change old head to snake body icon
            board[head.getRow()][head.getCol()] = Icon.SNAKE_BODY.symbol;
        }


        if (!snakeAte && removedTail.getCol() < board[removedTail.getRow()].length && board[removedTail.getRow()][removedTail.getCol()] != Icon.TUNNEL.symbol) {
            board[removedTail.getRow()][removedTail.getCol()] = Icon.BLANK.symbol; //snake redraw doesn't redraw whole board, so this is necessary
            freeSpots.add(removedTail);
        }


        return continuePlay;

    }

    /**
     * Fix row or column locations that go out of bounds by wrapping around.
     *
     * @param rowOrCol The new location to check and change if needed.
     * @param max      The length of the board (if row changes being checked) or length of the row (if columns)
     *                 - that is, one space more than the final index.
     * @return Returns the in-bounds location, which is wrapped around if the value is at either max or min to its equivalent
     * opposite, otherwise returning the start value.
     */
    private int wrapNextLocation(int rowOrCol, int max) {

        int min = 0;

        if (rowOrCol < min) {
            return max; //going up, back to bottom
        }

        if (rowOrCol > max) {
            return min; //going down, back to top
        }

        return rowOrCol;
    }

    enum Icon {

        BLANK('-'),
        FOOD('O'),
        SNAKE_BODY('s'),
        SNAKE_HEAD('S'),
        WALL('#'),
        TUNNEL(' ');

        private final char symbol;

        Icon(char symbol) {
            this.symbol = symbol;
        }

    }


}
