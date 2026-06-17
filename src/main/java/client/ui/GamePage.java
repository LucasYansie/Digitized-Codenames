package client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.GameClient;

public class GamePage extends JFrame {
    public static ArrayList<String> wordGrid = new ArrayList<>();

    private static final Map<String, JButton> wordButtonMap = new HashMap<>();
    private static final Set<String> revealedWords = new HashSet<>();

    private static JPanel boardPanel = new JPanel();
    private static CaptainWordListPanel wordListPanel = new CaptainWordListPanel();

    private static final Color BG = new Color(111, 48, 152);
    private static final Color ACCENT = new Color(153, 217, 235);


    public GamePage() {
        setTitle("CODENAMES SERVER");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.add(GameClient.chatBox, BorderLayout.PAGE_END);

        boardPanel.setLayout(new GridLayout(5, 5, 10, 10));
        boardPanel.setBackground(ACCENT);
        this.add(boardPanel, BorderLayout.CENTER);
        this.add(wordListPanel, BorderLayout.PAGE_START);
    }

    public static void updateWordGrid() {
        boardPanel.removeAll();
        wordButtonMap.clear();
        revealedWords.clear();

        for (String word : wordGrid) {
            JButton wordButton = new JButton(word);
            wordButton.setFont(new Font("Courier New", Font.BOLD, 16));
            wordButton.setOpaque(true);
            wordButton.setBorderPainted(false);
            wordButton.setBackground(BG);
            wordButton.setForeground(Color.WHITE);

            wordButton.addActionListener(e -> {
                GameClient.sendGuess(word);
            });

            wordButtonMap.put(word, wordButton);
            boardPanel.add(wordButton);
        }

        updateButtonAccess();

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public static void updateButtonAccess() {
        boolean canClick = GameClient.currentUsername != null
                && GameClient.currentUsername.equals(GameClient.currentGuesser);

        for (String word : wordButtonMap.keySet()) {
            JButton button = wordButtonMap.get(word);

            if (revealedWords.contains(word)) {
                button.setEnabled(false);
            } else {
                button.setEnabled(canClick);
            }
        }
    }

    public static void revealWord(String word, String resultType) {
        JButton button = findButtonIgnoringCase(word);

        if (button == null) {
            return;
        }

        String actualWord = button.getText();
        button.setEnabled(false);

        revealedWords.add(word);

        if (resultType.equals("TEAM1")) {
            button.setBackground(Color.BLUE);
            button.setForeground(Color.WHITE);
        } else if (resultType.equals("TEAM2")) {
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
        } else if (resultType.equals("NEUTRAL")) {
            button.setBackground(Color.LIGHT_GRAY);
        }
    }

    private static JButton findButtonIgnoringCase(String word) {
        for (String key : wordButtonMap.keySet()) {
            if (key.equalsIgnoreCase(word)) {
                return wordButtonMap.get(key);
            }
        }
        return null;
    }

    public static void setCaptain(String words) {
        wordListPanel.setWordList(words);
        wordListPanel.setVisible(true);
    }
}