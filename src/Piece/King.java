package Piece;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(int color, Coordinate coordinate) {
        super(color, coordinate);
        this.point = KING_POINT;
        this.name = "King";
    }

    @Override
    public List<Coordinate> getPossibleMove(Piece[][] board) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newRow = coordinate.row + dir[0];
            int newCol = coordinate.col + dir[1];

            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7 &&
                (checkEmptyCell(board, newRow, newCol) || 
                 board[newRow][newCol].getColor() != color)) {
                possibleMoves.add(new Coordinate(newRow, newCol));
            }
        }

        return possibleMoves;
    }

    @Override
    public String getType() {
        return "King";
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
}