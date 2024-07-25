package OnlinePlaying;

import Piece.*;
import menu.GameEndScreen;

import javax.swing.*;
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
        System.out.println("Initializing PlayOnline");
        this.client = client;
        setPreferredSize(new Dimension(800, 800));
        initializeBoard();
        addMouseListener(this);
        addMouseMotionListener(this);

        setLayout(null);
        messageLabel = new JLabel("");
        messageLabel.setBounds(10, 750, 300, 20);
        add(messageLabel);

        System.out.println("Waiting to receive START message from server...");
        try {
            String startMessage = client.receiveMessage();
            System.out.println("Received message: " + startMessage);
            if (startMessage != null && startMessage.startsWith("START")) {
                String color = startMessage.split(" ")[1];
                myColor = color;
                myTurn = myColor.equals("WHITE");

                System.out.println("Game start, my color: " + myColor + ", my turn: " + myTurn);

                new Thread(this::startReceivingMessages).start();
            } else {
                System.err.println("PlayOnline: Không nhận được message START từ server!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("PlayOnline: Lỗi khi nhận message START từ server!");
        }
    }

    private void startReceivingMessages() {
        try {
            while (true) {
                String message = client.receiveMessage();
                System.out.println("Client " + myColor + " nhận được message: " + message);
                if (message.startsWith("MOVE")) {
                    String[] parts = message.split(" ");
                    int startRow = Integer.parseInt(parts[1]);
                    int startCol = Integer.parseInt(parts[2]);
                    int endRow = Integer.parseInt(parts[3]);
                    int endCol = Integer.parseInt(parts[4]);
                    SwingUtilities.invokeLater(() -> receiveMove(startRow, startCol, endRow, endCol));
                } else if (message.startsWith("TURN")) {
                    myTurn = Boolean.parseBoolean(message.split(" ")[1]);
                    repaint();
                } else if (message.startsWith("BOARD")) {
                    String boardString = message.substring("BOARD ".length());
                    updateBoardFromString(boardString);
                    repaint();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("PlayOnline: Lỗi khi nhận message từ server!");
        }
    }

    private void initializeBoard() {
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

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
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
                int drawRow = row;
                int drawCol = col;
                if (myColor.equals("BLACK")) {
                    drawRow = 7 - row;
                    drawCol = 7 - col;
                }
                g.setColor(isWhite ? Color.WHITE : Color.GRAY);
                g.fillRect(drawCol * 100, drawRow * 100, 100, 100);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    private void drawPieces(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    int drawRow = piece.getCoordinate().row;
                    int drawCol = piece.getCoordinate().col;
                    if (myColor.equals("BLACK")) {
                        drawRow = 7 - piece.getCoordinate().row;
                        drawCol = 7 - piece.getCoordinate().col;
                    }
                    ImageIcon image = piece.getImage();
                    int x = drawCol * 100 + (100 - image.getIconWidth()) / 2;
                    int y = drawRow * 100 + (100 - image.getIconHeight()) / 2;
                    image.paintIcon(this, g, x, y);
                }
            }
        }
    }

    private void drawValidMoves(Graphics g) {
        if (validMoves != null) {
            for (Coordinate move : validMoves.keySet()) {
                int drawRow = move.row;
                int drawCol = move.col;
                if (myColor.equals("BLACK")) {
                    drawRow = 7 - move.row;
                    drawCol = 7 - move.col;
                }

                if (board[move.row][move.col] != null) {
                    g.setColor(new Color(255, 0, 0, 100)); // Đỏ nếu có quân cờ tại nước đi
                } else {
                    g.setColor(new Color(0, 255, 0, 100)); // Xanh nếu ô trống
                }

                g.fillRect(drawCol * 100, drawRow * 100, 100, 100);
            }
        }

        if (selectedPiecePosition != null) {
            int drawRow = selectedPiecePosition.row;
            int drawCol = selectedPiecePosition.col;
            if (myColor.equals("BLACK")) {
                drawRow = 7 - selectedPiecePosition.row;
                drawCol = 7 - selectedPiecePosition.col;
            }
            g.setColor(new Color(0, 0, 255, 100)); // Màu xanh cho quân cờ được chọn
            g.fillRect(drawCol * 100, drawRow * 100, 100, 100);
        }
    }

    private void handleMouseClick(Coordinate clickCoordinate) {
        if (!myTurn) {
            System.out.println("Không phải lượt của bạn!");
            return;
        }

        Piece clickedPiece = board[clickCoordinate.row][clickCoordinate.col];
        System.out.println("Clicked piece: " + (clickedPiece != null ? clickedPiece.getType() + " " + clickedPiece.getColorString() : "None"));

        if (clickedPiece == null) {
            if (selectedPiece != null && validMoves != null && validMoves.containsKey(clickCoordinate)) {
                movePiece(clickCoordinate);
            } else {
                clearSelection();
            }
        } else {
            if (clickedPiece != null && clickedPiece.getColorString().equals(myColor)) {
                selectPiece(clickedPiece, clickCoordinate);
            } else if (selectedPiece != null && validMoves != null && validMoves.containsKey(clickCoordinate)) {
                movePiece(clickCoordinate);
            } else {
                clearSelection();
            }
        }
    }

    private void movePiece(Coordinate targetCoordinate) {
        Piece[][] previousBoard = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                previousBoard[i][j] = board[i][j];
            }
        }

        board[targetCoordinate.row][targetCoordinate.col] = selectedPiece;
        board[selectedPiecePosition.row][selectedPiecePosition.col] = null;
        selectedPiece.setCoordinate(targetCoordinate);
        selectedPiece.hasMoved = true;

        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;
        if (isKingInCheck(currentPlayerColor)) {
            System.out.println("Move invalid: King is in check.");
            board = previousBoard;
            selectedPiece.setCoordinate(selectedPiecePosition);
            return;
        }

        if (selectedPiece instanceof King && Math.abs(targetCoordinate.col - selectedPiecePosition.col) == 2) {
            handleCastling(targetCoordinate);
        }

        int opponentColor = isWhiteTurn ? Piece.BLACK : Piece.WHITE;
        if (isKingInCheck(opponentColor)) {
            if (isCheckmate(opponentColor)) {
                System.out.println("Checkmate! Game over.");
                showGameEndScreen(myColor.equals("WHITE"));
            }
        }

        if (selectedPiece instanceof Pawn && (targetCoordinate.row == 0 || targetCoordinate.row == 7)) {
            promotePawn(targetCoordinate);
        }

        isWhiteTurn = !isWhiteTurn;
        myTurn = false;

        try {
            client.sendMessage("MOVE " + selectedPiecePosition.row + " " + selectedPiecePosition.col + " " + targetCoordinate.row + " " + targetCoordinate.col);
            System.out.println("Client " + myColor + " sent message: MOVE " + selectedPiecePosition.row + " " + selectedPiecePosition.col + " " + targetCoordinate.row + " " + targetCoordinate.col);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearSelection();
        repaint();

        System.out.println("isWhiteTurn: " + isWhiteTurn);
        System.out.println("myTurn: " + myTurn);
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

        if (isKingInCheck(currentPlayerColor)) {
            for (Iterator<Coordinate> iterator = possibleMoves.iterator(); iterator.hasNext();) {
                Coordinate move = iterator.next();
                if (!isValidMoveWhenKingInCheck(selectedPiece.getCoordinate(), move, currentPlayerColor)) {
                    iterator.remove();
                }
            }
        }

        if (clickedPiece instanceof King) {
            List<Coordinate> castlingMoves = ((King) clickedPiece).getValidCastlingMoves(board);
            for (Coordinate move : castlingMoves) {
                validMoves.put(move, true);
            }
        }

        for (Coordinate move : possibleMoves) {
            validMoves.put(move, true);
        }

        repaint();
    }

    private void handleCastling(Coordinate kingTarget) {
        int rookStartCol, rookTargetCol;

        if (kingTarget.col == 2) { // Nhập thành dài
            rookStartCol = 0;
            rookTargetCol = 3;
        } else { // Nhập thành ngắn
            rookStartCol = 7;
            rookTargetCol = 5;
        }

        board[kingTarget.row][rookTargetCol] = board[kingTarget.row][rookStartCol];
        board[kingTarget.row][rookStartCol] = null;
        board[kingTarget.row][rookTargetCol].setCoordinate(new Coordinate(kingTarget.row, rookTargetCol));
        board[kingTarget.row][rookTargetCol].hasMoved = true;
    }

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

    public void receiveMove(int startRow, int startCol, int endRow, int endCol) {
        SwingUtilities.invokeLater(() -> {
            Piece piece = board[startRow][startCol];
            if (piece != null) {
                board[endRow][endCol] = piece;
                piece.setCoordinate(new Coordinate(endRow, endCol));
                board[startRow][startCol] = null;
                piece.hasMoved = true;

                if (piece.getColorString().equals(myColor)) {
                    myTurn = false;
                } else {
                    myTurn = true;
                }

                isWhiteTurn = !isWhiteTurn;
                clearSelection();
                repaint();
            }
        });
    }

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
        } else {
            System.out.println("Không phải lượt của bạn!");
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

    private void updateBoardFromString(String boardString) {
        String[] rows = boardString.split(";");
        for (int i = 0; i < 8; i++) {
            String[] pieces = rows[i].split(",");
            for (int j = 0; j < 8; j++) {
                board[i][j] = createPieceFromShortName(pieces[j], i, j);
            }
        }
    }

    private Piece createPieceFromShortName(String shortName, int row, int col) {
        if (shortName.equals("--")) {
            return null;
        }

        int color = (shortName.charAt(0) == 'W') ? Piece.WHITE : Piece.BLACK;
        switch (shortName.charAt(1)) {
            case 'K':
                return new King(color, new Coordinate(row, col));
            case 'Q':
                return new Queen(color, new Coordinate(row, col));
            case 'R':
                return new Rook(color, new Coordinate(row, col));
            case 'B':
                return new Bishop(color, new Coordinate(row, col));
            case 'N':
                return new Knight(color, new Coordinate(row, col));
            case 'P':
                return new Pawn(color, new Coordinate(row, col));
            default:
                return null;
        }
    }
}
