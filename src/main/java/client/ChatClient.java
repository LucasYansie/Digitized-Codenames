package client;

import server.GameServer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class ChatClient extends JPanel{
    static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static JTextPane textBox;
    private static StyledDocument textArea;
    private static JTextField textField;
    private static String username;

    public ChatClient(String usernameInput) {
//        this.setSize(500, 400);
        this.setPreferredSize(new Dimension(super.getWidth(), 200));
        try {
            // Get username from user through a popup window
            username = usernameInput;

            socket = new Socket("localhost", 5002);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send TEAM# with username to server
            out.println(username);

            textBox = new JTextPane();
            textArea = textBox.getStyledDocument();
            textBox.setEditable(false);
//            textArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(textBox);

            textField = new JTextField();
            JButton sendButton = new JButton("Send");

            // Send message when user presses Enter or clicks the Send button
            textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendMessage();
                }
            });

            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendMessage();
                }
            });

            this.setLayout(new BorderLayout());
            this.add(scrollPane, BorderLayout.CENTER);
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            bottomPanel.add(textField, BorderLayout.CENTER);
            bottomPanel.add(sendButton, BorderLayout.EAST);
            this.add(bottomPanel, BorderLayout.SOUTH);

            this.setVisible(true);

            //Create Styles for chatBox team colours
            Style team1Style = textArea.addStyle("team1Style", null);
            StyleConstants.setForeground(team1Style, Color.BLUE);
            Style team2Style = textArea.addStyle("team2Style", null);
            StyleConstants.setForeground(team2Style, Color.RED);

            //Add custom styles to JTextPane
            textBox.setStyledDocument(textArea);

            // Listen for incoming messages from the server in a background thread
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            Scanner inputMessage = new Scanner(message);
                            inputMessage.useDelimiter(":::");
                            String typeMessage = inputMessage.next();
//                            System.out.println("typeMessage: " + typeMessage);

                            if (Objects.equals(typeMessage, "message")) {
                                String teamNum = inputMessage.next();
                                String inputUsername = inputMessage.next();
                                String messageToPrint = inputMessage.next();
                                //debug print
//                                System.out.println("inputUsername: " + inputUsername);
//                                System.out.println("messageToPrint: " + messageToPrint);

                                if(Objects.equals(teamNum, "TEAM1")){
//                                    System.out.println("IN TEAM 1 TRUE");
                                    textArea.insertString(textArea.getLength(), inputUsername + ": ", team1Style);
                                    textArea.insertString(textArea.getLength(), messageToPrint + "\n", null);
                                } else {
//                                    System.out.println("IN TEAM 1 FALSE");
                                    textArea.insertString(textArea.getLength(), inputUsername + ": ", team2Style);
                                    textArea.insertString(textArea.getLength(), messageToPrint + "\n", null);
                                }
                            } else{
                                String line = inputMessage.next();
                                textArea.insertString(textArea.getLength(), line + "\n", null);
                            }

                        }
                    } catch (IOException | BadLocationException e) {
                        // Handle socket closure and any input/output errors
                        if (!socket.isClosed()) {
                            e.printStackTrace();
                        }
                    } finally {
                        // Ensure proper socket closure if an exception occurs
                        try {
                            if (socket != null && !socket.isClosed()) {
                                socket.close();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message
    private static void sendMessage() {
        String message = textField.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);  // Send the message to the server
            textField.setText("");  // Clear the text field after sending
        }
    }

    public static void resetChatBox() {
        textBox.setText("Welcome to the game. Communicate with team members here!\n");
    }
}
