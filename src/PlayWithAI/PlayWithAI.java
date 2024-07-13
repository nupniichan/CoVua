package PlayWithAI;
import Piece.*;
import menu.GameEndScreen;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;
import java.awt.event.ActionEvent;

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
        // Initialize White Pieces
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

        // Initialize Black Pieces
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

        // Set the rest of the board to null (empty)
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
                    g.setColor(new Color(255, 0, 0, 100)); // Red for capturing
                } else {
                    g.setColor(new Color(0, 255, 0, 100)); // Green for valid moves
                }

                g.fillRect(x, y, 100, 100);
            }
        }

        if (selectedPiecePosition != null) {
            g.setColor(new Color(0, 0, 255, 100)); // Blue for selected piece
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
        Piece[][] previousBoard = cloneBoard(board); 
        movePiece(board, selectedPiecePosition, targetCoordinate); 
        selectedPiece.hasMoved = true;

        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;
        if (isKingInCheck(currentPlayerColor)) { 
            board = previousBoard;
            selectedPiece.setCoordinate(selectedPiecePosition);
            return; 
        }

        if (selectedPiece instanceof King && Math.abs(targetCoordinate.col - selectedPiecePosition.col) == 2) {
            handleCastling(board, targetCoordinate);
        }

        if (selectedPiece instanceof Pawn && 
            (targetCoordinate.row == 0 || targetCoordinate.row == 7)) {
            promotePawn(targetCoordinate); 
        }

        isWhiteTurn = !isWhiteTurn;
        clearSelection();
        repaint();

        if (!isGameOver(board) && !isWhiteTurn) {
            makeAIMove();
        }
    }
    
    private void movePiece(Piece[][] board, Coordinate start, Coordinate end) {
        Piece pieceToMove = board[start.row][start.col];
        board[end.row][end.col] = pieceToMove;
        board[start.row][start.col] = null;
        if (pieceToMove != null) { 
            pieceToMove.setCoordinate(end);
            pieceToMove.hasMoved = true; 
        }
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

    private void makeAIMove() {
        int depth = 3; 
        Move bestMove = minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true); 

        if (bestMove != null) {
            selectedPiece = board[bestMove.start.row][bestMove.start.col];
            selectedPiecePosition = bestMove.start;
            movePiece(bestMove.end); 
        } else {
            System.err.println("AI has no valid moves!");
        }
    }

    private Move minimax(Piece[][] board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || isGameOver(board)) {
            return new Move(null, null, evaluate(board)); 
        }

        Move bestMove = new Move(null, null, isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE);

        List<Piece> pieces = isMaximizingPlayer ? getAIPieces(board) : getPlayerPieces(board);
        for (Piece piece : pieces) {
            for (Coordinate move : getValidMovesForPiece(board, piece)) {
                Piece[][] newBoard = cloneBoard(board);
                movePiece(newBoard, piece.getCoordinate(), move); 

                if (newBoard[move.row][move.col] instanceof King && 
                    Math.abs(move.col - piece.getCoordinate().col) == 2) {
                    handleCastling(newBoard, move); 
                }

                int eval = minimax(newBoard, depth - 1, alpha, beta, !isMaximizingPlayer).evaluation;

                if (isMaximizingPlayer) {
                    if (eval > bestMove.evaluation) {
                        bestMove = new Move(piece.getCoordinate(), move, eval);
                    }
                    alpha = Math.max(alpha, eval);
                } else {
                    if (eval < bestMove.evaluation) {
                        bestMove = new Move(piece.getCoordinate(), move, eval);
                    }
                    beta = Math.min(beta, eval);
                }
                if (beta <= alpha) {
                    break; 
                }
            }
        }
        return bestMove;
    }

    private int evaluate(Piece[][] board) {
        int score = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    int pieceValue = piece.getValue();
                    if (piece instanceof Pawn) {
                        pieceValue += (piece.getColor() == Piece.WHITE) ? (row - 1) * 10 : (6 - row) * 10; 
                    }
                    score += (piece.getColor() == Piece.BLACK) ? pieceValue : -pieceValue;
                }
            }
        }
        return score;
    }
    
    private boolean isGameOver(Piece[][] board) {
        return isCheckmate(board, Piece.WHITE) || isCheckmate(board, Piece.BLACK);
    } 

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

    private List<Coordinate> getValidMovesForPiece(Piece[][] board, Piece piece) {
        List<Coordinate> validMoves = new ArrayList<>();
        List<Coordinate> possibleMoves = piece.getPossibleMove(board);

        if (piece instanceof King) {
            possibleMoves.addAll(((King) piece).getValidCastlingMoves(board));
        }

        for (Coordinate move : possibleMoves) {
            if (isValidMoveWhenKingInCheck(board, piece.getCoordinate(), move, piece.getColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private boolean isValidMoveWhenKingInCheck(Piece[][] board, Coordinate start, Coordinate end, int kingColor) {
        Piece originalPiece = board[end.row][end.col];
        board[end.row][end.col] = board[start.row][start.col];
        board[start.row][start.col] = null;

        boolean isValid = !isKingInCheck(board, kingColor);

        board[start.row][start.col] = board[end.row][end.col];
        board[end.row][end.col] = originalPiece;

        return isValid;
    }

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

    private boolean isCheckmate(Piece[][] board, int kingColor) {
        if (!isKingInCheck(board, kingColor)) {
            return false; 
        }

        Coordinate kingCoordinate = findKingCoordinate(board, kingColor);
        if (kingCoordinate != null) {
            Piece king = board[kingCoordinate.row][kingCoordinate.col];
            if (getValidMovesForPiece(board, king).size() > 0) {
                return false; 
            }
        }

        return true; 
    }

    private void handleCastling(Piece[][] board, Coordinate kingTarget) {
        int rookStartCol, rookTargetCol;
        if (kingTarget.col == 2) { // Nhập thành bên trái
            rookStartCol = 0;
            rookTargetCol = 3;
        } else { // Nhập thành bên phải
            rookStartCol = 7;
            rookTargetCol = 5;
        }
    
        // Di chuyển Xe
        board[kingTarget.row][rookTargetCol] = board[kingTarget.row][rookStartCol];
        board[kingTarget.row][rookStartCol] = null;
        board[kingTarget.row][rookTargetCol].setCoordinate(new Coordinate(kingTarget.row, rookTargetCol));
    
        // Đánh dấu Xe đã được di chuyển
        board[kingTarget.row][rookTargetCol].hasMoved = true; 
    }

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
                    newBoard[i][j].hasMoved = board[i][j].hasMoved;
                }
            }
        }
        return newBoard;
    }

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

    private void selectPiece(Piece clickedPiece, Coordinate clickCoordinate) {
        selectedPiece = clickedPiece;
        selectedPiecePosition = clickCoordinate;
        validMoves = new HashMap<>(); 
    
        messageLabel.setText("Selected: " + clickedPiece.getType() + " (" + clickedPiece.getColorString() + ")");
    
        List<Coordinate> possibleMoves = selectedPiece.getPossibleMove(board);
    
        // **Thêm phần này để tính đến nước đi nhập thành:**
        if (selectedPiece instanceof King) {
            possibleMoves.addAll(((King) selectedPiece).getValidCastlingMoves(board));
        }
        // **Hết phần bổ sung**
    
        int currentPlayerColor = isWhiteTurn ? Piece.WHITE : Piece.BLACK;
        if (isKingInCheck(currentPlayerColor)) {
            Iterator<Coordinate> iterator = possibleMoves.iterator();
            while (iterator.hasNext()) {
                Coordinate move = iterator.next();
                if (!isValidMoveWhenKingInCheck(board, selectedPiece.getCoordinate(), move, currentPlayerColor)) {
                    iterator.remove();
                }
            }
        }
    
        for (Coordinate move : possibleMoves) {
            validMoves.put(move, true); 
        }
    
        repaint();
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