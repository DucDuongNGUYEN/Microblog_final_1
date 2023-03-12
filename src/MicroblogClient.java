import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MicroblogClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    private String username;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public MicroblogClient(String username) {
        this.username = username;
    }

    public void start() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("CONNECT user:@" + username);

            String response = reader.readLine();
            if (!response.equals("OK")) {
                System.out.println("Failed to connect to server.");
                return;
            }

            Thread inputThread = new Thread(this::handleInput);
            inputThread.start();

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }

                String[] parts = line.split(" ", 2);
                String command = parts[0];
                String params = parts.length > 1 ? parts[1] : "";

                switch (command) {
                    case "PUBLISH":
                        writer.println("PUBLISH author:@" + username + " " + params);
                        break;
                    case "REPLY":
                        writer.println("PUBLISH replyto:" + params.split(" ")[0] + " author:@" + username + " " + params);
                        break;
                    case "REPUBLISH":
                        writer.println("PUBLISH author:@" + username + " " + params.split(" ")[0]);
                        break;
                    case "SUBSCRIBE":
                        writer.println("SUBSCRIBE " + params);
                        break;
                    case "UNSUBSCRIBE":
                        writer.println("UNSUBSCRIBE " + params);
                        break;
                    default:
                        System.out.println("Unknown command: " + command);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: MicroblogClient <username>");
            return;
        }

        MicroblogClient client = new MicroblogClient(args[0]);
        client.start();
    }

}
