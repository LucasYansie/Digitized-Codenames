package client.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LeaderboardPanel extends JPanel{
    private int teamNumber = 0;
    private Image backgroundImage;
    private int picNumber = 0;

    public void setTeamNumber(int number){
        this.teamNumber = number;
    }

    public void setPicNumber(int number){
        this.picNumber = number;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawLeaderboard(g2d);
    }

    public void drawLeaderboard(Graphics2D g2d){
        Image newBackgound;
        try {
            backgroundImage = ImageIO.read(new File("src/main/java/client/ui/LeaderboardPic/TEAM"+teamNumber+"_"+picNumber+".png"));
            newBackgound = backgroundImage.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        g2d.drawImage(newBackgound, 0, 0, this);
    }

}