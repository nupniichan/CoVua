package menu;
import javax.swing.*;

import PlayTogether.ChessBoard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameEndScreen extends JFrame {

    public GameEndScreen(boolean isWhiteWin) {
        setTitle("Game Over");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); 
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel winnerLabel = new JLabel(isWhiteWin ? "White wins!" : "Black wins!");
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(winnerLabel);

        JButton rematchButton = new JButton("Tái đấu");
        rematchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng màn hình kết thúc game
                ChessBoard chessBoard = new ChessBoard();
                JFrame frame = new JFrame("Chess Game");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(chessBoard);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        add(rematchButton);

        JButton backButton = new JButton("Quay lại trang chủ");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng màn hình kết thúc game
                // Chuyển đổi sang màn hình TrangChu.java
                TrangChu trangChu = new TrangChu();
                trangChu.setVisible(true);
            }
        });
        add(backButton);

        JButton exitButton = new JButton("Thoát game");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Thoát game hoàn toàn
            }
        });
        add(exitButton);

        setVisible(true);
    }
}
