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
                (board[newRow][newCol] == null || board[newRow][newCol].getColor() != color)) {

                Piece originalPiece = board[newRow][newCol];

                board[newRow][newCol] = this;
                board[coordinate.row][coordinate.col] = null;

                if (!isKingInCheck(color, board)) {
                    possibleMoves.add(new Coordinate(newRow, newCol));
                }

                board[coordinate.row][coordinate.col] = this;
                board[newRow][newCol] = originalPiece;
            }
        }

        return possibleMoves;
    }

    private boolean isKingInCheck(int kingColor, Piece[][] board) {
        Coordinate kingCoordinate = this.coordinate;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() != kingColor) {
                    List<Coordinate> possibleMoves = piece.getPossibleMove(board);
                    if (possibleMoves.contains(kingCoordinate)) {
                        return true; 
                    }
                }
            }
        }
        return false; 
    }

    @Override
    public String getType() {
        return "King";
    }

    @Override
    public String getColorString() {
        if (color == WHITE) {
            return "WHITE";
        } else if (color == BLACK) {
            return "BLACK";
        } else {
            return "Unknown";
        }
    }
    public List<Coordinate> getValidCastlingMoves(Piece[][] board) {
        List<Coordinate> validCastlingMoves = new ArrayList<>();

        if (hasMoved || isKingInCheck(color, board)) {
            return validCastlingMoves; 
        }

        if (isValidCastlingMove(board, 7)) {
            validCastlingMoves.add(new Coordinate(coordinate.row, coordinate.col + 2));
        }
        
        if (isValidCastlingMove(board, 0)) {
            validCastlingMoves.add(new Coordinate(coordinate.row, coordinate.col - 2));
        }

        return validCastlingMoves;
    }
    private boolean isValidCastlingMove(Piece[][] board, int rookCol) {
        int row = coordinate.row;
        int direction = (rookCol == 7) ? 1 : -1; 

        // Kiểm tra chướng ngại vật
        for (int col = coordinate.col + direction; col != rookCol; col += direction) {
            if (board[row][col] != null) {
                return false;
            }
        }

        // Kiểm tra Xe
        if (!(board[row][rookCol] instanceof Rook) || board[row][rookCol].hasMoved) {
            return false;
        }

        // Kiểm tra ô bị chiếu
        for (int col = coordinate.col + direction; col != rookCol; col += direction) { // Dừng ở ô trước Xe
            board[row][col] = this;
            if (isKingInCheck(color, board)) {
                board[row][col] = null;
                return false;
            }
            board[row][col] = null;
        }

        return true;
    }

    @Override
    public int getValue() {
        return KING_POINT; 
    }
    
}