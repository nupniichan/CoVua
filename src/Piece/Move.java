package Piece;

public class Move {
    Coordinate start;
    Coordinate end;
    int evaluation;

    public Move(Coordinate start, Coordinate end, int evaluation) {
        this.start = start;
        this.end = end;
        this.evaluation = evaluation;
    }
}