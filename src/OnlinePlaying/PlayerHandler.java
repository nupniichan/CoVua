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
    private String username;
    private ChessServer server;
    public PlayerHandler opponent; // Đối thủ của người chơi
    public boolean isTurn; // Theo dõi lượt của người chơi

    public PlayerHandler(Socket socket, ChessServer server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true); 
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("PlayerHandler: Lỗi khi tạo PlayerHandler: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Nhận từ " + username + ": " + inputLine);
                handleMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Người chơi ngắt kết nối: " + username);
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
            server.removePlayer(this); // Xóa người chơi khỏi server
        }
    }

    private void handleMessage(String message) {
        String[] parts = message.split(" ", 2);
        String command = parts[0];

        switch (command) {
            case "GET_PLAYERS": // Yêu cầu lấy danh sách người chơi
                sendPlayerList();
                break;
            case "USERNAME": // Nhận tên người dùng khi kết nối
                this.username = parts[1];
                System.out.println("Người chơi đã kết nối: " + username);
                break;
            case "CHALLENGE": // Xử lý yêu cầu thách đấu
                server.handleChallenge(this.username, parts[1]);
                break;
            case "ACCEPT_CHALLENGE": // Xử lý chấp nhận thách đấu
                PlayerHandler challengedPlayer = server.findPlayer(parts[1]);
                if (challengedPlayer != null) {
                    server.startGame(this, challengedPlayer);
                }
                break;
            case "MOVE": // Xử lý nước đi của người chơi
                if (opponent != null) {
                    opponent.sendMessage(message);
                    // Đổi lượt chơi sau khi di chuyển
                    isTurn = !isTurn;
                    opponent.isTurn = !opponent.isTurn; 
                } else {
                    System.err.println("Lỗi: Chưa tìm thấy đối thủ.");
                    sendMessage("ERROR_OPPONENT_NOT_FOUND"); // Thông báo lỗi cho client
                }
                break;
            case "DENY_CHALLENGE": // Xử lý từ chối thách đấu
                PlayerHandler challenger = server.findPlayer(parts[1]);
                if (challenger != null) {
                    challenger.sendMessage("CHALLENGE_DENIED " + this.username);
                }
                break;
            // Xử lý các message khác từ client ở đây...
            default:
                System.err.println("Lệnh không hợp lệ: " + command);
                break;
        }
    }

    // Gửi danh sách người chơi cho client
    private void sendPlayerList() {
        StringBuilder playerList = new StringBuilder("PLAYERS ");
        for (PlayerHandler player : server.players) {
            if (player != this) { // Không bao gồm chính người chơi đó trong danh sách
                playerList.append(player.getUsername()).append(",");
            }
        }
        // Xóa dấu phẩy thừa ở cuối chuỗi
        if (playerList.lastIndexOf(",") == playerList.length() - 1) {
            playerList.deleteCharAt(playerList.length() - 1);
        }
        sendMessage(playerList.toString());
    }

    // Gửi message đến client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Lấy tên người dùng
    public String getUsername() {
        return username;
    }

    // Thiết lập đối thủ cho người chơi
    public void setOpponent(PlayerHandler opponent) {
        this.opponent = opponent;
    }
}