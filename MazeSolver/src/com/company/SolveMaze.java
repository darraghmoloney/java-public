package com.company;

import java.util.ArrayList;

public class SolveMaze {

    private final int[][] maze;
    private final boolean[][] visitable; //if spot is not a wall, or unvisited, this is set to true
    private final boolean[][] alreadyVisited; //for printing "cleared" mark in maze display output

    private final int totalRows; //for bounds checking
    private final int totalCols;

    private boolean foundExit; //end / break conditions
    private int unvisitedCount;

    private static final int WALL_MARKER = 1; //for logic only - maze output display uses Strings from getMazeStringIcon()
    private static final int START_MARKER = 2;
    private static final int END_MARKER = 3;

    private static final int PRINT_DELAY = 700; //milliseconds between maze re-print so changes can be seen


    public SolveMaze(int[][] maze) {
        this.maze = maze;
        this.totalRows = maze.length;
        this.totalCols = maze[0].length;
        this.visitable = new boolean[maze.length][maze[0].length];
        this.alreadyVisited = new boolean[maze.length][maze[0].length];
    }

    public void solve() {

        Integer startRow = null;
        Integer startCol = null;

        for (int i = 0; i < maze.length; ++i) {
            for (int j = 0; j < maze[i].length; ++j) {
                if (maze[i][j] != WALL_MARKER) {
                    visitable[i][j] = true;
                    unvisitedCount++;
                }

                if (maze[i][j] == START_MARKER && startRow == null) { //set start position at FIRST occurrence of 2
                    startRow = i;
                    startCol = j;
                }

            }

        }

        if (startRow == null) {
            throw new RuntimeException("Couldn't find the maze starting point. Ensure the maze contains " + START_MARKER + ".");
        }

        dfs(startRow, startCol);

        if (!foundExit) { //successful exit will print within the dfs method
            pause();
            System.out.print("\033[H\033[2J");
            System.out.println();
            display();
            System.out.println("\t...couldn't find an exit.");
        }

    }

    private void dfs(int rowNum, int colNum) {

        //stop all checks if another method call was successful, none remain or over the attempts limit.
        if (foundExit || unvisitedCount == 0) {
            return;
        }

        //out of bounds.
        if (rowNum < 0 || colNum < 0 || rowNum >= totalRows || colNum >= totalCols) {
            return;
        }

        //prev. visited, or non-visitable (represented by number 1)
        if (!visitable[rowNum][colNum]) {
            return;
        }

        visitable[rowNum][colNum] = false;
        alreadyVisited[rowNum][colNum] = true;
        --unvisitedCount;

        pause();
        System.out.print("\033[H\033[2J"); //clear console in Linux
        System.out.println("checking " + rowNum + " " + colNum + ",\t" + unvisitedCount + " remaining...");
        display();
        System.out.println("->");

        if (maze[rowNum][colNum] == END_MARKER) {
            pause();
            System.out.print("\033[H\033[2J");
            System.out.println();
            display();
            System.out.println("\tfound an exit at " + rowNum + " " + colNum + "!");
            foundExit = true;
            return;
        }

        dfs(rowNum, colNum + 1); //right
        dfs(rowNum - 1, colNum); //up
        dfs(rowNum, colNum - 1); //left
        dfs(rowNum + 1, colNum); //down

    }

    private String getMazeStringIcon(int number) {

        switch (number) {
            case WALL_MARKER:
                return "#";
            case START_MARKER:
                return ">";
            case END_MARKER:
                return "!";
            default:
                return ".";
        }

    }

    public void display() {

        //header line of column numbers
        System.out.print("  ");
        for (int i = 0; i < maze[0].length; i++) {
            System.out.print("_" + i + "");
        }
        System.out.println();

        //each row
        for (int i = 0; i < maze.length; i++) {
            System.out.print(i + "| "); //row line number at side

            //maze elements in each column
            for (int j = 0; j < maze[i].length; j++) {

                String mazeIcon;

                if (alreadyVisited[i][j]) {
                    mazeIcon = " ";
                } else {
                    mazeIcon = getMazeStringIcon( maze[i][j] );
                }

                System.out.print( mazeIcon + " ");

            }

            System.out.println();

        }
    }

    private void pause() {
        try {
            Thread.sleep(PRINT_DELAY);
        }
        catch (InterruptedException in) {
            in.printStackTrace();
        }
    }

}

/*
    Builder class to read the maze from a file, parse the Strings as an int array and instantiate the maze solver object.
 */
class MazeBuilder {

    SolveMaze sm;
    int[][] mazeInt;

    MazeBuilder(String filepath) {
        ArrayList<String> mazeStrArray = FileRead.read(filepath);
        mazeInt = parseStringMaze(mazeStrArray);
    }

    public SolveMaze build() {
        sm = new SolveMaze(mazeInt);
        return sm;
    }

    private int[][] parseStringMaze(ArrayList<String> mazeStrArray) {

        int numRows = mazeStrArray.size();
        int numCols = mazeStrArray.get(0).length() / 2 + 1;

        int[][] mazeIntArray = new int[numRows][numCols];

        int rowNum = 0;

        // do sanity check (require non-jagged 2d array) & add items to int array
        for (String s : mazeStrArray) {

            s = s.trim();

            if ((s.length() / 2 + 1) > numCols) {
                System.out.println("Error in maze - must be symmetrical");
                System.out.println("Problem String: " + s + " (length: " + (s.length() / 2 + 1) + ", expected length: " + numCols + ")");
                throw new RuntimeException("Invalid Maze Creation");
            }

            for (int colNum = 0; colNum < numCols; colNum++) {
                //ignore space - jump 2 when filling chars from String. (only single digit numbers with spaces in maze strings)
                mazeIntArray[rowNum][colNum] = Integer.parseInt(s.charAt(colNum * 2) + "");
            }
            rowNum++;
        }

        return mazeIntArray;
    }
}
