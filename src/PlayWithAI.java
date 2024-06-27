import Piece.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;

public class PlayWithAI extends JPanel implements MouseListener, MouseMotionListener {
    private Piece[][] board = new Piece[8][8];
    private Piece selectedPiece;
    private Coordinate selectedPiecePosition;
    private Map<Coordinate, Boolean> validMoves;
    private boolean isWhiteTurn = true;
    private JButton endGameButton;
    private JLabel messageLabel;

    public PlayWithAI() {
        setPreferredSize(new Dimension(800, 800));
        initializeBoard();
        addMouseListener(this);
        addMouseMotionListener(this);

        endGameButton = new JButton("End Game");
        endGameButton.addActionListener(e -> showGameEndScreen(true));
        add(endGameButton);

        messageLabel = new JLabel("");
        messageLabel.setBounds(10, 750, 300, 20);
        add(messageLabel);
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    Piece piece = board[i][j];
                    ImageIcon image = piece.getImage();
                    if (image != null) {
                        int x = piece.getCoordinate().col * 100 + (100 - image.getIconWidth()) / 2;
                        int y = piece.getCoordinate().row * 100 + (100 - image.getIconHeight()) / 2;
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
        Piece clickedPiece = board[clickCoordinate.row][clickCoordinate.col];

        if (isWhiteTurn && clickedPiece != null && clickedPiece.getColor() == Piece.WHITE) {
            selectPiece(clickedPiece, clickCoordinate);
        } else if (selectedPiece != null && validMoves != null && validMoves.containsKey(clickCoordinate)) {
            movePiece(clickCoordinate);
        } else {
            clearSelection();
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

        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;
        if (isKingInCheck(currentPlayerColor)) {
            board = previousBoard;
            selectedPiece.setCoordinate(selectedPiecePosition);
            return; 
        }


        int opponentColor = isWhiteTurn ? Piece.BLACK : Piece.WHITE;
        if (isKingInCheck(opponentColor)) {

            if (isCheckmate(opponentColor)) {
                showGameEndScreen(currentPlayerColor == Piece.WHITE);
            }
        }

        isWhiteTurn = !isWhiteTurn;
        clearSelection();
        repaint();

        if (!isGameOver() && !isWhiteTurn) {
            makeAIMove();
        }
    }

    private void makeAIMove() {
        // Sử dụng Minimax để tìm nước đi tốt nhất
        int depth = 3; // Độ sâu tìm kiếm
        Move bestMove = minimax(board, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

        if (bestMove != null) {
            selectedPiece = board[bestMove.start.row][bestMove.start.col];
            selectedPiecePosition = bestMove.start;
            movePiece(bestMove.end);
        } else {
            System.err.println("Không tìm thấy nước đi hợp lệ.");
        }
    }

    private Move minimax(Piece[][] board, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
        if (depth == 0 || isGameOver(board)) {
            return new Move(null, null, evaluate(board)); 
        }
    
        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            Move bestMove = null;
            List<Piece> aiPieces = getAIPieces(board);
            for (Piece piece : aiPieces) {
                for (Coordinate move : getValidMovesForPiece(board, piece)) {
                    Piece[][] newBoard = cloneBoard(board);
                    movePiece(newBoard, piece.getCoordinate(), move);
                    Move moveEval = minimax(newBoard, depth - 1, false, alpha, beta);
                    if (moveEval.evaluation > maxEval) {
                        maxEval = moveEval.evaluation;
                        bestMove = new Move(piece.getCoordinate(), move, maxEval);
                        alpha = Math.max(alpha, maxEval);
                        if (beta <= alpha) {
                            return bestMove; // Cắt tỉa alpha-beta
                        }
                    }
                }
            }
            return bestMove;
        } else {
            int minEval = Integer.MAX_VALUE;
            Move bestMove = null;
            List<Piece> playerPieces = getPlayerPieces(board);
            for (Piece piece : playerPieces) {
                for (Coordinate move : getValidMovesForPiece(board, piece)) {
                    Piece[][] newBoard = cloneBoard(board);
                    movePiece(newBoard, piece.getCoordinate(), move);
                    Move moveEval = minimax(newBoard, depth - 1, true, alpha, beta);
                    if (moveEval.evaluation < minEval) {
                        minEval = moveEval.evaluation;
                        bestMove = new Move(piece.getCoordinate(), move, minEval);
                        beta = Math.min(beta, minEval);
                        if (beta <= alpha) {
                            return bestMove; // Cắt tỉa alpha-beta
                        }
                    }
                }
            }
            return bestMove;
        }
    }

    // Hàm đánh giá bàn cờ
    private int evaluate(Piece[][] board) {
        int score = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    if (piece.getColor() == Piece.BLACK) {
                        score += piece.getValue(); 
                    } else {
                        score -= piece.getValue(); 
                    }
                }
            }
        }
        return score;
    }

    // Hàm kiểm tra xem trò chơi đã kết thúc
    private boolean isGameOver(Piece[][] board) {
        return false; 
    }

    // Hàm lấy danh sách quân cờ của AI (màu đen)
    private List<Piece> getAIPieces(Piece[][] board) {
        List<Piece> aiPieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == Piece.BLACK) {
                    aiPieces.add(piece);
                }
            }
        }
        return aiPieces;
    }

    // Hàm lấy danh sách quân cờ của người chơi (màu trắng)
    private List<Piece> getPlayerPieces(Piece[][] board) {
        List<Piece> playerPieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() == Piece.WHITE) {
                    playerPieces.add(piece);
                }
            }
        }
        return playerPieces;
    }

    // Hàm lấy danh sách nước đi hợp lệ cho một quân cờ
    private List<Coordinate> getValidMovesForPiece(Piece[][] board, Piece piece) {
        List<Coordinate> validMoves = new ArrayList<>();
        List<Coordinate> possibleMoves = piece.getPossibleMove(board);
        for (Coordinate move : possibleMoves) {
            if (isValidMoveWhenKingInCheck(board, piece.getCoordinate(), move, piece.getColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    // Hàm kiểm tra xem nước đi có hợp lệ khi vua đang bị chiếu
    private boolean isValidMoveWhenKingInCheck(Piece[][] board, Coordinate start, Coordinate end, int kingColor) {
        Piece originalPiece = board[end.row][end.col];
        board[end.row][end.col] = board[start.row][start.col];
        board[start.row][start.col] = null;

        boolean isValid = !isKingInCheck(board, kingColor);

        board[start.row][start.col] = board[end.row][end.col];
        board[end.row][end.col] = originalPiece;

        return isValid;
    }

    // Hàm kiểm tra xem vua có đang bị chiếu
    private boolean isKingInCheck(Piece[][] board, int kingColor) {
        Coordinate kingCoordinate = findKingCoordinate(board, kingColor);
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

    // Hàm tìm tọa độ của vua
    private Coordinate findKingCoordinate(Piece[][] board, int kingColor) {
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

    // Hàm di chuyển quân cờ
    private void movePiece(Piece[][] board, Coordinate start, Coordinate end) {
        board[end.row][end.col] = board[start.row][start.col];
        board[start.row][start.col] = null;
        board[end.row][end.col].setCoordinate(end);
    }

    // Hàm tạo bản sao của bàn cờ
    private Piece[][] cloneBoard(Piece[][] board) {
        Piece[][] newBoard = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    Coordinate newCoordinate = new Coordinate(board[i][j].getCoordinate().row, board[i][j].getCoordinate().col);
                    if (board[i][j] instanceof Rook) {
                        newBoard[i][j] = new Rook(board[i][j].getColor(), newCoordinate);
                    } else if (board[i][j] instanceof Knight) {
                        newBoard[i][j] = new Knight(board[i][j].getColor(), newCoordinate);
                    } else if (board[i][j] instanceof Bishop) {
                        newBoard[i][j] = new Bishop(board[i][j].getColor(), newCoordinate);
                    } else if (board[i][j] instanceof Queen) {
                        newBoard[i][j] = new Queen(board[i][j].getColor(), newCoordinate);
                    } else if (board[i][j] instanceof King) {
                        newBoard[i][j] = new King(board[i][j].getColor(), newCoordinate);
                    } else if (board[i][j] instanceof Pawn) {
                        newBoard[i][j] = new Pawn(board[i][j].getColor(), newCoordinate);
                    }
                }
            }
        }
        return newBoard;
    }

    // Class Move
    private class Move {
        Coordinate start;
        Coordinate end;
        int evaluation;

        Move(Coordinate start, Coordinate end, int evaluation) {
            this.start = start;
            this.end = end;
            this.evaluation = evaluation;
        }
    }

    // Các hàm còn lại của class PlayWithAI
    private void selectPiece(Piece clickedPiece, Coordinate clickCoordinate) {
        selectedPiece = clickedPiece;
        selectedPiecePosition = clickCoordinate;
        validMoves = new HashMap<>();

        messageLabel.setText("Đã bấm vào quân cờ " + clickedPiece.getType() + " " + clickedPiece.getColorString());
        List<Coordinate> possibleMoves = selectedPiece.getPossibleMove(board);

        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;

        if (isKingInCheck(currentPlayerColor)) {
            Iterator<Coordinate> iterator = possibleMoves.iterator();
            while (iterator.hasNext()) {
                Coordinate move = iterator.next();
                if (!isValidMoveWhenKingInCheck(selectedPiece.getCoordinate(), move, currentPlayerColor)) {
                    iterator.remove();
                }
            }
        }

        for (Coordinate move : possibleMoves) {
            validMoves.put(move, true);
        }

        repaint();
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
                        Piece originalPiece = board[targetMove.row][targetMove.col];
                        board[targetMove.row][targetMove.col] = piece;
                        board[piece.getCoordinate().row][piece.getCoordinate().col] = null;
                        Coordinate originalCoordinate = piece.getCoordinate();
                        piece.setCoordinate(targetMove);

                        if (!isKingInCheck(kingColor)) {
                            board[targetMove.row][targetMove.col] = originalPiece;
                            board[originalCoordinate.row][originalCoordinate.col] = piece;
                            piece.setCoordinate(originalCoordinate);
                            return false;
                        }

                        board[targetMove.row][targetMove.col] = originalPiece;
                        board[originalCoordinate.row][originalCoordinate.col] = piece;
                        piece.setCoordinate(originalCoordinate);
                    }
                }
            }
        }
        return true;
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
        Coordinate clickCoordinate = new Coordinate(y, x);
        handleMouseClick(clickCoordinate);
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
}