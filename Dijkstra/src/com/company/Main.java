package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        // write your code here

        Vertex[] vertices = new Vertex[9];

        for (int i = 0; i < vertices.length; ++i) {
            vertices[i] = new Vertex(i + "");
        }

        //Adjacency list would be simpler...
        vertices[0].addNeighbour(vertices[1], 4);
        vertices[0].addNeighbour(vertices[7], 8);

        vertices[1].addNeighbour(vertices[0], 4);
        vertices[1].addNeighbour(vertices[2], 8);
        vertices[1].addNeighbour(vertices[7], 11);

        vertices[2].addNeighbour(vertices[1], 8);
        vertices[2].addNeighbour(vertices[3], 7);
        vertices[2].addNeighbour(vertices[5], 4);
        vertices[2].addNeighbour(vertices[8], 2);

        vertices[3].addNeighbour(vertices[2], 7);
        vertices[3].addNeighbour(vertices[4], 9);
        vertices[3].addNeighbour(vertices[5], 14);

        vertices[4].addNeighbour(vertices[3], 9);
        vertices[4].addNeighbour(vertices[5], 10);

        vertices[5].addNeighbour(vertices[2], 4);
        vertices[5].addNeighbour(vertices[3], 14);
        vertices[5].addNeighbour(vertices[4], 10);
        vertices[5].addNeighbour(vertices[6], 2);

        vertices[6].addNeighbour(vertices[5], 2);
        vertices[6].addNeighbour(vertices[7], 1);
        vertices[6].addNeighbour(vertices[8], 6);

        vertices[7].addNeighbour(vertices[0], 8);
        vertices[7].addNeighbour(vertices[1], 11);
        vertices[7].addNeighbour(vertices[6], 1);
        vertices[7].addNeighbour(vertices[8], 7);

        vertices[8].addNeighbour(vertices[2], 2);
        vertices[8].addNeighbour(vertices[6], 6);
        vertices[8].addNeighbour(vertices[7], 7);


        ////////////////////////////////////////////////////////////////////

        Vertex checkVertex = vertices[0];

        Dijsktra.findShortestPath(checkVertex);


        //use Strings to find all the potential paths from the source
        //(minimal spanning tree-ish?)
        ArrayList<String> allPaths = new ArrayList<>();

        Vertex_Loop:
        for (Vertex v : vertices) {

            String nextChain = v.printChain();

            //check if current path exists already as a (sub)string of others
            for (String path : allPaths) {

                if (path.length() < nextChain.length()) {
                    continue;
                }

                path = path.substring(0, nextChain.length());

                if (nextChain.equals(path)) {
                    continue Vertex_Loop;
                }

            }

            allPaths.add(nextChain);

            //check if new path's sub-paths already exist in the list
            //and remove them if so
            String chainWithoutV = nextChain.substring(0, nextChain.length() - 2);

            while (chainWithoutV.length() > 0) {

                allPaths.remove(chainWithoutV); //remove includes "contains" check

                chainWithoutV = chainWithoutV.substring(0, chainWithoutV.length() - 2);

            }


        }


        for (String pathString : allPaths) {
            System.out.println(pathString);
        }

        System.out.println();

        Arrays.sort(vertices);


        for (Vertex v : vertices) {

            if (v.getDistance() != Double.POSITIVE_INFINITY) {
                System.out.println(v + " predecessor: " + v.getPredecessor());
            }

        }


    }

}
