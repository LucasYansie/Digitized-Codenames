package client.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LobbyPage extends UIPage {

    // Colour / font setup
    private static final Color BG = new Color(111, 48, 152);
    private static final Color ACCENT = new Color(153, 217, 235);

    private static final Font TITLE_FONT = new Font("Courier New", Font.BOLD, 45);
    private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 20);
    private static final Font LIST_FONT  = new Font("Courier New", Font.BOLD, 25);

    private final DefaultListModel<String> team1Model = new DefaultListModel<>();
    private final DefaultListModel<String> team2Model = new DefaultListModel<>();
    private final JList<String> team1List = new JList<>(team1Model);
    private final JList<String> team2List = new JList<>(team2Model);
    private JLabel team1Label;
    private JLabel team2Label;
    private static JLabel readyLabel;
    private static JButton readyButton;


    private int userTeam;

    public LobbyPage(String username, PrintWriter out) {
        super();
        setTitle("Lobby");
        getContentPane().setBackground(BG);

        JLabel titleLabel = new JLabel("Lobby", SwingConstants.CENTER);
        styleLabel(titleLabel, TITLE_FONT);

        styleList(team1List);
        styleList(team2List);

        JScrollPane team1Pane = new JScrollPane(team1List);
        JScrollPane team2Pane = new JScrollPane(team2List);
        styleScrollPane(team1Pane);
        styleScrollPane(team2Pane);

        team1Label = new JLabel("Team 1", SwingConstants.CENTER);
        team2Label = new JLabel("Team 2", SwingConstants.CENTER);
        styleLabel(team1Label, LABEL_FONT);
        styleLabel(team2Label, LABEL_FONT);

        JPanel team1Panel = new JPanel(new BorderLayout(5, 5));
        team1Panel.setBackground(BG);
        team1Panel.setOpaque(true);
        team1Panel.add(team1Label, BorderLayout.NORTH);
        team1Panel.add(team1Pane, BorderLayout.CENTER);

        JPanel team2Panel = new JPanel(new BorderLayout(5, 5));
        team2Panel.setBackground(BG);
        team2Panel.setOpaque(true);
        team2Panel.add(team2Label, BorderLayout.NORTH);
        team2Panel.add(team2Pane, BorderLayout.CENTER);

        JPanel teamDisplayTable = new JPanel(new GridLayout(1, 2, 20, 10));
        teamDisplayTable.setBackground(BG);
        teamDisplayTable.setOpaque(true);
        teamDisplayTable.add(team1Panel);
        teamDisplayTable.add(team2Panel);

        
        JPanel teamDisplayPanel = new JPanel(new BorderLayout(10, 10));
        teamDisplayPanel.setBackground(BG);
        teamDisplayPanel.setOpaque(true);
        teamDisplayPanel.add(titleLabel, BorderLayout.NORTH);
        teamDisplayPanel.add(teamDisplayTable, BorderLayout.CENTER);

        
        readyLabel = new JLabel("Click Ready to join a team!", SwingConstants.CENTER);
        styleLabel(readyLabel, LABEL_FONT);

        readyButton = new JButton("Ready");
        readyButton.setBackground(ACCENT);
        readyButton.setForeground(Color.BLACK);
        readyButton.setFont(LABEL_FONT);
        readyButton.setFocusPainted(false);
        readyButton.setPreferredSize(new Dimension((int) (this.getWidth()*0.5), 50));

        JPanel readyPanel = new JPanel(new BorderLayout(10, 10));
        readyPanel.setBackground(BG);
        readyPanel.setOpaque(true);
        readyPanel.add(readyLabel, BorderLayout.NORTH);
        readyPanel.add(readyButton, BorderLayout.SOUTH);

        add(teamDisplayPanel, BorderLayout.CENTER);
        add(readyPanel, BorderLayout.SOUTH);

        readyButton.addActionListener((ActionEvent e) -> {
            out.println("READY");
            readyButton.setEnabled(false);
            SwingUtilities.invokeLater(() ->
                readyLabel.setText("Waiting for other players...")
            );
        });
    }

    // these functions help style stuff easier, instead of repeating a bunch of code every time
    private void styleLabel(JLabel label, Font font) {
        label.setFont(font);
        label.setForeground(Color.BLACK);
        label.setBackground(BG);
        label.setOpaque(true);
    }

    private void styleList(JList<String> list) {
        list.setBackground(BG);
        list.setForeground(Color.BLACK);
        list.setFont(LIST_FONT);
        list.setFixedCellHeight(32);
        list.setOpaque(true);
    }

    private void styleScrollPane(JScrollPane pane) {
        pane.getViewport().setBackground(BG);
        pane.setBackground(BG);
    }

    public void setUserTeam(int team) {
        userTeam = team;
        SwingUtilities.invokeLater(() -> {
            if (team == 1) {
                team1Label.setText("Team 1 (Your team)");
            } else {
                team2Label.setText("Team 2 (Your team)");
            }
        });
    }

    public void updateTeams(ArrayList<String> team1, ArrayList<String> team2) {
        SwingUtilities.invokeLater(() -> {
            team1Model.clear();
            for (String p : team1) team1Model.addElement(p);

            team2Model.clear();
            for (String p : team2) team2Model.addElement(p);
        });
    }

    public static void resetLobby() {
        readyButton.setEnabled(true);
        SwingUtilities.invokeLater(() ->
                readyLabel.setText("Click Ready to join a team!")
        );
    }
}