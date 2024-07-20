package OnlinePlaying;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChessClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private boolean isWhite;
    public boolean isTurn;

    public void startConnection(String ip, int port) throws IOException {
        System.out.println("ChessClient: Đang thử kết nối tới " + ip + ":" + port + "...");
        clientSocket = new Socket(ip, port);
        System.out.println("ChessClient: Kết nối thành công!");
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void setUsername(String username) {
        this.username = username;
        try {
            System.out.println("ChessClient: Gửi USERNAME: " + username);
            sendMessage("USERNAME " + username);
        } catch (IOException e) {
            System.err.println("ChessClient: Lỗi khi gửi username: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void receiveColor(String color) {
        if (color.equalsIgnoreCase("WHITE")) {
            this.isWhite = true;
            this.isTurn = true; 
        } else if (color.equalsIgnoreCase("BLACK")) {
            this.isWhite = false;
            this.isTurn = false; 
        } else {
            System.err.println("Invalid color received from server: " + color);
        }
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}