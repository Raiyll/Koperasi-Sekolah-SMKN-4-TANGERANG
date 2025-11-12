package Koperasi;

import java.awt.*;
import javax.swing.*;

public class RoundedTextField extends JTextField {
    private int cornerRadius = 15;

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setForeground(Color.WHITE);
        setBackground(new Color(0, 120, 215));
        setCaretColor(Color.WHITE);
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14)); // padding biar teks gak nempel
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Warna background halus
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setStroke(new BasicStroke(1.8f)); // tebal garis
        g2.setColor(new Color(255, 144, 19)); // oranye
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

        g2.dispose();
    }
}
