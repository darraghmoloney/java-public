package com.company;

public class Vertex {

    private final String name;
    private Vertex root; //The representative value of the disjoint set this Vertex belongs to.

    public Vertex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Vertex getRoot() {
        return root;
    }

    public void setRoot(Vertex root) {
        this.root = root;
    }

    /**
     * Recursively find the parent and compress the path
     * when the parent is found by setting all Vertices
     * in the chain to point directly to that parent.
     *
     * @return The root Vertex that represents that set.
     */
    public Vertex findSet() {

        if (this.getRoot() == this) return this;

        Vertex root = getRoot().findSet();  //move up the chain
        setRoot(root);                      //path compress

        return root;

    }

    /**
     * Join two Vertices in the same set by giving them the same
     * parent. As the findSet() call compresses the path ensuring
     * constant-time O1 access, it doesn't matter which is set as the root.
     *
     * @param other The Vertex member of another set to be joined by
     *              this Vertex.
     */
    public void unionSet(Vertex other) {
        Vertex aRoot = this.findSet();
        Vertex bRoot = other.findSet();

        aRoot.setRoot(bRoot); //both compressed through findSet() so should only have 1-level height difference

    }

    @Override
    public String toString() {
        return name;
    }
}
