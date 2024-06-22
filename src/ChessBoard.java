import Piece.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoard extends JPanel implements MouseListener, MouseMotionListener {

    private final Map<Coordinate, Piece> boardMap = new HashMap<>();
    private Piece selectedPiece;
    private Coordinate selectedPiecePosition;
    private Map<Coordinate, Boolean> validMoves;
    private boolean isWhiteTurn = true;
    private JButton endGameButton;
    private JLabel messageLabel; 
    private Piece[][] board = new Piece[8][8];

    public ChessBoard() {
        setPreferredSize(new Dimension(800, 800));
        initializeBoard();
        addMouseListener(this);
        addMouseMotionListener(this);

        endGameButton = new JButton("End Game");
        endGameButton.addActionListener(e -> showGameEndScreen(true));
        add(endGameButton);

        // Khởi tạo JLabel
        messageLabel = new JLabel("");
        messageLabel.setBounds(10, 750, 200, 20);
        add(messageLabel);
    }

    private void initializeBoard() {
        boardMap.put(new Coordinate(7, 0), new Rook(Piece.WHITE, new Coordinate(7, 0)));
        boardMap.put(new Coordinate(7, 1), new Knight(Piece.WHITE, new Coordinate(7, 1)));
        boardMap.put(new Coordinate(7, 2), new Bishop(Piece.WHITE, new Coordinate(7, 2)));
        boardMap.put(new Coordinate(7, 3), new Queen(Piece.WHITE, new Coordinate(7, 3)));
        boardMap.put(new Coordinate(7, 4), new King(Piece.WHITE, new Coordinate(7, 4)));
        boardMap.put(new Coordinate(7, 5), new Bishop(Piece.WHITE, new Coordinate(7, 5)));
        boardMap.put(new Coordinate(7, 6), new Knight(Piece.WHITE, new Coordinate(7, 6)));
        boardMap.put(new Coordinate(7, 7), new Rook(Piece.WHITE, new Coordinate(7, 7)));
        for (int i = 0; i < 8; i++) {
            boardMap.put(new Coordinate(6, i), new Pawn(Piece.WHITE, new Coordinate(6, i)));
        }
    
        boardMap.put(new Coordinate(0, 0), new Rook(Piece.BLACK, new Coordinate(0, 0)));
        boardMap.put(new Coordinate(0, 1), new Knight(Piece.BLACK, new Coordinate(0, 1)));
        boardMap.put(new Coordinate(0, 2), new Bishop(Piece.BLACK, new Coordinate(0, 2)));
        boardMap.put(new Coordinate(0, 3), new Queen(Piece.BLACK, new Coordinate(0, 3)));
        boardMap.put(new Coordinate(0, 4), new King(Piece.BLACK, new Coordinate(0, 4)));
        boardMap.put(new Coordinate(0, 5), new Bishop(Piece.BLACK, new Coordinate(0, 5)));
        boardMap.put(new Coordinate(0, 6), new Knight(Piece.BLACK, new Coordinate(0, 6)));
        boardMap.put(new Coordinate(0, 7), new Rook(Piece.BLACK, new Coordinate(0, 7)));
        for (int i = 0; i < 8; i++) {
            boardMap.put(new Coordinate(1, i), new Pawn(Piece.BLACK, new Coordinate(1, i)));
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Coordinate coord = new Coordinate(i, j);
                Piece piece = boardMap.get(coord);
                if (piece != null) {
                    board[i][j] = piece;
                } else {
                    board[i][j] = null;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
        drawValidMoves(g);

        messageLabel.setBounds(10, 750, 200, 20);
        endGameButton.setBounds(20, 20, 100, 30);
    }

    private void drawBoard(Graphics g) {
        boolean isWhite = true;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                g.setColor(isWhite ? Color.WHITE : Color.GRAY);
                g.fillRect(col * 100, row * 100, 100, 100);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    private void drawPieces(Graphics g) {
        for (Map.Entry<Coordinate, Piece> entry : boardMap.entrySet()) {
            Coordinate coordinate = entry.getKey();
            Piece piece = entry.getValue();
            ImageIcon image = piece.getImage();
            if (image != null) {
                int x = coordinate.col * 100 + (100 - image.getIconWidth()) / 2;
                int y = coordinate.row * 100 + (100 - image.getIconHeight()) / 2;
                image.paintIcon(this, g, x, y);
            }
        }
    }

    private void drawValidMoves(Graphics g) {
        if (validMoves != null) {
            g.setColor(new Color(0, 255, 0, 100)); // Màu xanh lá cây
            for (Coordinate move : validMoves.keySet()) {
                int x = move.col * 100;
                int y = move.row * 100;
                System.out.println("drawValidMoves() - Drawing move: " + move + ", x: " + x + ", y: " + y); // In ra tọa độ vẽ
                g.fillRect(x, y, 100, 100); 
            }
        }

        // Vẽ ô quân cờ đang được chọn
        if (selectedPiecePosition != null) {
            g.setColor(new Color(0, 0, 255, 100)); // Màu xanh dương
            int x = selectedPiecePosition.col * 100;
            int y = selectedPiecePosition.row * 100;
            g.fillRect(x, y, 100, 100); 
        }
    }

    private void handleMouseClick(Coordinate clickCoordinate) {
        Piece clickedPiece = boardMap.get(clickCoordinate);

        if (clickedPiece == null) {
            return; 
        }

        if (selectedPiece == null && clickedPiece.getColor() == (isWhiteTurn ? Piece.WHITE : Piece.BLACK)) {
            selectedPiece = clickedPiece;
            selectedPiecePosition = clickCoordinate;
            validMoves = new HashMap<>();

            messageLabel.setText("Đã bấm vào quân cờ " + clickedPiece.getType() + " " + clickedPiece.getColorString());

            List<Coordinate> possibleMoves = selectedPiece.getPossibleMove(board);
            for (Coordinate move : possibleMoves) {
                validMoves.put(move, true);
            }
            repaint();
        } else if (selectedPiece != null && validMoves != null && validMoves.containsKey(clickCoordinate)) {
            boardMap.put(clickCoordinate, selectedPiece);
            boardMap.remove(selectedPiecePosition);

            selectedPiece.setCoordinate(clickCoordinate);
            // Cập nhật mảng board:
            board[clickCoordinate.row][clickCoordinate.col] = selectedPiece; 
            board[selectedPiecePosition.row][selectedPiecePosition.col] = null;

            selectedPiece = null;
            validMoves = null;
            selectedPiecePosition = null;
            isWhiteTurn = !isWhiteTurn;
            repaint();

            if (isGameOver()) {
                boolean isWhiteWin = determineWinner();
                showGameEndScreen(isWhiteWin);
            }
        } else {
            selectedPiece = null;
            validMoves = null;
            selectedPiecePosition = null;
            repaint();
        }
    }

    private boolean isGameOver() {
        return false;
    }

    private boolean determineWinner() {
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / 100;
        int y = e.getY() / 100;
        System.out.println("mousePressed() - x: " + x + ", y: " + y);
        selectedPiecePosition = new Coordinate(y, x);
        System.out.println("mousePressed() - selectedPiecePosition: " + selectedPiecePosition);
        handleMouseClick(selectedPiecePosition);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void showGameEndScreen(boolean isWhiteWin) {
        new GameEndScreen(isWhiteWin);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess Game");
        ChessBoard chessBoard = new ChessBoard();
        frame.add(chessBoard);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
} 