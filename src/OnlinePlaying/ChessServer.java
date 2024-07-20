package OnlinePlaying;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChessServer {
    private static final int PORT = 12345;
    public List<PlayerHandler> players = new ArrayList<>();

    public static void main(String[] args) {
        ChessServer server = new ChessServer();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("ChessServer: Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("ChessServer: Client connected: " + clientSocket.getInetAddress());
                PlayerHandler playerHandler = new PlayerHandler(clientSocket, this);
                players.add(playerHandler);
                new Thread(playerHandler).start();
            }
        } catch (IOException e) {
            System.err.println("ChessServer: Lỗi server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void broadcast(String message, PlayerHandler sender) {
        for (PlayerHandler player : players) {
            if (player != sender) {
                player.sendMessage(message);
            }
        }
    }

    public synchronized void removePlayer(PlayerHandler player) {
        players.remove(player);
        broadcast("PLAYER_DISCONNECTED " + player.getUsername(), player);
        if (player.opponent != null) {
            player.opponent.opponent = null; 
            player.opponent.sendMessage("OPPONENT_DISCONNECTED");
        }
    }

    public PlayerHandler findPlayer(String username) {
        for (PlayerHandler player : players) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public synchronized void handleChallenge(String challengerName, String challengedName) {
        PlayerHandler challenger = findPlayer(challengerName);
        PlayerHandler challenged = findPlayer(challengedName);

        if (challenger == null || challenged == null) {
            if (challenger != null) {
                challenger.sendMessage("CHALLENGE_FAILED " + challengedName + " không online.");
            }
            return;
        }

        if (challenged.opponent != null) {
            challenger.sendMessage("CHALLENGE_FAILED " + challengedName + " đang trong một ván đấu khác.");
            return;
        }
        challenged.sendMessage("CHALLENGE " + challengerName);
    }

    public synchronized void startGame(PlayerHandler player1, PlayerHandler player2) {
        player1.setOpponent(player2);
        player2.setOpponent(player1);

        // Gửi message "GAME_START" cho cả hai client
        player1.sendMessage("GAME_START"); 
        player2.sendMessage("GAME_START");

        // Gửi màu cờ và thông báo lượt chơi đầu tiên
        player1.sendMessage("START WHITE"); 
        player1.isTurn = true;
        player2.sendMessage("START BLACK");
        player2.isTurn = false;
    }
}