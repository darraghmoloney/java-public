package com.company;

class PlaceMarker {

    private final int row;
    private final int col;

    public PlaceMarker(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlaceMarker)) return false;

        PlaceMarker other = (PlaceMarker) obj;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
