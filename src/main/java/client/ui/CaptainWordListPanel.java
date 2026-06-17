package client.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;



public class CaptainWordListPanel extends JPanel {
    private final JLabel wordList = new JLabel("uGuessedIt!");  //Store wordlist for captain, or 'uGuessedIt!' for other players
    private static final Color ACCENT = new Color(153, 217, 235);

    public CaptainWordListPanel() {
        this.setOpaque(true);
        this.setBackground(ACCENT);
        this.setPreferredSize(new Dimension(super.getWidth(), 50));
        this.add(wordList);
        this.setBackground(ACCENT);
    }

    public void setWordList(String list) {
        SwingUtilities.invokeLater(() -> {
            wordList.setText(list);
        });
    }
}
