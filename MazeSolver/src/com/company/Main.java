package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<String> mazeStrArray;
        mazeStrArray = FileRead.read("src/com/company/mazeMap.txt");

        int numRows = mazeStrArray.size();
        int numCols = mazeStrArray.get(0).length() / 2 + 1;

        int[][] maze = new int[numRows][numCols];

        int rowNum = 0;

        //sanity check (require symmetrical 2d array) & adding items to int array for easier manipulation
        for (String s : mazeStrArray) {

            s = s.trim();

            if ((s.length() / 2 + 1) > numCols) {
                System.out.println("Error in maze - must be symmetrical");
                System.out.println("Problem String: " + s + " (length: " + (s.length() / 2 + 1) + ", expected length: " + numCols + ")");
                return;
            }

            for (int colNum = 0; colNum < numCols; colNum++) {
                //ignore space - jump 2 when filling chars from String. (only single digit numbers with spaces in maze strings)
                maze[rowNum][colNum] = Integer.parseInt(s.charAt(colNum * 2) + "");
            }
            rowNum++;
        }

        System.out.print("  ");
        for (int i = 0; i < maze[0].length; i++) {
            System.out.print("_" + i + "");
        }
        System.out.println();

        for (int i = 0; i < maze.length; i++) {
            System.out.print(i + "| ");

            for (int j = 0; j < maze[i].length; j++) {
                String mazeIcon = SolveMaze.getMazeStringIcon( maze[i][j] );
                System.out.print( mazeIcon + " ");

            }

            System.out.println();

        }

        SolveMaze sm = new SolveMaze(maze);

        sm.solve();

    }
}
