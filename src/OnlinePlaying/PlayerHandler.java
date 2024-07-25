package OnlinePlaying;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ChessServer server;
    private String username;

    public PlayerHandler(Socket socket, ChessServer server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error setting up player handler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(username + " says: " + inputLine);
                processCommand(inputLine);
            }
        } catch (IOException e) {
            System.err.println("Error handling client input: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void processCommand(String inputLine) {
        if (inputLine.startsWith("USERNAME")) {
            this.username = inputLine.split(" ")[1];
            sendMessage("USERNAME_ACCEPTED");
        } else if (inputLine.equals("GET_PLAYERS")) {
            if (this.username != null) {
                StringBuilder playersList = new StringBuilder("PLAYERS ");
                for (PlayerHandler player : server.getPlayers()) {
                    if (!player.getUsername().equals(this.getUsername())) {
                        playersList.append(player.getUsername()).append(",");
                    }
                }
                sendMessage(playersList.toString());
            } else {
                sendMessage("USERNAME_NOT_SET");
            }
        } else if (inputLine.startsWith("CHALLENGE")) {
            if (this.username != null) {
                String challengedUsername = inputLine.split(" ")[1];
                PlayerHandler challengedPlayer = server.getPlayerByUsername(challengedUsername);
                if (challengedPlayer != null) {
                    challengedPlayer.sendMessage("CHALLENGE " + this.username);
                } else {
                    sendMessage("CHALLENGE_FAILED User not found");
                }
            } else {
                sendMessage("USERNAME_NOT_SET");
            }
        } else if (inputLine.startsWith("ACCEPT_CHALLENGE")) {
            String challengerUsername = inputLine.split(" ")[1];
            PlayerHandler challenger = server.getPlayerByUsername(challengerUsername);
            if (challenger != null) {
                challenger.sendMessage("CHALLENGE_ACCEPTED " + this.username);
                // Bắt đầu trò chơi cho cả hai người chơi
                sendMessage("GAME_START");
                challenger.sendMessage("GAME_START");
                // Gửi thông điệp START đến cả hai người chơi
                sendMessage("START WHITE");
                challenger.sendMessage("START BLACK");
            }
        } else if (inputLine.startsWith("DENY_CHALLENGE")) {
            String challengerUsername = inputLine.split(" ")[1];
            PlayerHandler challenger = server.getPlayerByUsername(challengerUsername);
            if (challenger != null) {
                challenger.sendMessage("CHALLENGE_DENIED " + this.username);
            }
        } else if (inputLine.startsWith("MOVE")) {
            // Xử lý thông điệp MOVE từ một người chơi và gửi đến đối thủ
            String[] parts = inputLine.split(" ");
            String move = parts[1] + " " + parts[2] + " " + parts[3] + " " + parts[4];
            PlayerHandler opponent = server.getOpponent(this);
            if (opponent != null) {
                opponent.sendMessage("MOVE " + move);
            }
        }
        // Các lệnh khác ở đây...
    }
    
    
    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            server.removePlayer(this);
        } catch (IOException e) {
            System.err.println("Error closing player connection: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }
}
