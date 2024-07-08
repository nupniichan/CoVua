package Piece;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(int color, Coordinate coordinate) {
        super(color, coordinate);
        this.point = BISHOP_POINT;
        this.name = "Bishop";
    }

    @Override
    public List<Coordinate> getPossibleMove(Piece[][] board) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            int row = coordinate.row + dir[0];
            int col = coordinate.col + dir[1];

            while (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
                if (checkEmptyCell(board, row, col)) {
                    possibleMoves.add(new Coordinate(row, col));
                } else {
                    if (board[row][col].getColor() != color) {
                        possibleMoves.add(new Coordinate(row, col));
                    }
                    break; 
                }
                row += dir[0]; 
                col += dir[1];
            }
        }

        return possibleMoves;
    }

    @Override
    public String getType() {
        return "Bishop";
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
        return BISHOP_POINT; 
    }
}