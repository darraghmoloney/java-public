package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vertex {

    private final String name;
    private final List<Edge> neighbours;
    private boolean visited;
    private double lowestEdgeWeight = Double.MAX_VALUE;

    public Vertex(String name) {
        this.name = name;
        neighbours = new ArrayList<>();
    }

    public List<Edge> getNeighbours() {
        return neighbours;
    }

    public double getLowestEdgeWeight() {
        return lowestEdgeWeight;
    }

    public void addNeighbours(Edge... neighbours) {
        this.neighbours.addAll(Arrays.asList(neighbours));
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setLowestEdgeWeight(double lowestEdgeWeight) {
        this.lowestEdgeWeight = lowestEdgeWeight;
    }

    @Override
    public String toString() {
        return name;
    }

}
