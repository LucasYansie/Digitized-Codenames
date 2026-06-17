package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final Set<ClientHandler> clientHandlers = new HashSet<>();
    private static final Map<String, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new ChatServer();
    }

    public ChatServer() throws IOException {
        final int PORT = 5002;
        System.out.println("Chat server started at port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private String team;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Prompt client for username
                Scanner scanner = new Scanner(in.readLine());
                scanner.useDelimiter(":::");
                team = scanner.next();
                username = scanner.next();
                synchronized (clients) {
                    clients.put(username, this);
                }

                //Broadcast welcome message to players
                out.println("welcomeMessage:::Welcome to the game. Communicate with team members here!");

                // Notify other clients about new players
                synchronized (clientHandlers) {
                    for (ClientHandler handler : clientHandlers) {
                        handler.out.println("gameJoin:::" + username + " has joined the game!");
                    }
                }

                //Add each clientHandler to clientHandlers set
                synchronized (clientHandlers) {
                    clientHandlers.add(this);
                }

                //Continuously listen for incoming messages
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(username + ": " + message);
                    synchronized (clientHandlers) {
                        for(ClientHandler handler : clientHandlers) {
                            handler.out.println("message:::" + team + ":::" + username + ":::" + message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientHandlers) {
                    clientHandlers.remove(this);
                }
                synchronized (clients) {
                    clients.remove(username);
                }

                // Notify others when a client leaves
                synchronized (clientHandlers) {
                    for (ClientHandler handler : clientHandlers) {
                        handler.out.println("exitAnnouncement:::" + username + " has left the game.");
                    }
                }
            }
        }
    }


}
