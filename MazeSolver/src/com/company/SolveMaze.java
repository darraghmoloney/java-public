package com.company;

public class SolveMaze {

    private final int[][] maze;
    private final boolean[][] visitable;
    private final int totalRows;
    private final int totalCols;
    private boolean foundExit;
    private int numberOfTries; //breakout if stuck
    private static int printedCount; //break print lines nicely in search output

    private static final int wallMarker = 1;
    private static final int startMarker = 2;
    private static final int endMarker = 3;

    public SolveMaze(int[][] maze) {
        this.maze = maze;
        this.totalRows = maze.length;
        this.totalCols = maze[0].length;
        this.visitable = new boolean[maze.length][maze[0].length];
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

        System.out.print("checking " + rowNum + " " + colNum + ",\t");
        printedCount++;

        if (printedCount % 5 == 0) {
            System.out.println();
        }

        visitable[rowNum][colNum] = false;

        if (maze[rowNum][colNum] == endMarker) {
            System.out.println();
            System.out.println("\tfound the exit at " + rowNum + " " + colNum + "!");
            foundExit = true;
            return;
        }


        dfs(rowNum, colNum + 1); //right
        dfs(rowNum - 1, colNum); //up
        dfs(rowNum, colNum - 1); //left
        dfs(rowNum + 1, colNum); //down

    }

    public static String getMazeStringIcon(int number) {

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

}
