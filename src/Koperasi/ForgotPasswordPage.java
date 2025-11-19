package Koperasi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPasswordPage extends JFrame {

    private JTextField txtKodeAnggota;
    private JTextField txtUsername;
    private JTextField txtNoTelp;
    private JPasswordField txtPasswordBaru;
    private JButton btnReset;
    private JButton btnKembali;

    private int attemptCount = 0; // hitung percobaan

    public ForgotPasswordPage() {
        setTitle("Lupa Password - Koperasi Sekolah");
        setSize(450, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(new Color(0, 116, 217));

        JLabel lblTitle = new JLabel("Lupa Password");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(140, 20, 200, 30);
        add(lblTitle);

        JPanel panelForm = new JPanel(null);
        panelForm.setBackground(Color.WHITE);
        panelForm.setBounds(40, 70, 360, 240);
        panelForm.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(panelForm);

        int y = 15;

        JLabel lblKode = new JLabel("Kode Anggota");
        lblKode.setBounds(20, y, 120, 25);
        panelForm.add(lblKode);

        txtKodeAnggota = new JTextField();
        txtKodeAnggota.setBounds(150, y, 180, 25);
        panelForm.add(txtKodeAnggota);

        y += 35;

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(20, y, 120, 25);
        panelForm.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, y, 180, 25);
        panelForm.add(txtUsername);

        y += 35;

        JLabel lblNoTelp = new JLabel("No. Telepon");
        lblNoTelp.setBounds(20, y, 120, 25);
        panelForm.add(lblNoTelp);

        txtNoTelp = new JTextField();
        txtNoTelp.setBounds(150, y, 180, 25);
        panelForm.add(txtNoTelp);

        y += 35;

        JLabel lblPassBaru = new JLabel("Password Baru");
        lblPassBaru.setBounds(20, y, 120, 25);
        panelForm.add(lblPassBaru);

        txtPasswordBaru = new JPasswordField();
        txtPasswordBaru.setBounds(150, y, 180, 25);
        panelForm.add(txtPasswordBaru);

        y += 45;

        btnReset = new JButton("Reset Password");
        btnReset.setBounds(40, y, 140, 30);
        btnReset.setBackground(new Color(255, 144, 19));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        panelForm.add(btnReset);

        btnKembali = new JButton("Kembali");
        btnKembali.setBounds(190, y, 140, 30);
        btnKembali.setBackground(new Color(150, 150, 150));
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFocusPainted(false);
        panelForm.add(btnKembali);

        // Aksi tombol
        btnReset.addActionListener(this::handleResetPassword);
        btnKembali.addActionListener(e -> dispose());
    }

    private void handleResetPassword(ActionEvent evt) {
        String kode = txtKodeAnggota.getText().trim();
        String username = txtUsername.getText().trim();
        String noTelp = txtNoTelp.getText().trim();
        String passwordBaru = new String(txtPasswordBaru.getPassword()).trim();

        if (kode.isEmpty() || username.isEmpty() || noTelp.isEmpty() || passwordBaru.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Jika sudah terblokir di UI, jangan lanjut
        if (attemptCount >= 3) {
            JOptionPane.showMessageDialog(this,
                    "User telah diblokir, silahkan hubungi pegawai.",
                    "Diblokir",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlCek = """
                SELECT kode_anggota, username, no_telp, tipe_anggota, status_anggota
                FROM anggota
                WHERE kode_anggota = ? AND username = ? AND no_telp = ? AND tipe_anggota = 'Siswa'
                """;

        try (Connection conn = ConnectionDB.getConnection()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Koneksi database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cek kredensial
            try (PreparedStatement ps = conn.prepareStatement(sqlCek)) {
                ps.setString(1, kode);
                ps.setString(2, username);
                ps.setString(3, noTelp);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // tipe_anggota sudah dipastikan 'Siswa'
                        // Update password
                        String sqlUpdate = "UPDATE anggota SET password = ? WHERE kode_anggota = ?";

                        try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                            psUpdate.setString(1, passwordBaru);
                            psUpdate.setString(2, kode);
                            int updated = psUpdate.executeUpdate();

                            if (updated > 0) {
                                JOptionPane.showMessageDialog(this,
                                        "Password berhasil direset.\nSilakan login dengan password baru.",
                                        "Sukses",
                                        JOptionPane.INFORMATION_MESSAGE);
                                dispose(); // tutup form lupa password
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Gagal mengupdate password.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        // Kredensial tidak valid
                        attemptCount++;
                        if (attemptCount >= 3) {
                            // Blokir user (jika kode_anggota ada & tipe siswa)
                            blokirUser(conn, username);
                            btnReset.setEnabled(false);
                            JOptionPane.showMessageDialog(this,
                                    "User telah diblokir, silahkan hubungi pegawai.",
                                    "Diblokir",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Kredensial tidak valid! Percobaan: " + attemptCount + " / 3",
                                    "Gagal",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan database: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean blokirUser(Connection conn, String username) {
        String sql = """
            UPDATE anggota
            SET status_anggota = 'Tidak Aktif'
            WHERE username = ? AND tipe_anggota = 'Siswa'
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
