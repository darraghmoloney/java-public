package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        SolveMaze sm = new MazeBuilder("src/com/company/mazeMap.txt").build();

        sm.display();

        sm.solve();

    }
}
