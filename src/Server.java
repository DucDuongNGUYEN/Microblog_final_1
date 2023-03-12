import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static long nextMessageId = 1;
    private static List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter output = new PrintWriter(clientSocket.getOutputStream())) {

                String request = input.readLine();

                String[] requestParts = request.split(" ", 2);
               // String response = "";

                // Handle PUBLISH requests
                if (requestParts[0].equals("PUBLISH")) {
                    String author = requestParts[1].substring(7); // remove "author:" prefix
                    String content = input.readLine();

                    long id = nextMessageId++;
                    Message message = new Message(id, author, content);
                    messages.add(message);

                    System.out.println("New message published: " + message);
                    output.println("OK");
                    //response = "OK";
                    // Handle RCV_IDS requests
                } else if (requestParts[0].equals("RCV_IDS")) {
                    String author = null;
                    String tag = null;
                    long sinceId = 0;
                    int limit = 5;

                    for (int i = 1; i < requestParts.length; i++) {
                        String part = requestParts[i];

                        if (part.startsWith("author:")) {
                            author = part.substring(7);
                        } else if (part.startsWith("tag:")) {
                            tag = part.substring(4);
                        } else if (part.startsWith("since_id:")) {
                            sinceId = Long.parseLong(part.substring(9));
                        } else if (part.startsWith("limit:")) {
                            limit = Integer.parseInt(part.substring(6));
                        }
                    }

                    List<Long> messageIds = new ArrayList<>();
                    synchronized (messages) {
                        for (int i = messages.size() - 1; i >= 0 && messageIds.size() < limit; i--) {
                            Message message = messages.get(i);

                            if ((author == null || message.getAuthor().equals(author)) &&
                                    (tag == null || message.getContent().contains("#" + tag)) &&
                                    message.getId() > sinceId) {
                                messageIds.add(message.getId());
                            }
                        }
                    }

                    StringBuilder responseBuilder = new StringBuilder("MSG_IDS\n");
                    for (long messageId : messageIds) {
                        responseBuilder.append(messageId).append("\n");
                    }
                   // response = responseBuilder.toString();
                    output.println(responseBuilder.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}