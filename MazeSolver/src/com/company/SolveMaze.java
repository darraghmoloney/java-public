package com.company;

import java.util.ArrayList;

public class SolveMaze {

    private final int[][] maze;
    private final boolean[][] visitable;
    private final boolean[][] alreadyVisited;
    private final int totalRows;
    private final int totalCols;
    private boolean foundExit;
    private int numberOfTries; //breakout if stuck

    private static final int wallMarker = 1;
    private static final int startMarker = 2;
    private static final int endMarker = 3;

    private static final int printDelay = 700; //milliseconds between maze re-print so changes can be seen

    public SolveMaze(int[][] maze) {
        this.maze = maze;
        this.totalRows = maze.length;
        this.totalCols = maze[0].length;
        this.visitable = new boolean[maze.length][maze[0].length];
        this.alreadyVisited = new boolean[maze.length][maze[0].length];
    }

    public void solve() {

        int startRow = 0;
        int startCol = 0;

        for (int i = 0; i < maze.length; ++i) {
            for (int j = 0; j < maze[i].length; ++j) {
                if (maze[i][j] != wallMarker) {
                    visitable[i][j] = true;
                }

                if (maze[i][j] == startMarker) {
                    startRow = i;
                    startCol = j;
                }

            }

        }

        //sanity check - break out if search attempts reach number of cells. should break out anyway, but...
        while (!foundExit && numberOfTries < totalCols * totalRows) {
            dfs(startRow, startCol);
        }

        if (!foundExit) {
            System.out.println("\tcouldn't find the exit.");
        }

    }

    private void dfs(int rowNum, int colNum) {

        numberOfTries++;

        //stop all checks if another method call was successful.
        if (foundExit) {
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

        pause();

        System.out.print("\033[H\033[2J"); //clear console in Linux


        System.out.println("checking " + rowNum + " " + colNum + ",\t");

        display();
        System.out.println("->");

        visitable[rowNum][colNum] = false;
        alreadyVisited[rowNum][colNum] = true;

        if (maze[rowNum][colNum] == endMarker) {
            pause();

            System.out.print("\033[H\033[2J");

            display();
            System.out.println("\tfound the exit at " + rowNum + " " + colNum + "!");
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
            case wallMarker:
                return "#";
            case startMarker:
                return ">";
            case endMarker:
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
            Thread.sleep(printDelay);
        }
        catch (InterruptedException in) {
            in.printStackTrace();
        }
    }


}

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
