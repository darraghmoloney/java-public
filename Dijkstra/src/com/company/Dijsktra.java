package com.company;

import java.util.HashMap;
import java.util.PriorityQueue;

public class Dijsktra {

    public static void findShortestPath(Vertex start, Vertex end) {

        start.setDistance(0);

        PriorityQueue<Vertex> visitQueue = new PriorityQueue<>();

        visitQueue.add(start);


        while (!visitQueue.isEmpty()) {

            Vertex current = visitQueue.remove();
            current.setVisited(true);
            System.out.println(current + ", neigbours: ");

            if (end != null && current == end) {
                return;
            }

            HashMap<Vertex, Double> neighbours = current.getNeighbours();

            System.out.print("\t");
            for (Vertex n : neighbours.keySet()) {

                if (n.isVisited()) {
                    continue;
                }


                double edgeDist = neighbours.get(n);

                double newDist = edgeDist + current.getDistance();

                if (newDist < n.getDistance()) {
                    n.setDistance(newDist);
                    n.setPredecessor(current);
                }

                System.out.print(n + ", ");

                if (!visitQueue.contains(n)) {
                    visitQueue.add(n);
                }

            }

            System.out.println();

        }




    }

    public static void findShortestPath(Vertex start) {
        findShortestPath(start, null);
    }





}
