package Piece;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(int color, Coordinate coordinate) {
        super(color, coordinate);
        this.point = PAWN_POINT;
        this.name = "Pawn";
    }

    @Override
    public List<Coordinate> getPossibleMove(Piece[][] board) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int direction = (color == Piece.WHITE) ? -1 : 1;
        int startRow = (color == Piece.WHITE) ? 6 : 1;

        // Move forward one square
        if (coordinate.row + direction >= 0 && coordinate.row + direction <= 7) {
            if (checkEmptyCell(board, coordinate.row + direction, coordinate.col)) {
                possibleMoves.add(new Coordinate(coordinate.row + direction, coordinate.col));
            }
        }

        // Move forward two squares from starting position
        if (coordinate.row == startRow) {
            if (checkEmptyCell(board, coordinate.row + direction, coordinate.col) &&
                checkEmptyCell(board, coordinate.row + 2 * direction, coordinate.col)) {
                possibleMoves.add(new Coordinate(coordinate.row + 2 * direction, coordinate.col));
            }
        }

        // Capture diagonally to the left
        if (coordinate.col - 1 >= 0 && coordinate.row + direction >= 0 && coordinate.row + direction <= 7) {
            if (board[coordinate.row + direction][coordinate.col - 1] != null &&
                board[coordinate.row + direction][coordinate.col - 1].getColor() != color) {
                possibleMoves.add(new Coordinate(coordinate.row + direction, coordinate.col - 1));
            }
        }

        // Capture diagonally to the right
        if (coordinate.col + 1 <= 7 && coordinate.row + direction >= 0 && coordinate.row + direction <= 7) {
            if (board[coordinate.row + direction][coordinate.col + 1] != null &&
                board[coordinate.row + direction][coordinate.col + 1].getColor() != color) {
                possibleMoves.add(new Coordinate(coordinate.row + direction, coordinate.col + 1));
            }
        }

        return possibleMoves;
    }

    @Override
    public String getType() {
        return "Pawn";
    }

    @Override
    public String getColorString() {
        return (color == WHITE) ? "White" : "Black";
    }
}