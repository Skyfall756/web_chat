import java.io.*;
import java.net.Socket;

public class Connection implements Runnable {
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private Loger loger;

    public Connection(Socket socket, Server server) {
        try {
            this.loger = Loger.getLoger();
            this.clientSocket = socket;
            this.server = server;
            this.out = new PrintWriter(clientSocket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        try {
            clientName = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.sendMsgToAll(clientName + " присоединился к чату");
        System.out.println("Подключился новый пользователь: " + clientName + ", " +
                clientSocket.getPort());
        loger.log(clientName + " зашел в чат");
        try {
            while (!clientSocket.isClosed()) {
                if (in.ready()) {
                    String msgFromClient = in.readLine();
                    if (msgFromClient.equalsIgnoreCase("exit")) {
                        in.close();
                        out.close();
                        clientSocket.close();
                        loger.log(clientName + " вышел из чата");
                    } else server.sendMsgToAll("[" + clientName + "]: " + msgFromClient);
                }
            }
            server.sendMsgToAll(clientName + " вышел из чата!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        server.deleteClient(this);
    }

    public void sendMsg(String msg) {
        try {
            out.println(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
