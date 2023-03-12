import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RepostClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private List<String> authors;

    public RepostClient(List<String> authors) {
        this.authors = authors;
    }

    public void start() {
        try (Socket socket = new Socket(HOST, PORT)) {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Send REPLY command to server to enable reposting
            out.println("REPLY");

            // Loop through authors and retrieve their message IDs
            List<Long> messageIds = new ArrayList<>();
            for (String author : authors) {
                String response = sendMessage(out, "RCV_IDS", "author=" + author);
                String[] lines = response.split("\n");
                for (String line : lines) {
                    if (line.startsWith("messageId:")) {
                        long messageId = Long.parseLong(line.substring(10));
                        messageIds.add(messageId);
                    }
                }
            }

            // Repost each message
            for (long messageId : messageIds) {
                String response = sendMessage(out, "RCV_MSG", "msg_id=" + messageId);
                String[] lines = response.split("\n");
                for (String line : lines) {
                    if (line.startsWith("author=")) {
                        String author = line.substring(7);
                        String message = sendMessage(out, "PUBLISH", "author=" + author, line);
                        System.out.println("Reposted message: " + message);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendMessage(PrintWriter out, String command, String... params) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(command);
        for (String param : params) {
            sb.append(" ");
            sb.append(param);
        }
        out.println(sb.toString());

        Scanner in = new Scanner(System.in);
        StringBuilder response = new StringBuilder();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            response.append(line);
            response.append("\n");
            if (line.isEmpty()) {
                break;
            }
        }
        return response.toString();
    }

    public static void main(String[] args) {
        List<String> authors = new ArrayList<>(Arrays.asList(args));
        RepostClient client = new RepostClient(authors);
        client.start();
    }
}
