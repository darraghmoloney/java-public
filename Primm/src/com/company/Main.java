package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");
        Vertex c = new Vertex("c");
        Vertex d = new Vertex("d");
        Vertex e = new Vertex("e");
        Vertex f = new Vertex("f");
        Vertex g = new Vertex("g");

        a.addNeighbours(
                new Edge(a, b, 2),
                new Edge(a, c, 6),
                new Edge(a, e, 5),
                new Edge(a, f, 10)
        );

        b.addNeighbours(
                new Edge(b, a, 2),
                new Edge(b, d, 3),
                new Edge(b, e, 3)
        );

        c.addNeighbours(
                new Edge(c, a, 6),
                new Edge(c, d, 1),
                new Edge(c, f, 2)
        );

        d.addNeighbours(
                new Edge(d, b, 3),
                new Edge(d, c, 1),
                new Edge(d, e, 4),
                new Edge(d, g, 5)
        );

        e.addNeighbours(
                new Edge(e, a, 5),
                new Edge(e, b, 3),
                new Edge(e, d, 4)
        );

        f.addNeighbours(
                new Edge(f, a, 10),
                new Edge(f, c, 2),
                new Edge(f, g, 3)
        );

        g.addNeighbours(
                new Edge(g, f, 3),
                new Edge(g, d, 5)
        );

        int numVertices = 7; //better to put in Collection & get size()

        PriorityQueue<Edge> edgePriorityQueue = new PriorityQueue<>();
        List<Edge> mst = new ArrayList<>();

        for (Edge n : d.getNeighbours()) {
            edgePriorityQueue.offer(n);
        }

        int totalWeight = 0;

        while (!edgePriorityQueue.isEmpty()) {

            if (mst.size() == numVertices - 1) break; //if implemented right, the complete tree will have n-1 edges

            Edge next = edgePriorityQueue.poll();
            System.out.print("Checking " + next + "...");

            //extra check if unknown number of vertices, etc.
            if (next.getOrigin().isVisited() && next.getDestination().isVisited()) {
                System.out.println("\t-> already visited both vertices, skipping.");
                continue;
            }
            System.out.println();

            next.getOrigin().setVisited(true);
            next.getDestination().setVisited(true);

            totalWeight += next.getWeight();

            mst.add(next);

            for (Edge n : next.getDestination().getNeighbours()) {
                if (!n.getDestination().isVisited() && !edgePriorityQueue.contains(n)) {
                    edgePriorityQueue.add(n);
                }
            }

        }

        System.out.println();
        System.out.println("Minimal spanning tree using Primm-Jarnik algorithm: ");
        System.out.println("\t" + mst);
        System.out.println("Total cost: " + totalWeight);

    }
}
