package Koperasi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PEMBELIANPAGE extends JFrame {

    private JTextField txtKodeTransaksi, txtKodeAnggota, txtNamaLengkap, txtKelas, txtNoTelp;
    private JComboBox<String> comboStatusAnggota;

    private JTextField txtNamaBarang, txtHargaSatuan, txtStok, txtJumlah, txtTotalHarga;
    private JComboBox<String> comboKategori;

    private DefaultTableModel modelKeranjang, modelRiwayat;
    private JTable tableKeranjang, tableRiwayat;

    private JButton btnMinus, btnPlus;

    public PEMBELIANPAGE() {
        setTitle("Koperasi Sekolah");
        setSize(1200, 950);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0, 116, 217));

        // ==========================
        // LOGO DAN TITLE (SIDE BY SIDE)
        // ==========================
        // ==========================

        ImageIcon iconLogo = new ImageIcon("src/Koperasi/assets/logosmk4 (1).png");
        Image scaledLogo = iconLogo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        logo.setBounds(520, 30, 100, 100);
        add(logo);


        // Panel untuk title di sebelah kanan logo
        JPanel titlePanel = new JPanel(null);
        titlePanel.setBounds(630, 30, 300, 100);
        titlePanel.setBackground(new Color(0, 116, 217));
        add(titlePanel);

        JLabel title1 = new JLabel("KOPERASI SEKOLAH");
        title1.setFont(new Font("SansSerif", Font.BOLD, 24));
        title1.setForeground(Color.WHITE);
        title1.setBounds(0, 20, 300, 30);
        titlePanel.add(title1);

        JLabel title2 = new JLabel("SMKN 4 TANGERANG");
        title2.setFont(new Font("SansSerif", Font.BOLD, 22));
        title2.setForeground(Color.WHITE);
        title2.setBounds(0, 55, 300, 30);
        titlePanel.add(title2);

        // =====================================================
        // CARD : DATA ANGGOTA
        // =====================================================
        JPanel cardAnggota = new JPanel(null);
        cardAnggota.setBounds(280, 160, 425, 270);
        cardAnggota.setBackground(Color.WHITE);
        cardAnggota.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardAnggota);

        JPanel headerA = new JPanel(null);
        headerA.setBounds(0, 0, 425, 40);
        headerA.setBackground(new Color(220, 100, 20));
        cardAnggota.add(headerA);

        JLabel lblA = new JLabel("Data Anggota");
        lblA.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblA.setForeground(Color.WHITE);
        lblA.setBounds(15, 5, 300, 30);
        headerA.add(lblA);

        // Fields Data Anggota
        String[] labelA = {
                "Kode Transaksi",
                "Kode Anggota",
                "Nama Lengkap",
                "Kelas",
                "Status Anggota",
                "No Telp"
        };

        int y = 60;

        txtKodeTransaksi = new JTextField();
        txtKodeAnggota = new JTextField();
        txtNamaLengkap = new JTextField();
        txtKelas = new JTextField();
        comboStatusAnggota = new JComboBox<>(new String[]{"Aktif", "Tidak Aktif"});
        txtNoTelp = new JTextField();

        JComponent[] componentsA = {
                txtKodeTransaksi, txtKodeAnggota, txtNamaLengkap,
                txtKelas, comboStatusAnggota, txtNoTelp
        };

        for (int i = 0; i < labelA.length; i++) {
            JLabel l = new JLabel(labelA[i]);
            l.setBounds(20, y, 150, 25);
            cardAnggota.add(l);

            componentsA[i].setBounds(180, y, 200, 25);
            cardAnggota.add(componentsA[i]);

            y += 32;
        }

        // =====================================================
// CARD : PILIH BARANG (VERSI RAPi)
// =====================================================
        JPanel cardBarang = new JPanel(null);
        cardBarang.setBounds(755, 160, 425, 270); // tinggi normal
        cardBarang.setBackground(Color.WHITE);
        cardBarang.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardBarang);

        JPanel headerB = new JPanel(null);
        headerB.setBounds(0, 0, 425, 40);
        headerB.setBackground(new Color(220, 100, 20));
        cardBarang.add(headerB);

        JLabel lblB = new JLabel("Pilih Barang");
        lblB.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblB.setForeground(Color.WHITE);
        lblB.setBounds(15, 5, 300, 30);
        headerB.add(lblB);

