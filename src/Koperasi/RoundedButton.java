package Koperasi;

import java.awt.*;
import javax.swing.*;

public class RoundedButton extends JButton {
    private int cornerRadius = 40; // semakin besar, semakin oval

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setBackground(new Color(255, 144, 19)); // warna oranye
        setFont(new Font("Segoe UI", Font.BOLD, 18));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // kursor tangan
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Bayangan lembut
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);

        // Warna tombol utama
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Tanpa border luar biar clean
    }

    // Efek hover
    @Override
    public void setModel(ButtonModel newModel) {
        ButtonModel oldModel = getModel();
        super.setModel(newModel);
        if (newModel != null) {
            newModel.addChangeListener(e -> {
                if (newModel.isRollover()) {
                    setBackground(new Color(255, 160, 50));
                } else {
                    setBackground(new Color(255, 144, 19));
                }
            });
        }
    }
}
