package Piece;

import java.util.Objects;

public class Coordinate {
    public int row;
    public int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate other = (Coordinate) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col); 
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")"; 
    }
}