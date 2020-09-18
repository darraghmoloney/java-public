package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        double[][] adjacencyMatrix = {
                /*       a  b  c  d   e  f  g   */
                /* a */ {0, 1, 2, 12, 0, 0, 0},
                /* b */ {1, 0, 0, 4, 7, 0, 8},
                /* c */ {2, 0, 0, 6, 0, 3, 0},
                /* d */ {12, 4, 6, 0, 2, 5, 0},
                /* e */ {0, 7, 0, 2, 0, 4, 9},
                /* f */ {0, 0, 3, 5, 4, 0, 2},
                /* g */ {0, 8, 0, 0, 9, 2, 0}
        };

        ArrayList<Vertex> vertices = new ArrayList<>();

        //creating vertices
        for (int i = 0; i < adjacencyMatrix.length; ++i) {
            Vertex v = new Vertex(((char) (i + 'A') + ""));
            vertices.add(v);
        }

        //adding neighbours for each vertex
        for (int i = 0; i < vertices.size(); ++i) {

            Vertex origin = vertices.get(i);

            for (int j = 0; j < adjacencyMatrix[i].length; ++j) {

                if (i == j || adjacencyMatrix[i][j] == 0) continue;

                Vertex destination = vertices.get(j);
                double edgeWeight = adjacencyMatrix[i][j];

                Edge neighbourEdge = new Edge(origin, destination, edgeWeight);
                origin.addNeighbours(neighbourEdge);
            }

        }

        EagerPrimAlgo.showMinimalSpanningTree(vertices, vertices.get(4));


    }
}
