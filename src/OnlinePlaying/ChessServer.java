package OnlinePlaying;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChessServer {
    private ServerSocket serverSocket;

    public ChessServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is running on port " + port);
    }

    public void start() throws IOException {
        // Tạo một luồng riêng để lắng nghe thông điệp UDP
        new Thread(() -> {
            try (DatagramSocket udpSocket = new DatagramSocket(4445)) {
                byte[] buf = new byte[256];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    udpSocket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    if (received.equals("DISCOVER_SERVER_REQUEST")) {
                        String response = "DISCOVER_SERVER_RESPONSE";
                        buf = response.getBytes();
                        InetAddress address = packet.getAddress();
                        int port = packet.getPort();
                        packet = new DatagramPacket(buf, buf.length, address, port);
                        udpSocket.send(packet);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");
            new ClientHandler(clientSocket).start();
        }
    }

    public static void main(String[] args) {
        try {
            ChessServer server = new ChessServer(12345); // Port 12345
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
