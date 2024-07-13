package OnlinePlaying;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;
    private List<String> servers;

    public UDPClient() throws IOException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("255.255.255.255"); // Broadcast address
        servers = new ArrayList<>();
    }

    public List<String> discoverServers() throws IOException {
        String request = "DISCOVER_SERVER_REQUEST";
        buf = request.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);

        socket.setSoTimeout(5000); // 5 seconds timeout
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000) {
            try {
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (received.equals("DISCOVER_SERVER_RESPONSE")) {
                    String serverInfo = packet.getAddress().getHostAddress() + ":12345"; // Fixed port for simplicity
                    if (!servers.contains(serverInfo)) {
                        servers.add(serverInfo);
                    }
                }
            } catch (IOException e) {
                // Ignore timeout exceptions
            }
        }
        socket.close();
        return servers;
    }
}
