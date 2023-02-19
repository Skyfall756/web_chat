import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
    private String host;
    private int port;
    private Scanner scanner;
    private String name;
    private Loger loger;

    public TestClient() throws IOException {
        this.loger = Loger.getLoger();
        this.host = this.getHostFromSetting("Setting.txt");
        this.port = this.getPortFromSetting("Setting.txt");
        this.scanner = new Scanner(System.in);
        System.out.println("Как вас зовут?");
        this.name = scanner.nextLine();


        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {
            out.println(name);
            out.flush();

            Thread receiveMsg = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()))) {
                    while (true) {
                        if (reader.ready()) {
                            System.out.println(reader.readLine());
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            });
            receiveMsg.start();
            while (true) {
                String msg = scanner.nextLine();
                if (msg.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    out.flush();
                    System.out.println("Вы вышли из чата");
                    break;
                }
                out.println(msg);
                out.flush();
                loger.log(name + " отправил сообщение: " + msg);
            }
            receiveMsg.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getHostFromSetting (String fileName) {
        String result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String in = reader.readLine();
            while (!in.toLowerCase().contains("host")) {
                in = reader.readLine();
            }
            result = in.substring(in.indexOf('"') + 1, in.lastIndexOf('"'));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
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
        new TestClient();

    }
}

