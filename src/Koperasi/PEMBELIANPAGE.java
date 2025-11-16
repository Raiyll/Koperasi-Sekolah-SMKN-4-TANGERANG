package Koperasi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PEMBELIANPAGE extends JFrame {

    private JTextField txtKodeTransaksi, txtKodeAnggota, txtNamaLengkap, txtKelas, txtNoTelp;
    private JComboBox<String> comboStatusAnggota;

    private JComboBox<BarangItem> comboNamaBarang;
    private JTextField txtKategoriBarang;
    private JTextField txtHargaSatuan, txtStok, txtJumlah, txtTotalHarga;

    private DefaultTableModel modelKeranjang, modelRiwayat;
    private JTable tableKeranjang, tableRiwayat;

    private JButton btnMinus, btnPlus, btnKeranjang, btnSyncAnggota;

    public PEMBELIANPAGE() {
        setTitle("Koperasi Sekolah");
        setSize(1200, 950);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0, 116, 217));

        // ==========================
        // LOGO DAN TITLE
        // ==========================
        ImageIcon iconLogo = new ImageIcon("src/Koperasi/assets/logosmk4 (1).png");
        Image scaledLogo = iconLogo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        logo.setBounds(520, 30, 100, 100);
        add(logo);

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

        // readonly semua kecuali Kode Anggota
        txtKodeTransaksi.setEditable(false);
        txtNamaLengkap.setEditable(false);
        txtKelas.setEditable(false);
        txtNoTelp.setEditable(false);
        comboStatusAnggota.setEnabled(false);

        // auto generate kode transaksi
        txtKodeTransaksi.setText(generateKodeTransaksi());

        JComponent[] componentsA = {
                txtKodeTransaksi, txtKodeAnggota, txtNamaLengkap,
                txtKelas, comboStatusAnggota, txtNoTelp
        };

        for (int i = 0; i < labelA.length; i++) {
            JLabel l = new JLabel(labelA[i]);
            l.setBounds(20, y, 150, 25);
            cardAnggota.add(l);

            if (i == 1) {
                // Kode Anggota + tombol Sync
                componentsA[i].setBounds(180, y, 120, 25);
                cardAnggota.add(componentsA[i]);

                btnSyncAnggota = new JButton("Sync");
                btnSyncAnggota.setBounds(310, y, 80, 25);
                btnSyncAnggota.setFocusPainted(false);
                btnSyncAnggota.setBackground(new Color(0, 102, 204));
                btnSyncAnggota.setForeground(Color.WHITE);
                btnSyncAnggota.addActionListener(e -> syncAnggota());
                cardAnggota.add(btnSyncAnggota);
            } else {
                componentsA[i].setBounds(180, y, 200, 25);
                cardAnggota.add(componentsA[i]);
            }

            y += 32;
        }

        // =====================================================
        // CARD : PILIH BARANG
        // =====================================================
        JPanel cardBarang = new JPanel(null);
        cardBarang.setBounds(755, 160, 425, 270);
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

        String[] labelB = {
                "Nama Barang",
                "Harga Satuan",
                "Kategori Barang",
                "Stok",
                "Jumlah",
                "Total Harga"
        };

        y = 60;

        comboNamaBarang = new JComboBox<>();
        comboNamaBarang.setMaximumRowCount(10);

        txtHargaSatuan = new JTextField();
        txtHargaSatuan.setEditable(false);

        txtKategoriBarang = new JTextField();
        txtKategoriBarang.setEditable(false);

        txtStok = new JTextField();
        txtStok.setEditable(false);

        txtJumlah = new JTextField("1");
        txtJumlah.setHorizontalAlignment(SwingConstants.CENTER);

        txtTotalHarga = new JTextField();
        txtTotalHarga.setEditable(false);

        for (int i = 0; i < labelB.length; i++) {
            JLabel l = new JLabel(labelB[i]);
            l.setBounds(20, y, 150, 25);
            cardBarang.add(l);

            if (i == 0) {
                comboNamaBarang.setBounds(180, y, 200, 25);
                cardBarang.add(comboNamaBarang);
            } else if (i == 4) {
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
                switch (i) {
                    case 1: field = txtHargaSatuan; break;
                    case 2: field = txtKategoriBarang; break;
                    case 3: field = txtStok; break;
                    case 5: field = txtTotalHarga; break;
                }
                field.setBounds(180, y, 200, 25);
                cardBarang.add(field);
            }
            y += 28;
        }

        // Tombol Tambah ke Keranjang
        btnKeranjang = new JButton("Tambah ke Keranjang");
        btnKeranjang.setBounds(125, 225, 175, 30);
        btnKeranjang.setBackground(new Color(0, 102, 204));
        btnKeranjang.setForeground(Color.WHITE);
        btnKeranjang.setFocusPainted(false);
        btnKeranjang.addActionListener(e -> tambahKeKeranjang());
        cardBarang.add(btnKeranjang);

        // Auto hitung total ketika jumlah diketik
        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
            }
        });

        // Ketika pilih barang, isi field lainnya
        comboNamaBarang.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                BarangItem item = (BarangItem) comboNamaBarang.getSelectedItem();
                if (item != null) {
                    txtHargaSatuan.setText(String.valueOf(item.getHargaJual()));
                    txtKategoriBarang.setText(item.getKategori());
                    txtStok.setText(String.valueOf(item.getStok()));

                    txtJumlah.setText("1");
                    hitungTotalHarga();

                    if (item.getStok() <= 0) {
                        btnKeranjang.setEnabled(false);
                        JOptionPane.showMessageDialog(
                                this,
                                "Stok barang \"" + item.getNamaBarang() + "\" habis.\nBarang tidak tersedia.",
                                "Stok Habis",
                                JOptionPane.WARNING_MESSAGE
                        );
                    } else {
                        btnKeranjang.setEnabled(true);
                    }
                }
            }
        });

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

        // ====== LOAD DATA BARANG DARI DATABASE ======
        loadBarangFromDatabase();

        setVisible(true);
    }

    // ==================== HELPER ====================

    private String generateKodeTransaksi() {
        Random random = new Random();
        int number = random.nextInt(90000) + 10000; // 10000 - 99999
        return "TRS-" + number;
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
        BarangItem item = (BarangItem) comboNamaBarang.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtHargaSatuan.getText().isEmpty() || txtStok.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data barang belum lengkap!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int stok = Integer.parseInt(txtStok.getText());
            int jumlah = Integer.parseInt(txtJumlah.getText());

            if (stok <= 0) {
                JOptionPane.showMessageDialog(this, "Stok barang habis. Tidak bisa menambah ke keranjang.", "Stok Habis", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jumlah > stok) {
                JOptionPane.showMessageDialog(this,
                        "Jumlah yang dibeli melebihi stok!\nStok tersedia: " + stok,
                        "Stok Tidak Cukup",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int harga = Integer.parseInt(txtHargaSatuan.getText());
            int total = harga * jumlah;

            modelKeranjang.addRow(new Object[]{item.getNamaBarang(), jumlah, harga, total});

            int sisaStok = stok - jumlah;
            txtStok.setText(String.valueOf(sisaStok));
            item.setStok(sisaStok);

            if (sisaStok <= 0) {
                btnKeranjang.setEnabled(false);
                JOptionPane.showMessageDialog(this,
                        "Stok barang \"" + item.getNamaBarang() + "\" sekarang habis.",
                        "Stok Habis",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            txtJumlah.setText("1");
            hitungTotalHarga();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah dan harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
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

        int totalBelanja = 0;
        for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
            totalBelanja += (int) modelKeranjang.getValueAt(i, 3);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String tanggal = sdf.format(new Date());

        modelRiwayat.addRow(new Object[]{
                txtKodeTransaksi.getText(),
                txtNamaLengkap.getText(),
                totalBelanja,
                tanggal
        });

        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!\nTotal: Rp " + totalBelanja, "Sukses", JOptionPane.INFORMATION_MESSAGE);

        resetForm();
    }

    private void resetForm() {
        // generate kode transaksi baru
        txtKodeTransaksi.setText(generateKodeTransaksi());

        txtKodeAnggota.setText("");
        txtNamaLengkap.setText("");
        txtKelas.setText("");
        comboStatusAnggota.setSelectedIndex(0);
        txtNoTelp.setText("");

        comboNamaBarang.setSelectedIndex(0);
        BarangItem item = (BarangItem) comboNamaBarang.getSelectedItem();
        if (item != null) {
            txtHargaSatuan.setText(String.valueOf(item.getHargaJual()));
            txtKategoriBarang.setText(item.getKategori());
            txtStok.setText(String.valueOf(item.getStok()));
        } else {
            txtHargaSatuan.setText("");
            txtKategoriBarang.setText("");
            txtStok.setText("");
        }

        txtJumlah.setText("1");
        hitungTotalHarga();

        modelKeranjang.setRowCount(0);

        btnKeranjang.setEnabled(true);

        JOptionPane.showMessageDialog(this, "Form berhasil direset!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===================== DB: LOAD DATA BARANG =====================
    private void loadBarangFromDatabase() {
        comboNamaBarang.removeAllItems();
        String sql = "SELECT kode_barang, nama_barang, kategori, harga_jual, stok FROM barang ORDER BY nama_barang";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BarangItem item = new BarangItem(
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getString("kategori"),
                        rs.getInt("harga_jual"),
                        rs.getInt("stok")
                );
                comboNamaBarang.addItem(item);
            }

            // pilih data pertama sebagai default
            if (comboNamaBarang.getItemCount() > 0) {
                comboNamaBarang.setSelectedIndex(0);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data barang dari database:\n" + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== DB: SYNC ANGGOTA =====================
    private void syncAnggota() {
        String kode = txtKodeAnggota.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan Kode Anggota terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT nama_anggota, kelas, no_telp, status_anggota FROM anggota WHERE kode_anggota = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtNamaLengkap.setText(rs.getString("nama_anggota"));
                    txtKelas.setText(rs.getString("kelas"));
                    txtNoTelp.setText(rs.getString("no_telp"));
                    String status = rs.getString("status_anggota");

                    if (status != null) {
                        comboStatusAnggota.setSelectedItem(status);
                    } else {
                        comboStatusAnggota.setSelectedIndex(0);
                    }

                    JOptionPane.showMessageDialog(this, "Data anggota berhasil disinkronkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Data anggota dengan kode " + kode + " tidak ditemukan!", "Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
                    txtNamaLengkap.setText("");
                    txtKelas.setText("");
                    txtNoTelp.setText("");
                    comboStatusAnggota.setSelectedIndex(0);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data anggota dari database:\n" + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Kelas helper untuk representasi barang di combo
    private static class BarangItem {
        private String kodeBarang;
        private String namaBarang;
        private String kategori;
        private int hargaJual;
        private int stok;

        public BarangItem(String kodeBarang, String namaBarang, String kategori, int hargaJual, int stok) {
            this.kodeBarang = kodeBarang;
            this.namaBarang = namaBarang;
            this.kategori = kategori;
            this.hargaJual = hargaJual;
            this.stok = stok;
        }

        public String getKodeBarang() {
            return kodeBarang;
        }

        public String getNamaBarang() {
            return namaBarang;
        }

        public String getKategori() {
            return kategori;
        }

        public int getHargaJual() {
            return hargaJual;
        }

        public int getStok() {
            return stok;
        }

        public void setStok(int stok) {
            this.stok = stok;
        }

        @Override
        public String toString() {
            return namaBarang;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PEMBELIANPAGE::new);
    }
}
