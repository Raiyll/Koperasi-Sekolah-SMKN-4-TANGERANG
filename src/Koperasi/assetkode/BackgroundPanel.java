package Koperasi.assetkode;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    /**
     * resourcePath contoh: "/Koperasi/assets/FotoSMK4.jpg"
     */
    public BackgroundPanel(String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            throw new IllegalArgumentException("Gambar tidak ditemukan di: " + resourcePath);
        }
        backgroundImage = new ImageIcon(url).getImage();
        setLayout(new BorderLayout()); // supaya child component bisa ditaruh normal
        setOpaque(false);              // panel transparan (gambar digambar manual)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
