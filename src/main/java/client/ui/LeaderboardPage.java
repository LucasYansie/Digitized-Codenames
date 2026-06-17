package client.ui;

import client.GameClient;

import javax.swing.*;
import java.awt.*;

import static client.GameClient.getPicNum;
import static client.GameClient.getTeamNumber;

public class LeaderboardPage extends UIPage {
    public LeaderboardPanel leaderboard = new LeaderboardPanel();
    private int teamNumber = 1;
    private int picNumber = 1;

    public LeaderboardPage() {
        setLayout(new BorderLayout());

        this.teamNumber = getTeamNumber();
        this.picNumber = getPicNum();

        leaderboard.setPicNumber(teamNumber);
        leaderboard.setTeamNumber(picNumber);

        System.out.println("page: "+teamNumber);
        System.out.println("page: "+picNumber);

        this.add(leaderboard, BorderLayout.CENTER);

        JButton quit = new JButton("QUIT");
        quit.addActionListener(e -> {
            GameClient.sendToLobby();
        });

        this.add(quit, BorderLayout.PAGE_END);
        this.setSize(new Dimension(1100, 900));
        this.setVisible(true);
    }
}
