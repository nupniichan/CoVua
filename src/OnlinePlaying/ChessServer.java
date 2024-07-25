package OnlinePlaying;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChessServer {
    private static final int PORT = 12345;
    private Set<PlayerHandler> players = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        new ChessServer().start();
    }

    public void start() {
        System.out.println("Chess server is starting on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(clientSocket, this);
                players.add(playerHandler);
                new Thread(playerHandler).start();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Set<PlayerHandler> getPlayers() {
        return players;
    }

    public PlayerHandler getPlayerByUsername(String username) {
        synchronized (players) {
            for (PlayerHandler player : players) {
                if (player.getUsername() != null && player.getUsername().equals(username)) {
                    return player;
                }
            }
        }
        return null;
    }

    public void removePlayer(PlayerHandler player) {
        players.remove(player);
        System.out.println("Player removed: " + player.getUsername());
    }
    public PlayerHandler getOpponent(PlayerHandler player) {
        synchronized (players) {
            for (PlayerHandler p : players) {
                if (p != player) {
                    return p;
                }
            }
        }
        return null;
    }
}
