package OnlinePlaying;

import javax.swing.*;

import Piece.Bishop;
import Piece.Coordinate;
import Piece.King;
import Piece.Knight;
import Piece.Pawn;
import Piece.Piece;
import Piece.Queen;
import Piece.Rook;
import menu.GameEndScreen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlayOnline extends JPanel implements MouseListener, MouseMotionListener {
    private Piece[][] board = new Piece[8][8];
    private Piece selectedPiece;
    private Coordinate selectedPiecePosition;
    private Map<Coordinate, Boolean> validMoves;
    private boolean isWhiteTurn = true;
    private JLabel messageLabel;
    private ChessClient client;
    private boolean myTurn;
    private String myColor; 

    public PlayOnline(ChessClient client) {
        this.client = client;

        setPreferredSize(new Dimension(800, 800));
        initializeBoard();
        addMouseListener(this);
        addMouseMotionListener(this);

        setLayout(null);

        messageLabel = new JLabel("");
        messageLabel.setBounds(10, 750, 300, 20);
        add(messageLabel);

        // Nhận thông tin màu cờ từ server
        try {
            String startMessage = client.receiveMessage();
            if (startMessage != null && startMessage.startsWith("START")) {
                String color = startMessage.split(" ")[1];
                client.receiveColor(color); // Cập nhật màu cờ cho client
                myColor = client.isWhite() ? "WHITE" : "BLACK";
                myTurn = client.isTurn; // Khởi tạo myTurn dựa trên màu cờ

                System.out.println("PlayOnline: Được khởi tạo với màu " + myColor + ", lượt: " + myTurn);

                // Thêm dòng này để bắt đầu vòng lặp nhận message từ server
                startReceivingMessages(); 
            } else {
                System.err.println("PlayOnline: Không nhận được message START từ server!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("PlayOnline: Lỗi khi nhận message START từ server!");
        }
    }

    private void startReceivingMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = client.receiveMessage();
                    System.out.println("Client " + myColor + " nhận được message: " + message);
                    if (message != null) {
                        // ... (Xử lý các message như "MOVE", "YOUR_TURN", "OPPONENT_TURN")
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("PlayOnline: Lỗi khi nhận message từ server!");
            }
        }).start();
    }
    
    private void initializeBoard() {
        // Khởi tạo bàn cờ cho người chơi trắng
        board[7][0] = new Rook(Piece.WHITE, new Coordinate(7, 0));
        board[7][1] = new Knight(Piece.WHITE, new Coordinate(7, 1));
        board[7][2] = new Bishop(Piece.WHITE, new Coordinate(7, 2));
        board[7][3] = new Queen(Piece.WHITE, new Coordinate(7, 3));
        board[7][4] = new King(Piece.WHITE, new Coordinate(7, 4));
        board[7][5] = new Bishop(Piece.WHITE, new Coordinate(7, 5));
        board[7][6] = new Knight(Piece.WHITE, new Coordinate(7, 6));
        board[7][7] = new Rook(Piece.WHITE, new Coordinate(7, 7));
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(Piece.WHITE, new Coordinate(6, i));
        }

        // Khởi tạo bàn cờ cho người chơi đen
        board[0][0] = new Rook(Piece.BLACK, new Coordinate(0, 0));
        board[0][1] = new Knight(Piece.BLACK, new Coordinate(0, 1));
        board[0][2] = new Bishop(Piece.BLACK, new Coordinate(0, 2));
        board[0][3] = new Queen(Piece.BLACK, new Coordinate(0, 3));
        board[0][4] = new King(Piece.BLACK, new Coordinate(0, 4));
        board[0][5] = new Bishop(Piece.BLACK, new Coordinate(0, 5));
        board[0][6] = new Knight(Piece.BLACK, new Coordinate(0, 6));
        board[0][7] = new Rook(Piece.BLACK, new Coordinate(0, 7));
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(Piece.BLACK, new Coordinate(1, i));
        }

        // Các ô còn lại rỗng
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }

        // Đặt lại tọa độ cho tất cả các quân cờ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    board[i][j].setCoordinate(new Coordinate(i, j));
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

        messageLabel.setBounds(10, 750, 300, 20);
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    Piece piece = board[i][j];
                    ImageIcon image = piece.getImage();
                    if (image != null) {
                        int row = i, col = j;
                        if (myColor.equals("BLACK")) {
                            row = 7 - i;
                            col = 7 - j;
                        }
                        int x = col * 100 + (100 - image.getIconWidth()) / 2;
                        int y = row * 100 + (100 - image.getIconHeight()) / 2;
                        image.paintIcon(this, g, x, y);
                    }
                }
            }
        }
    }

    private void drawValidMoves(Graphics g) {
        if (validMoves != null) {
            for (Coordinate move : validMoves.keySet()) {
                int x = move.col * 100;
                int y = move.row * 100;

                if (board[move.row][move.col] != null) {
                    g.setColor(new Color(255, 0, 0, 100));
                } else {
                    g.setColor(new Color(0, 255, 0, 100));
                }

                g.fillRect(x, y, 100, 100);
            }
        }

        if (selectedPiecePosition != null) {
            g.setColor(new Color(0, 0, 255, 100));
            int x = selectedPiecePosition.col * 100;
            int y = selectedPiecePosition.row * 100;
            g.fillRect(x, y, 100, 100);
        }
    }

    private void handleMouseClick(Coordinate clickCoordinate) {
        if (myTurn) { 
            Piece clickedPiece = board[clickCoordinate.row][clickCoordinate.col];

            if (clickedPiece == null) {
                if (selectedPiece != null && validMoves != null && validMoves.containsKey(clickCoordinate)) {
                    movePiece(clickCoordinate);
                    try {
                        String message = "MOVE " + selectedPiecePosition.row + " " +
                                selectedPiecePosition.col + " " +
                                clickCoordinate.row + " " + clickCoordinate.col;
                        client.sendMessage(message);
                        System.out.println("Client " + myColor + " đã gửi message: " + message);

                        myTurn = false; 
                        isWhiteTurn = !isWhiteTurn; 
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    clearSelection(); 
                }
            } else {
                if (clickedPiece.getColorString().equals(myColor)) { 
                    selectPiece(clickedPiece, clickCoordinate);
                } else { 
                    if (selectedPiece != null && validMoves != null && validMoves.containsKey(clickCoordinate)) {
                        movePiece(clickCoordinate);
                        try {
                            String message = "MOVE " + selectedPiecePosition.row + " " +
                                    selectedPiecePosition.col + " " +
                                    clickCoordinate.row + " " + clickCoordinate.col;
                            client.sendMessage(message);
                            myTurn = false;
                            isWhiteTurn = !isWhiteTurn;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        clearSelection(); 
                    }
                }
            }
        }
    }

    private void movePiece(Coordinate targetCoordinate) {
        // Lưu lại trạng thái bàn cờ trước khi di chuyển
        Piece[][] previousBoard = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                previousBoard[i][j] = board[i][j];
            }
        }

        // Di chuyển quân cờ
        board[targetCoordinate.row][targetCoordinate.col] = selectedPiece;
        board[selectedPiecePosition.row][selectedPiecePosition.col] = null;
        selectedPiece.setCoordinate(targetCoordinate);
        selectedPiece.hasMoved = true;

        // Kiểm tra xem vua của người chơi hiện tại có đang bị chiếu tướng không
        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;
        if (isKingInCheck(currentPlayerColor)) {
            // Nếu vua bị chiếu tướng, hãy hủy bỏ nước đi
            board = previousBoard;
            selectedPiece.setCoordinate(selectedPiecePosition);
            return;
        }

        // Xử lý nhập thành
        if (selectedPiece instanceof King && Math.abs(targetCoordinate.col - selectedPiecePosition.col) == 2) {
            handleCastling(targetCoordinate);
        }

        // Kiểm tra chiếu tướng và chiếu hết cho đối thủ
        int opponentColor = isWhiteTurn ? Piece.BLACK : Piece.WHITE;
        if (isKingInCheck(opponentColor)) {
            if (isCheckmate(opponentColor)) {
                showGameEndScreen(myColor.equals("WHITE")); 
            }
        }

        // Phong cấp tốt
        if (selectedPiece instanceof Pawn && (targetCoordinate.row == 0 || targetCoordinate.row == 7)) {
            promotePawn(targetCoordinate);
        }

        
        isWhiteTurn = !isWhiteTurn;
        try {
            client.sendMessage("TURN " + (isWhiteTurn ? "WHITE" : "BLACK"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearSelection();
        repaint();
    }

    private void promotePawn(Coordinate pawnCoordinate) {
        Piece pawnToPromote = selectedPiece;
        JPopupMenu promotionMenu = new JPopupMenu();

        promotionMenu.add(createPromotionMenuItem("Hậu", new ImageIcon("src\\main\\resources\\images\\" + (pawnToPromote.getColor() == Piece.WHITE ? "White" : "Black") + "_Queen.png"), pawnCoordinate, pawnToPromote));
        promotionMenu.add(createPromotionMenuItem("Xe", new ImageIcon("src\\main\\resources\\images\\" + (pawnToPromote.getColor() == Piece.WHITE ? "White" : "Black") + "_Rook.png"), pawnCoordinate, pawnToPromote));
        promotionMenu.add(createPromotionMenuItem("Tượng", new ImageIcon("src\\main\\resources\\images\\" + (pawnToPromote.getColor() == Piece.WHITE ? "White" : "Black") + "_Bishop.png"), pawnCoordinate, pawnToPromote));
        promotionMenu.add(createPromotionMenuItem("Mã", new ImageIcon("src\\main\\resources\\images\\" + (pawnToPromote.getColor() == Piece.WHITE ? "White" : "Black") + "_Knight.png"), pawnCoordinate, pawnToPromote));

        promotionMenu.show(this, pawnCoordinate.col * 100, pawnCoordinate.row * 100);
    }

    private JMenuItem createPromotionMenuItem(String pieceName, ImageIcon pieceImage, Coordinate pawnCoordinate, Piece pawnToPromote) {
        JMenuItem menuItem = new JMenuItem(new AbstractAction(pieceName, pieceImage) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Piece newPiece = null;
                switch (pieceName) {
                    case "Hậu":
                        newPiece = new Queen(pawnToPromote.getColor(), pawnCoordinate);
                        break;
                    case "Xe":
                        newPiece = new Rook(pawnToPromote.getColor(), pawnCoordinate);
                        break;
                    case "Tượng":
                        newPiece = new Bishop(pawnToPromote.getColor(), pawnCoordinate);
                        break;
                    case "Mã":
                        newPiece = new Knight(pawnToPromote.getColor(), pawnCoordinate);
                        break;
                }
                board[pawnCoordinate.row][pawnCoordinate.col] = newPiece;
                repaint();
            }
        });
        return menuItem;
    }

    private void selectPiece(Piece clickedPiece, Coordinate clickCoordinate) {
        selectedPiece = clickedPiece;
        selectedPiecePosition = clickCoordinate;
        validMoves = new HashMap<>();

        messageLabel.setText("Đã bấm vào quân cờ " + clickedPiece.getType() + " " + clickedPiece.getColorString());
        List<Coordinate> possibleMoves = selectedPiece.getPossibleMove(board);

        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;

        // Loại bỏ các nước đi không hợp lệ khi vua đang bị chiếu tướng
        if (isKingInCheck(currentPlayerColor)) {
            for (Iterator<Coordinate> iterator = possibleMoves.iterator(); iterator.hasNext();) {
                Coordinate move = iterator.next();
                if (!isValidMoveWhenKingInCheck(selectedPiece.getCoordinate(), move, currentPlayerColor)) {
                    iterator.remove();
                }
            }
        }

        // Thêm các nước đi nhập thành hợp lệ cho vua
        if (clickedPiece instanceof King) {
            List<Coordinate> castlingMoves = ((King) clickedPiece).getValidCastlingMoves(board);
            for (Coordinate move : castlingMoves) {
                validMoves.put(move, true);
            }
        }

        // Thêm các nước đi hợp lệ khác
        for (Coordinate move : possibleMoves) {
            validMoves.put(move, true);
        }

        repaint();
    }

    private void handleCastling(Coordinate kingTarget) {
        int rookStartCol, rookTargetCol;

        if (kingTarget.col == 2) { 
            rookStartCol = 0;
            rookTargetCol = 3;
        } else { 
            rookStartCol = 7;
            rookTargetCol = 5;
        }

        board[kingTarget.row][rookTargetCol] = board[kingTarget.row][rookStartCol];
        board[kingTarget.row][rookStartCol] = null;
        board[kingTarget.row][rookTargetCol].setCoordinate(new Coordinate(kingTarget.row, rookTargetCol));
        board[kingTarget.row][rookTargetCol].hasMoved = true;
    }

    // Kiểm tra xem nước đi có hợp lệ khi vua đang bị chiếu tướng hay không
    private boolean isValidMoveWhenKingInCheck(Coordinate start, Coordinate end, int kingColor) {
        Piece originalPiece = board[end.row][end.col];
        board[end.row][end.col] = board[start.row][start.col];
        board[start.row][start.col] = null;

        boolean isValid = !isKingInCheck(kingColor);

        board[start.row][start.col] = board[end.row][end.col];
        board[end.row][end.col] = originalPiece;

        return isValid;
    }

    private void clearSelection() {
        selectedPiece = null;
        validMoves = null;
        selectedPiecePosition = null;
        messageLabel.setText("");
        repaint();
    }

    // Kiểm tra xem vua có đang bị chiếu tướng không
    private boolean isKingInCheck(int kingColor) {
        Coordinate kingCoordinate = findKingCoordinate(kingColor);
        if (kingCoordinate == null) {
            return false;
        }

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

    // Tìm tọa độ của vua
    private Coordinate findKingCoordinate(int kingColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece instanceof King && piece.getColor() == kingColor) {
                    return piece.getCoordinate();
                }
            }
        }
        return null;
    }

    // Kiểm tra xem vua có đang bị chiếu hết không
    private boolean isCheckmate(int kingColor) {
        if (!isKingInCheck(kingColor)) {
            return false;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == kingColor) {
                    List<Coordinate> possibleMoves = piece.getPossibleMove(board);
                    for (Coordinate targetMove : possibleMoves) {
                        if (isValidMoveWhenKingInCheck(piece.getCoordinate(), targetMove, kingColor)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    // Nhận nước đi từ server và cập nhật bàn cờ
    public void receiveMove(int startRow, int startCol, int endRow, int endCol) {
        SwingUtilities.invokeLater(() -> {
            Coordinate startCoordinate = new Coordinate(startRow, startCol);
            Coordinate endCoordinate = new Coordinate(endRow, endCol);

            selectedPiece = board[startCoordinate.row][startCoordinate.col];
            selectedPiecePosition = startCoordinate;

            movePiece(endCoordinate); 

            isWhiteTurn = !isWhiteTurn;
            myTurn = true; // Kích hoạt lại lượt của người chơi sau khi nhận nước đi từ đối thủ
            System.out.println("Nước đi đã được cập nhật từ Server.");
            repaint();
        });
    }

    // Hiển thị màn hình kết thúc game
    private void showGameEndScreen(boolean isWhiteWin) {
        new GameEndScreen(isWhiteWin);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (myTurn) { 
            int x = e.getX() / 100;
            int y = e.getY() / 100;

            if (myColor.equals("BLACK")) { 
                x = 7 - x;
                y = 7 - y;
            }

            Coordinate clickCoordinate = new Coordinate(y, x);
            handleMouseClick(clickCoordinate);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess Game");
            ChessClient client = new ChessClient();
            try {
                client.startConnection("127.0.0.1", 12345); 
                PlayOnline playOnline = new PlayOnline(client);
                frame.add(playOnline);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}