package com.company;

import java.util.ArrayList;
import java.util.List;

public class DFSTracked {

    private static final List<String> pathsVisited = new ArrayList<>(); //Could also be a List<Vertex>

    public static List<String> getPathsVisited() {
        return pathsVisited;
    }

    public static void dfs(Vertex v, String visitedPath) {

        System.out.println("checking " + v + ", currentPath: [" + visitedPath + "]");

        if (visitedPath.contains(v.toString())) { //No repeated visits in this implementation
            return;
        }

        visitedPath += v + ",";


        if (v.getConnections().size() == 0) { //Leaf node
            visitedPath = visitedPath.substring(0, visitedPath.length()-1); //remove last comma
            System.out.println(visitedPath + "\t no more connections");
            pathsVisited.add(visitedPath);
            return;
        }

        for (Vertex c : v.getConnections()) {

            System.out.println("\t calling check for " + c + " from " + v);
            dfs(c, visitedPath);

        }


    }


}
