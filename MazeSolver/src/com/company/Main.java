package com.company;


public class Main {

    public static void main(String[] args) {

        SolveMaze sm = new MazeBuilder("/home/darragh/IdeaProjects/MazeSolver/src/com/company/mazeMap.txt").build();

        sm.solve();

    }
}
