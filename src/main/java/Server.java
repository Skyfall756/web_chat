import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static Loger loger;
    private List<Connection> connections;
    private int port;

    public Server() throws IOException {
        this.port = this.getPortFromSetting("Setting.txt");
        loger = Loger.getLoger();
        connections = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running");
            loger.log("Server is running");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Connection connection = new Connection(clientSocket, this);
                connections.add(connection);
                new Thread(connection).start();
            }
        }
    }

    public void sendMsgToAll(String message) {
        for (Connection connection : connections) {
            connection.sendMsg(message);
        }
    }

    public void deleteClient(Connection connection) {
        connections.remove(connection);
    }

    public int getPortFromSetting (String fileName) {
        String result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String in = reader.readLine();
            while (!in.toLowerCase().contains("port")) {
                in = reader.readLine();
            }
            result = in.substring(in.indexOf('"') + 1, in.lastIndexOf('"'));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Integer.parseInt(result);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
    }
}