// Fields Pilih Barang
        String[] labelB = {
                "Nama Barang",
                "Harga Satuan",
                "Kategori Barang",
                "Stok",
                "Jumlah",
                "Total Harga"
        };

        y = 60;

        txtNamaBarang = new JTextField();
        txtHargaSatuan = new JTextField();
        comboKategori = new JComboBox<>(new String[]{
                "Makanan", "Minuman", "ATK", "Seragam"
        });
        txtStok = new JTextField();
        txtJumlah = new JTextField("1");
        txtJumlah.setHorizontalAlignment(SwingConstants.CENTER);
        txtTotalHarga = new JTextField();
        txtTotalHarga.setEditable(false);

        for (int i = 0; i < labelB.length; i++) {
            JLabel l = new JLabel(labelB[i]);
            l.setBounds(20, y, 150, 25);
            cardBarang.add(l);

            if (i == 2) {
                comboKategori.setBounds(180, y, 200, 25);
                cardBarang.add(comboKategori);

            } else if (i == 4) {
                // Jumlah dengan tombol +/-
                btnMinus = new JButton("-");
                btnMinus.setBounds(180, y, 45, 25);
                btnMinus.setBackground(Color.RED);
                btnMinus.setForeground(Color.WHITE);
                btnMinus.setFocusPainted(false);

                txtJumlah.setBounds(230, y, 50, 25);

                btnPlus = new JButton("+");
                btnPlus.setBounds(285, y, 45, 25);
                btnPlus.setBackground(Color.GREEN);
                btnPlus.setForeground(Color.WHITE);
                btnPlus.setFocusPainted(false);

                cardBarang.add(btnMinus);
                cardBarang.add(txtJumlah);
                cardBarang.add(btnPlus);

                // Event listeners untuk +/-
                btnMinus.addActionListener(e -> {
                    try {
                        int val = Integer.parseInt(txtJumlah.getText());
                        if (val > 1) {
                            txtJumlah.setText(String.valueOf(val - 1));
                            hitungTotalHarga();
                        }
                    } catch (NumberFormatException ex) {
                        txtJumlah.setText("1");
                    }
                });

                btnPlus.addActionListener(e -> {
                    try {
                        int val = Integer.parseInt(txtJumlah.getText());
                        txtJumlah.setText(String.valueOf(val + 1));
                        hitungTotalHarga();
                    } catch (NumberFormatException ex) {
                        txtJumlah.setText("1");
                    }
                });

            } else {
                JTextField field = null;
                switch(i) {
                    case 0: field = txtNamaBarang; break;
                    case 1: field = txtHargaSatuan; break;
                    case 3: field = txtStok; break;
                    case 5: field = txtTotalHarga; break;
                }
                field.setBounds(180, y, 200, 25);
                cardBarang.add(field);
            }
            y += 28; // dikurangi dari 32 agar muat di card
        }

// Auto calculate total harga
        txtHargaSatuan.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
            }
        });

