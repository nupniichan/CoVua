package OnlinePlaying;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChessGameLauncher {
    private static JFrame frame;
    private static ChessClient client;
    private static PlayOnline playOnline;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Chess Game Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new BorderLayout());

            JButton findServerButton = new JButton("Find Servers");
            findServerButton.addActionListener(e -> {
                try {
                    UDPClient udpClient = new UDPClient();
                    udpClient.discoverServers();
                    // TODO: Add logic to display list of found servers and allow user to select one
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            JButton connectButton = new JButton("Connect to Server");
            connectButton.addActionListener(e -> {
                try {
                    client = new ChessClient();
                    client.startConnection("127.0.0.1", 12345); // Example IP and port
                    launchGame();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            frame.add(findServerButton, BorderLayout.NORTH);
            frame.add(connectButton, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }

    private static void launchGame() {
        frame.dispose();
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Chess Game");
            playOnline = new PlayOnline(client);
            frame.add(playOnline);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
