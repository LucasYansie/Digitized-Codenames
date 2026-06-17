package client;

import client.ui.*;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class GameClient {

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String username;
    private static int userTeam;
    public static ChatClient chatBox;
    static LeaderboardPage leaderboardPage;
    private static LobbyPage lobbyPage;
    private static GamePage gamePage;
    private static UIPage UIPage;
    private static String gameState;
    public static String currentUsername = null;
    public static String currentGuesser = null;
    public static int picNum;
    public static int teamNum;
    private static boolean isCaptain1 = false;
    private static boolean isCaptain2 = false;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 5001);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            username = JOptionPane.showInputDialog("Enter username:");
            if (username == null || username.trim().isEmpty()) {
                System.exit(0);
            }

            username = username.trim();
            currentUsername = username;
            out.println(username);

            String team = in.readLine();

            chatBox = new ChatClient(team + ":::" + username);

            gameState = "LOBBY";

            lobbyPage = new LobbyPage(username, out);
            gamePage = new GamePage();
            UIPage = new UIPage();

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Server says: " + line);

                        if (gameState.equals("LOBBY")) {
                            lobbyPage.setVisible(true);
                            switch (line) {
                                case "lobby/team-info":
                                    handleTeamUpdate();
                                    break;

                                case "START GAME":
                                    handleStartGame();
                                    break;
                                default:
                                    break;
                            }
                        } else if (gameState.equals("IN GAME")) {
                            gamePage.setVisible(true);

                            if (line.equals("WORD GRID")) {
                                handleWordGridInput();
                                SwingUtilities.invokeLater(GamePage::updateWordGrid);

                            } else if (line.startsWith("REVEAL:::")) {
                                String[] parts = line.split(":::");
                                if (parts.length >= 3) {
                                    String resultType = parts[1];
                                    String word = parts[2];

                                    SwingUtilities.invokeLater(() ->
                                            GamePage.revealWord(word, resultType)
                                    );
                                }
                            } else if (line.startsWith("CURRENT GUESSER:::")) {
                                currentGuesser = line.substring("CURRENT GUESSER:::".length()).trim();

                                SwingUtilities.invokeLater(GamePage::updateButtonAccess);
                            } else if (line.startsWith("CURRENT TURN:::")) {
                                System.out.println("Turn update: " + line);
                            } else if (line.startsWith("GAME MSG:::")) {
                                String msg = line.substring("GAME MSG:::".length());
                                System.out.println(msg);
                                if(msg.equals("TEAM 1 WINS!") || msg.equals("TEAM 2 WINS!")) {
                                    handleEndRound();
                                }
                            } else if (line.startsWith("TEAM 1 CAPTAIN:")) {
                                String subString = line.substring("TEAM 1 CAPTAIN: ".length());
                                String msg = in.readLine();
                                if(subString.equals(username)) {
                                    isCaptain1 = true;
                                    System.out.println("Setting Captain with wordlist: " + msg);
                                    GamePage.setCaptain(msg);
                                }
                                System.out.println(line);
                            } else if (line.startsWith("TEAM 2 CAPTAIN:")) {
                                String subString = line.substring("TEAM 2 CAPTAIN: ".length());
                                String msg = in.readLine();
                                if(subString.equals(username)) {
                                    isCaptain2 = true;
                                    System.out.println("Setting Captain with wordlist: " + msg);
                                    GamePage.setCaptain(msg);
                                }
                                System.out.println(line);
                            } else if (line.equals("END INFO")) {
                            } else {
                                System.out.println("Unknown input: " + line);
                            }
                        } else if(gameState.equals("LEADERBOARD")){
                            String picNumString = in.readLine();
                            System.out.println(picNumString);

                            String teamNumString = in.readLine();
                            System.out.println(teamNumString);

                            picNum = Integer.parseInt(picNumString);
                            System.out.println(picNum);

                            teamNum = Integer.parseInt(teamNumString);
                            System.out.println(teamNum);

                            handleLeaderboard();
                        }
                    }

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Connection to server lost.");
                    System.exit(0);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Server is offline!");
        }
    }

    private static void handleTeamUpdate() throws IOException {
        ArrayList<String> team1 = new ArrayList<>();
        ArrayList<String> team2 = new ArrayList<>();

        in.readLine(); // TEAM 1
        String player;
        while (!(player = in.readLine()).equals("END INFO")) {
            team1.add(player);
        }

        in.readLine(); // TEAM 2
        while (!(player = in.readLine()).equals("END INFO")) {
            team2.add(player);
        }

        in.readLine(); // USER TEAM
        userTeam = Integer.parseInt(in.readLine());

        SwingUtilities.invokeLater(() -> {
            lobbyPage.updateTeams(team1, team2);
            lobbyPage.setUserTeam(userTeam);
        });
    }

    private static void handleStartGame() {
        gameState = "IN GAME";
        SwingUtilities.invokeLater(() -> {
            lobbyPage.setVisible(false);
            gamePage.setVisible(true);
        });
    }

    private static void handleWordGridInput() throws IOException {
        GamePage.wordGrid.clear();

        for (int i = 0; i < 25; i++) {
            GamePage.wordGrid.add(in.readLine());
        }
    }
    public static void sendGuess(String word) {
        if (out != null) {
            out.println("GUESS:::" + word);
        }
    }

    private static void handleEndRound() {
        gameState = "LEADERBOARD";
        SwingUtilities.invokeLater(() -> {
            gamePage.setVisible(false);
        });
    }

    private static void handleLeaderboard() {
        gameState = "END ROUND";
        leaderboardPage = new LeaderboardPage();
    }

    public static int getTeamNumber(){
        return teamNum;
    }

    public static int getPicNum(){
        return picNum;
    }

    public static void sendToLobby() {
        gameState = "LOBBY";
        LobbyPage.resetLobby();
        ChatClient.resetChatBox();
        leaderboardPage.setVisible(false);
        lobbyPage.setVisible(true);
    }
}
