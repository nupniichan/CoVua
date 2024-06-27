package Piece;

import javax.swing.*;
import java.util.List;

public abstract class Piece {

    public static final int QUEEN_POINT = 9;
    public static final int ROOK_POINT = 5;
    public static final int BISHOP_POINT = 3;
    public static final int KNIGHT_POINT = 3;
    public static final int PAWN_POINT = 1;
    public static final int EMPTY_POINT = 0;
    public static final int KING_POINT = 1000;

    public static final int WHITE = 1;
    public static final int BLACK = 2; 

    protected int color;
    protected int point;
    protected Coordinate coordinate;
    protected String name;
    protected ImageIcon image;

    public Piece(int color, Coordinate coordinate) {
        this.color = color;
        this.coordinate = coordinate;
        loadImage();
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate newCoordinate) {
        this.coordinate = newCoordinate;
    }    

    public String getName() {
        return name;
    }

    public ImageIcon getImage() {
        return image;
    }

    public abstract int getValue();
    
    public boolean checkEmptyCell(Piece[][] board, int iRow, int iCol) {
        if (iRow < 0 || iRow > 7 || iCol < 0 || iCol > 7) {
            return false;
        }
        return board[iRow][iCol] == null;
    }

    public abstract List<Coordinate> getPossibleMove(Piece[][] board);

    private void loadImage() {
        String imagePath = "src\\main\\resources\\images\\" +
                (color == WHITE ? "White" : "Black") + "_" +
                getClass().getSimpleName() + ".png";
        this.image = new ImageIcon(imagePath);
    }
    public abstract String getType();
    public abstract String getColorString();
}