// Tombol Tambah ke Keranjang
        JButton btnKeranjang = new JButton("Tambah ke Keranjang");
        btnKeranjang.setBounds(125, 225, 175, 30); // posisi pas di dalam card
        btnKeranjang.setBackground(new Color(0, 102, 204));
        btnKeranjang.setForeground(Color.WHITE);
        btnKeranjang.setFocusPainted(false);
        btnKeranjang.addActionListener(e -> tambahKeKeranjang());
        cardBarang.add(btnKeranjang);


        // =====================================================
        // CARD : KERANJANG BELANJA
        // =====================================================
        JPanel cardCart = new JPanel(null);
        cardCart.setBounds(450, 460, 570, 210);
        cardCart.setBackground(Color.WHITE);
        cardCart.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardCart);

        JPanel headerC = new JPanel(null);
        headerC.setBounds(0, 0, 570, 40);
        headerC.setBackground(new Color(220, 100, 20));
        cardCart.add(headerC);

        JLabel lblC = new JLabel("Keranjang Belanja");
        lblC.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblC.setForeground(Color.WHITE);
        lblC.setBounds(15, 5, 300, 30);
        headerC.add(lblC);

        // Tabel Keranjang
        modelKeranjang = new DefaultTableModel(
                new Object[]{"Barang", "Jumlah", "Harga", "Total"}, 0
        );
        tableKeranjang = new JTable(modelKeranjang);
        tableKeranjang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollCart = new JScrollPane(tableKeranjang);
        scrollCart.setBounds(20, 60, 530, 95);
        scrollCart.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCart.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardCart.add(scrollCart);

        // Tombol-tombol
        JButton btnReset = new JButton("Reset Form");
        btnReset.setBounds(90, 165, 120, 30);
        btnReset.setBackground(new Color(220, 100, 20));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetForm());
        cardCart.add(btnReset);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(225, 165, 120, 30);
        btnSimpan.setBackground(new Color(220, 100, 20));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(e -> simpanTransaksi());
        cardCart.add(btnSimpan);

        JButton btnHapus = new JButton("Hapus Item");
        btnHapus.setBounds(360, 165, 120, 30);
        btnHapus.setBackground(new Color(220, 100, 20));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusItem());
        cardCart.add(btnHapus);

        // =====================================================
        // CARD : RIWAYAT TRANSAKSI
        // =====================================================
        JPanel cardHistory = new JPanel(null);
        cardHistory.setBounds(280, 700, 900, 200);
        cardHistory.setBackground(Color.WHITE);
        cardHistory.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardHistory);

        JPanel headerD = new JPanel(null);
        headerD.setBounds(0, 0, 900, 40);
        headerD.setBackground(new Color(220, 100, 20));
        cardHistory.add(headerD);

        JLabel lblD = new JLabel("Riwayat Transaksi");
        lblD.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblD.setForeground(Color.WHITE);
        lblD.setBounds(20, 5, 300, 30);
        headerD.add(lblD);

        // Tabel Riwayat
        modelRiwayat = new DefaultTableModel(
                new Object[]{"Kode", "Anggota", "Total", "Tanggal"}, 0
        );
        tableRiwayat = new JTable(modelRiwayat);
        tableRiwayat.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollHistory = new JScrollPane(tableRiwayat);
        scrollHistory.setBounds(20, 60, 860, 120);
        scrollHistory.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollHistory.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardHistory.add(scrollHistory);

        setVisible(true);
    }

    private void hitungTotalHarga() {
        try {
            int harga = Integer.parseInt(txtHargaSatuan.getText());
            int jumlah = Integer.parseInt(txtJumlah.getText());
            int total = harga * jumlah;
            txtTotalHarga.setText(String.valueOf(total));
        } catch (NumberFormatException ex) {
            txtTotalHarga.setText("0");
        }
    }

    private void tambahKeKeranjang() {
        if (txtNamaBarang.getText().isEmpty() || txtHargaSatuan.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi data barang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String namaBarang = txtNamaBarang.getText();
            int jumlah = Integer.parseInt(txtJumlah.getText());
            int harga = Integer.parseInt(txtHargaSatuan.getText());
            int total = Integer.parseInt(txtTotalHarga.getText());

            modelKeranjang.addRow(new Object[]{namaBarang, jumlah, harga, total});

            // Reset field barang
            txtNamaBarang.setText("");
            txtHargaSatuan.setText("");
            txtStok.setText("");
            txtJumlah.setText("1");
            txtTotalHarga.setText("");
            comboKategori.setSelectedIndex(0);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusItem() {
        int selectedRow = tableKeranjang.getSelectedRow();
        if (selectedRow >= 0) {
            modelKeranjang.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Item berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void simpanTransaksi() {
        if (txtKodeTransaksi.getText().isEmpty() || txtNamaLengkap.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lengkapi data anggota!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (modelKeranjang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hitung total belanja
        int totalBelanja = 0;
        for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
            totalBelanja += (int) modelKeranjang.getValueAt(i, 3);
        }

        // Format tanggal
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String tanggal = sdf.format(new Date());

        // Tambah ke riwayat
        modelRiwayat.addRow(new Object[]{
                txtKodeTransaksi.getText(),
                txtNamaLengkap.getText(),
                totalBelanja,
                tanggal
        });

        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!\nTotal: Rp " + totalBelanja, "Sukses", JOptionPane.INFORMATION_MESSAGE);

        // Reset form
        resetForm();
    }

    private void resetForm() {
        // Reset Data Anggota
        txtKodeTransaksi.setText("");
        txtKodeAnggota.setText("");
        txtNamaLengkap.setText("");
        txtKelas.setText("");
        comboStatusAnggota.setSelectedIndex(0);
        txtNoTelp.setText("");

        // Reset Pilih Barang
        txtNamaBarang.setText("");
        txtHargaSatuan.setText("");
        comboKategori.setSelectedIndex(0);
        txtStok.setText("");
        txtJumlah.setText("1");
        txtTotalHarga.setText("");

        // Clear Keranjang
        modelKeranjang.setRowCount(0);

        JOptionPane.showMessageDialog(this, "Form berhasil direset!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PEMBELIANPAGE());
    }
}