/**
 * This program uses a depth-first search algorithm to explore all possible
 * connected paths and find the longest.
 */

package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {


        Vertex v1 = new Vertex("1");
        Vertex v2 = new Vertex("2");
        Vertex v3 = new Vertex("3");
        Vertex v4 = new Vertex("4");
        Vertex v5 = new Vertex("5");
        Vertex v6 = new Vertex("6");
        Vertex v7 = new Vertex("7");
        Vertex v8 = new Vertex("8");

        v1.addConnections(v3, v4);

        v3.addConnections(v4, v6);

        v5.addConnections(v1, v7, v8);

        v6.addConnections(v8);

        List<Vertex> vList = new ArrayList<>(Arrays.asList(v1, v2, v3,v4, v5, v6, v7, v8));


        for (Vertex v : vList) {
            DFSTracked.dfs(v, "");
        }

        System.out.println("------------------------------------------");

        ArrayList<String> longest = new ArrayList<>(); //Store best single path, or best multiple paths of equal length
        longest.add("");

        for (String path : DFSTracked.getPathsVisited()) {
            int pLength = path.length() / 2 + 1; //add back 1 because last item has no comma

            System.out.println(path + "\t\t\t L:" + pLength );



            if (path.length() > longest.get(0).length()) { //Replace all if current is longer
                longest.clear();
                longest.add(path);
            } else if (path.length() == longest.get(0).length()) { //Add current to list if equal to other max length Strings
                longest.add(path);
            }

        }

        int longestLength = longest.get(0).length() / 2 + 1;


        System.out.println("Longest path(s) found (length " + longestLength + "): " + longest);


    }
}
