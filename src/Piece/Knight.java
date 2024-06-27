package Piece;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(int color, Coordinate coordinate) {
        super(color, coordinate);
        this.point = KNIGHT_POINT;
        this.name = "Knight";
    }

    @Override
    public List<Coordinate> getPossibleMove(Piece[][] board) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int[] rowOffsets = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] colOffsets = {1, 2, 2, 1, -1, -2, -2, -1};

        for (int i = 0; i < 8; i++) {
            int newRow = coordinate.row + rowOffsets[i];
            int newCol = coordinate.col + colOffsets[i];

            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7 &&
                    (checkEmptyCell(board, newRow, newCol) || board[newRow][newCol].getColor() != color)) {
                possibleMoves.add(new Coordinate(newRow, newCol));
            }
        }

        return possibleMoves;
    }

    @Override
    public String getType() {
        return "Knight";
    }
    @Override
    public String getColorString() {
        if (color == WHITE) {
            return "White";
        } else if (color == BLACK) {
            return "Black";
        } else {
            return "Unknown";
        }
    }
    @Override
    public int getValue() {
        return KNIGHT_POINT; 
    }
}