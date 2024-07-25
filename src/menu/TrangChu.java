package menu;

import javax.swing.*;
import OnlinePlaying.ChessClient;
import OnlinePlaying.PlayOnline;
import PlayTogether.ChessBoard;
import PlayWithAI.PlayWithAI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class TrangChu extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ChessClient client;

    public TrangChu() {
        setTitle("Cờ vua");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        client = new ChessClient();

        JPanel homePanel = createHomePanel();
        JPanel modePanel = createModePanel();

        mainPanel.add(homePanel, "home");
        mainPanel.add(modePanel, "mode");

        add(mainPanel);

        cardLayout.show(mainPanel, "home");
    }

    private JPanel createHomePanel() {
        String backgroundPath = "src\\main\\resources\\images\\MAIN.jpg";
        BackgroundPanel panel = new BackgroundPanel(backgroundPath);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playButton = createStyledButton("Chơi", e -> cardLayout.show(mainPanel, "mode"));
        JButton settingsButton = createStyledButton("Cài đặt", e -> showSettingsDialog());
        JButton historyButton = createStyledButton("Lịch sử chơi", e -> JOptionPane.showMessageDialog(this, "Lịch sử chơi chưa được cài đặt!"));
        JButton exitButton = createStyledButton("Thoát", e -> System.exit(0));

        buttonPanel.add(playButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(settingsButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(historyButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(exitButton);

        panel.add(Box.createVerticalGlue());
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog();
        settingsDialog.setTitle("Cài đặt");
        settingsDialog.setSize(400, 200);
        settingsDialog.setModal(true);
        settingsDialog.setLocationRelativeTo(this);

        JPanel settingsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label1 = new JLabel("Màu nền:");
        JTextField backgroundColorField = new JTextField("Trắng");

        JLabel label2 = new JLabel("Kích thước bàn cờ:");
        JComboBox<String> sizeComboBox = new JComboBox<>(new String[]{"8x8", "10x10", "12x12"});

        settingsPanel.add(label1);
        settingsPanel.add(backgroundColorField);
        settingsPanel.add(label2);
        settingsPanel.add(sizeComboBox);

        JButton applyButton = new JButton("Áp dụng");
        applyButton.addActionListener(e -> {
            String backgroundColor = backgroundColorField.getText();
            String boardSize = (String) sizeComboBox.getSelectedItem();
            settingsDialog.dispose();
        });

        JButton cancelButton = new JButton("Hủy bỏ");
        cancelButton.addActionListener(e -> settingsDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(actionListener);
        return button;
    }

    private JPanel createModePanel() {
        BackgroundPanel panel = new BackgroundPanel("src\\main\\resources\\images\\MAIN.jpg");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton backButton = new JButton("Quay lại");
        backButton.setFont(new Font("Arial", Font.PLAIN, 24));
        JButton onlineButton = new JButton("Chơi online");
        onlineButton.setFont(new Font("Arial", Font.PLAIN, 24));
        JButton offlineButton = new JButton("Chơi offline");
        offlineButton.setFont(new Font("Arial", Font.PLAIN, 24));
        JButton aiButton = new JButton("Chơi với máy");
        aiButton.setFont(new Font("Arial", Font.PLAIN, 24));
        JButton exitButton = new JButton("Thoát");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 24));

        onlineButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Nhập tên người dùng:", "Chơi online", JOptionPane.PLAIN_MESSAGE);
            if (username != null && !username.trim().isEmpty()) {
                // Hiển thị dialog kết nối
                showConnectDialog(username);
            }
        });

        offlineButton.addActionListener(e -> {
            JPanel chessBoardPanel = new ChessBoard();
            mainPanel.add(chessBoardPanel, "chessBoard");
            cardLayout.show(mainPanel, "chessBoard");
        });

        aiButton.addActionListener(e -> {
            JPanel aiPanel = new PlayWithAI();
            mainPanel.add(aiPanel, "aiGame");
            cardLayout.show(mainPanel, "aiGame");
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "home"));

        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(onlineButton, gbc);
        gbc.gridy++;
        panel.add(offlineButton, gbc);
        gbc.gridy++;
        panel.add(aiButton, gbc);
        gbc.gridy++;
        panel.add(backButton, gbc);
        gbc.gridy++;
        panel.add(exitButton, gbc);

        return panel;
    }

    private void showConnectDialog(String username) {
        JTextField ipField = new JTextField(15);
        JTextField portField = new JTextField(5);
        portField.setText("12345");

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Địa chỉ IP:"));
        panel.add(ipField);
        panel.add(new JLabel("Cổng:"));
        panel.add(portField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Kết nối tới server", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());

            try {
                CountDownLatch latch = new CountDownLatch(1); 
                client.startConnection(ip, port);

                new Thread(() -> {
                    try {
                        client.sendMessage("GET_PLAYERS");
                        latch.countDown(); 
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }).start();

                latch.await(); 
                client.setUsername(username);
                showOnlineLobby();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi luồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showOnlineLobby() {
        JDialog lobbyDialog = new JDialog(this, "Phòng chờ online", true);
        lobbyDialog.setSize(400, 300);
        lobbyDialog.setLayout(new BorderLayout());
    
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> playerList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(playerList);
        lobbyDialog.add(listScrollPane, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel();
        JButton challengeButton = new JButton("Thách đấu");
        JButton refreshButton = new JButton("Làm mới");
        buttonPanel.add(challengeButton);
        buttonPanel.add(refreshButton);
        lobbyDialog.add(buttonPanel, BorderLayout.SOUTH);
    
        refreshButton.addActionListener(e -> {
            try {
                listModel.clear();
                client.sendMessage("GET_PLAYERS");
                String playerListResponse = client.receiveMessage();
                if (playerListResponse.startsWith("PLAYERS ")) {
                    String[] players = playerListResponse.substring("PLAYERS ".length()).split(",");
                    for (String player : players) {
                        if (!player.trim().equals(client.getUsername())) {
                            listModel.addElement(player.trim());
                        }
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(lobbyDialog, "Lỗi kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        challengeButton.addActionListener(e -> {
            String challengedPlayer = playerList.getSelectedValue();
            if (challengedPlayer != null) {
                try {
                    client.sendMessage("CHALLENGE " + challengedPlayer);
                    JOptionPane.showMessageDialog(lobbyDialog, "Đã gửi yêu cầu thách đấu đến " + challengedPlayer);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(lobbyDialog, "Lỗi kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        try {
            client.sendMessage("GET_PLAYERS");
            String playerListResponse = client.receiveMessage();
            if (playerListResponse.startsWith("PLAYERS ")) {
                String[] players = playerListResponse.substring("PLAYERS ".length()).split(",");
                for (String player : players) {
                    if (!player.trim().equals(client.getUsername())) {
                        listModel.addElement(player.trim());
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(lobbyDialog, "Lỗi kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    
        lobbyDialog.setVisible(true);
    
        new Thread(() -> {
            try {
                while (true) {
                    String message = client.receiveMessage();
                    if (message.startsWith("CHALLENGE ")) {
                        String challengerName = message.substring("CHALLENGE ".length());
                        int choice = JOptionPane.showConfirmDialog(lobbyDialog,
                                challengerName + " muốn thách đấu bạn. Bạn có chấp nhận không?",
                                "Yêu cầu thách đấu",
                                JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            client.sendMessage("ACCEPT_CHALLENGE " + challengerName);
                        } else {
                            client.sendMessage("DENY_CHALLENGE " + challengerName);
                        }
                    } else if (message.startsWith("CHALLENGE_ACCEPTED")) {
                        // Không cần làm gì thêm ở đây
                    } else if (message.startsWith("CHALLENGE_FAILED")) {
                        String reason = message.substring("CHALLENGE_FAILED ".length());
                        JOptionPane.showMessageDialog(lobbyDialog, "Thách đấu thất bại: " + reason);
                    } else if (message.startsWith("GAME_START")) {
                        SwingUtilities.invokeLater(() -> {
                            startOnlineGame();
                            lobbyDialog.dispose();
                        });
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(lobbyDialog, "Mất kết nối đến server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }
    
    private void startOnlineGame() {
        System.out.println("Starting online game...");
        JPanel playOnlinePanel = new PlayOnline(client);
        mainPanel.add(playOnlinePanel, "playOnline");
        cardLayout.show(mainPanel, "playOnline");
        System.out.println("Switched to playOnline panel.");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrangChu().setVisible(true);
        });
    }
}
