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
    private boolean isMyTurn;

    public boolean isTurn() {
        return isMyTurn;
    }

    public void setTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendCommand(String command) throws IOException {
        out.println(command);
    }

    public String receiveResponse() throws IOException {
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void setUsername(String username) {
        this.username = username;
        try {
            sendMessage("USERNAME " + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }
}