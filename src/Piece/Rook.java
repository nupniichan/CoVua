package Piece;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(int color, Coordinate coordinate) {
        super(color, coordinate);
        this.point = ROOK_POINT;
        this.name = "Rook";
    }

    @Override
    public List<Coordinate> getPossibleMove(Piece[][] board) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // 4 hướng của Xe

        for (int[] dir : directions) {
            int row = coordinate.row + dir[0];
            int col = coordinate.col + dir[1];

            while (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
                if (checkEmptyCell(board, row, col)) {
                    possibleMoves.add(new Coordinate(row, col));
                } else {
                    if (board[row][col].getColor() != color) {
                        possibleMoves.add(new Coordinate(row, col)); // Ăn quân
                    }
                    break; // Dừng khi gặp quân cản (cùng màu hoặc khác màu)
                }
                row += dir[0]; // Tiếp tục đi theo hướng dir
                col += dir[1];
            }
        }

        return possibleMoves;
    }

    @Override
    public String getType() {
        return "Rook";
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