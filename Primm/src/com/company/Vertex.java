package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vertex {

    private final String name;
    private boolean visited = false;
    private final List<Edge> neighbours;

    public Vertex(String name) {
        this.name = name;
        this.neighbours = new ArrayList<>();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void addNeighbours(Edge... edges) {
        Collections.addAll(neighbours, edges);
    }

    public List<Edge> getNeighbours() {
        return neighbours;
    }

    @Override
    public String toString() {
        return name;
    }
}
