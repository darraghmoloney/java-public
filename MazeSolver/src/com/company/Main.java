package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<String> mazeStrArray = FileRead.read("src/com/company/mazeMap.txt");

        int[][] maze = SolveMaze.parseStringMaze(mazeStrArray);

        SolveMaze sm = new SolveMaze(maze);

        sm.display();

        sm.solve();

    }
}
