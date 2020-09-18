package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class EagerPrimAlgo {

    public static void showMinimalSpanningTree(List<Vertex> vertices, Vertex checkVertex) {

        ArrayList<Edge> mst = new ArrayList<>();
        PriorityQueue<Edge> edgePriorityQueue = new PriorityQueue<>();
        double totalWeight = 0.0;

        while (mst.size() < vertices.size() - 1) {

            checkVertex.setVisited(true);

            for (Edge e : checkVertex.getNeighbours()) {

                Vertex destination = e.getDestination();
                if (destination.isVisited()) continue;

                double edgeWeight = e.getWeight();

                //if a lower edge weight for that Vertex is found, remove any occurrences of the Vertex from the
                //Priority Queue, and add the lower weight Edge to it. Otherwise, do nothing.
                if (edgeWeight < destination.getLowestEdgeWeight()) {

                    destination.setLowestEdgeWeight(edgeWeight);
                    edgePriorityQueue.removeIf(queueEdge -> queueEdge.getDestination() == destination || queueEdge.getOrigin() == destination);
                    edgePriorityQueue.add(e);

                }

            }

            Edge nextMin = edgePriorityQueue.remove();
            totalWeight += nextMin.getWeight();
            mst.add(nextMin);
            checkVertex = nextMin.getDestination();


        }

        System.out.println("Minimal spanning tree found: ");
        System.out.println("\t" + mst);
        System.out.println("\tTotal weight: "  + totalWeight);

    }
}
