/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PublisherClient {

    public static void main(String[] args) {

        // Demande un pseudo à l'utilisateur
        System.out.print("Entrez votre pseudo : ");
        String pseudo = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            pseudo = in.readLine();
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du pseudo : " + e.getMessage());
            System.exit(1);
        }

        // Se connecte au serveur
        try (Socket socket = new Socket("localhost", 12345)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Envoie le pseudo au serveur
            out.println("PSEUDO " + pseudo);
            String response = in.readLine();
            if (!"OK".equals(response)) {
                System.err.println("Erreur lors de l'envoi du pseudo : " + response);
                System.exit(1);
            }

            // Attend les messages de l'utilisateur et les envoie au serveur
            System.out.println("Entrez vos messages (Ctrl+D pour terminer) :");
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println("PUBLISH " + message);
                response = in.readLine();
                if (!"OK".equals(response)) {
                    System.err.println("Erreur lors de l'envoi du message : " + response);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la communication avec le serveur : " + e.getMessage());
            System.exit(1);
        }
    }

}


 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PublisherClient {
    private String user;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public PublisherClient(String host, int port) throws IOException {
        // Connexion au serveur
        socket = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(System.in));
        output = new PrintWriter(socket.getOutputStream(), true);

        // Demande du pseudo de l'utilisateur
        System.out.print("Entrez votre pseudo : ");
        user = input.readLine();
    }

    public void publish() throws IOException {
        while (true) {
            // Lecture du message de l'utilisateur
            String message = input.readLine();

            // Envoi du message au serveur via la requête PUBLISH
            output.println("PUBLISH user:@" + user + "\n" + message);

            // Arrêt de la boucle si l'utilisateur entre "exit"
            if (message.equals("exit")) {
                break;
            }
        }

        // Fermeture de la connexion
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 12345;
        PublisherClient client = new PublisherClient(host, port);
        client.publish();
    }
}

