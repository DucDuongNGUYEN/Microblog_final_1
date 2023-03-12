import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class FollowerClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            // Connexion au serveur
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Demande des identifiants des messages à suivre
            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez les utilisateurs à suivre (séparés par des espaces) : ");
            String[] users = scanner.nextLine().split(" ");
            for (String user : users) {
                String request = "RCV_IDS author:" + user;
                out.println(request);
                String response = in.readLine();
                if (response.startsWith("MSG_IDS")) {
                    String[] ids = response.substring(8).split("\n");
                    for (String id : ids) {
                        // Récupération du contenu des messages
                        out.println("RCV_MSG msg_id:" + id.trim());
                        response = in.readLine();
                        if (response.startsWith("MSG")) {
                            String[] parts = response.split("\\s+");
                            String message = "";
                            for (int i = 4; i < parts.length; i++) {
                                message += parts[i] + " ";
                            }
                            System.out.println("@" + parts[2] + " : " + message);
                        }
                    }
                }
            }

            // Fermeture de la connexion
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
