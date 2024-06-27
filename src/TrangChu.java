import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrangChu extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    public TrangChu() {
        setTitle("Cờ vua");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel homePanel = createHomePanel();
        JPanel modePanel = createModePanel();
        // Không cần khởi tạo ChessBoard ở đây nữa

        mainPanel.add(homePanel, "home");
        mainPanel.add(modePanel, "mode");
        // Không cần thêm chessBoardPanel vào đây nữa

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
        System.out.println();
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
            // Ví dụ: Chuyển đến panel "chessBoard" (bạn cần định nghĩa panel này)
            cardLayout.show(mainPanel, "chessBoard");
        });

        offlineButton.addActionListener(e -> {
            // Khởi tạo ChessBoard cho chế độ chơi offline
            JPanel chessBoardPanel = new ChessBoard();
            mainPanel.add(chessBoardPanel, "chessBoard");
            cardLayout.show(mainPanel, "chessBoard");
        });

        aiButton.addActionListener(e -> {
            // Khởi tạo PlayWithAI cho chế độ chơi với máy
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrangChu().setVisible(true);
        });
    }
}