package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Vertex c = new Vertex("C");
        Vertex d = new Vertex("D");
        Vertex e = new Vertex("E");
        Vertex f = new Vertex("F");
        Vertex g = new Vertex("G");

        //A Vertex that has itself as its own root is a single-member disjoint set.
        a.setRoot(a);
        b.setRoot(b);
        c.setRoot(c);
        d.setRoot(d);
        e.setRoot(e);
        f.setRoot(f);
        g.setRoot(g);


        Edge ab = new Edge(a, b, 2);
        Edge ac = new Edge(a, c, 6);
        Edge ae = new Edge(a, e, 5);
        Edge af = new Edge(a, f, 10);

        Edge ba = new Edge(b, a, 2);
        Edge bd = new Edge(b, d, 3);
        Edge be = new Edge(b, e, 3);

        Edge ca = new Edge(c, a, 6);
        Edge cd = new Edge(c, d, 1);
        Edge cf = new Edge(c, f, 2);

        Edge db = new Edge(d, b, 3);
        Edge dc = new Edge(d, c, 1);
        Edge de = new Edge(d, e, 4);
        Edge dg = new Edge(d, g, 5);

        Edge ea = new Edge(e, a, 5);
        Edge eb = new Edge(e, b, 3);
        Edge ed = new Edge(e, d, 4);

        Edge fa = new Edge(f, a, 10);
        Edge fc = new Edge(f, c, 2);
        Edge fg = new Edge(f, g, 5);

        Edge gd = new Edge(g, d, 5);
        Edge gf = new Edge(g, f, 5);


        ArrayList<Edge> edges = new ArrayList<>(
                Arrays.asList(ab, ac, ae, af, ba, bd, be, ca, cd, cf, db, dc, de, dg, ea, eb, ed, fa, fc, fg, gd, gf)
        );
        PriorityQueue<Edge> edgeQueue = new PriorityQueue<>(edges);

        ArrayList<Edge> mst = new ArrayList<>(); //minimal spanning tree
        int totalWeight = 0;

        while (!edgeQueue.isEmpty()) {

            Edge next = edgeQueue.remove();

            Vertex origin = next.getOrigin();
            Vertex destination = next.getDestination();

            if (origin.findSet() != destination.findSet()) { //join only those that are in different sets.
                origin.unionSet(destination);
                mst.add(next);
                totalWeight += next.getWeight();
            }

        }

        System.out.println("Minimal Spanning Tree found using Kruskal's algorithm: ");
        System.out.println(mst);
        System.out.println("Total weight: " + totalWeight);


    }
}
