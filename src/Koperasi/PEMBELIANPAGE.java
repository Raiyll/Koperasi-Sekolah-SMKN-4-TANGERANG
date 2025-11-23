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
        setSize(1400, 900);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0, 116, 217));

        // ==========================
        // LOGO DAN TITLE
        // ==========================
        ImageIcon iconLogo = new ImageIcon("src/Koperasi/assets/logosmk4 (1).png");
        Image scaledLogo = iconLogo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);

        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        logo.setBounds(50, 20, 80, 80);
        add(logo);

        JPanel titlePanel = new JPanel(null);
        titlePanel.setBounds(140, 20, 300, 80);
        titlePanel.setBackground(new Color(0, 116, 217));
        add(titlePanel);

        JLabel title1 = new JLabel("KOPERASI SEKOLAH");
        title1.setFont(new Font("SansSerif", Font.BOLD, 22));
        title1.setForeground(Color.WHITE);
        title1.setBounds(0, 15, 300, 25);
        titlePanel.add(title1);

        JLabel title2 = new JLabel("SMKN 4 TANGERANG");
        title2.setFont(new Font("SansSerif", Font.BOLD, 20));
        title2.setForeground(Color.WHITE);
        title2.setBounds(0, 45, 300, 25);
        titlePanel.add(title2);

        // Tombol Back ke Menu
        JButton btnBack = new JButton("← Kembali");
        btnBack.setBounds(1240, 30, 120, 40);
        btnBack.setBackground(new Color(220, 53, 69));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            MENUPAGE menuPage = new MENUPAGE();
            menuPage.setVisible(true);
            dispose();
        });
        add(btnBack);

        // =====================================================
        // CARD : DATA ANGGOTA (Kiri Atas)
        // =====================================================
        JPanel cardAnggota = new JPanel(null);
        cardAnggota.setBounds(30, 120, 450, 270);
        cardAnggota.setBackground(Color.WHITE);
        cardAnggota.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardAnggota);

        JPanel headerA = new JPanel(null);
        headerA.setBounds(0, 0, 450, 40);
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

        txtKodeTransaksi.setEditable(false);
        txtNamaLengkap.setEditable(false);
        txtKelas.setEditable(false);
        txtNoTelp.setEditable(false);
        comboStatusAnggota.setEnabled(false);

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
                componentsA[i].setBounds(180, y, 140, 25);
                cardAnggota.add(componentsA[i]);

                btnSyncAnggota = new JButton("Cari");
                btnSyncAnggota.setBounds(330, y, 80, 25);
                btnSyncAnggota.setFocusPainted(false);
                btnSyncAnggota.setBackground(new Color(0, 102, 204));
                btnSyncAnggota.setForeground(Color.WHITE);
                btnSyncAnggota.addActionListener(e -> syncAnggota());
                cardAnggota.add(btnSyncAnggota);
            } else {
                componentsA[i].setBounds(180, y, 230, 25);
                cardAnggota.add(componentsA[i]);
            }

            y += 32;
        }

        // =====================================================
        // CARD : PILIH BARANG (Kanan Atas)
        // =====================================================
        JPanel cardBarang = new JPanel(null);
        cardBarang.setBounds(500, 120, 450, 270);
        cardBarang.setBackground(Color.WHITE);
        cardBarang.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardBarang);

        JPanel headerB = new JPanel(null);
        headerB.setBounds(0, 0, 450, 40);
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
                comboNamaBarang.setBounds(180, y, 230, 25);
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
                field.setBounds(180, y, 230, 25);
                cardBarang.add(field);
            }
            y += 28;
        }

        btnKeranjang = new JButton("Tambah ke Keranjang");
        btnKeranjang.setBounds(137, 225, 175, 30);
        btnKeranjang.setBackground(new Color(0, 102, 204));
        btnKeranjang.setForeground(Color.WHITE);
        btnKeranjang.setFocusPainted(false);
        btnKeranjang.addActionListener(e -> tambahKeKeranjang());
        cardBarang.add(btnKeranjang);

        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
            }
        });

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
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                if (comboNamaBarang.getSelectedIndex() == -1) {
                    clearBarangFields();
                }
            }
        });

        // =====================================================
        // CARD : KERANJANG BELANJA (Tengah)
        // =====================================================
        JPanel cardCart = new JPanel(null);
        cardCart.setBounds(970, 120, 400, 270);
        cardCart.setBackground(Color.WHITE);
        cardCart.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardCart);

        JPanel headerC = new JPanel(null);
        headerC.setBounds(0, 0, 400, 40);
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
        scrollCart.setBounds(15, 60, 370, 130);
        scrollCart.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCart.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardCart.add(scrollCart);

        JButton btnReset = new JButton("Reset Form");
        btnReset.setBounds(15, 205, 110, 30);
        btnReset.setBackground(new Color(220, 100, 20));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> resetForm());
        cardCart.add(btnReset);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(145, 205, 110, 30);
        btnSimpan.setBackground(new Color(34, 139, 34));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(e -> simpanTransaksi());
        cardCart.add(btnSimpan);

        JButton btnHapus = new JButton("Hapus Item");
        btnHapus.setBounds(275, 205, 110, 30);
        btnHapus.setBackground(new Color(220, 53, 69));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusItem());
        cardCart.add(btnHapus);

        // =====================================================
        // CARD : RIWAYAT TRANSAKSI (Bawah - LEBIH LEBAR)
        // =====================================================
        JPanel cardHistory = new JPanel(null);
        cardHistory.setBounds(30, 410, 1340, 430);
        cardHistory.setBackground(Color.WHITE);
        cardHistory.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(cardHistory);

        JPanel headerD = new JPanel(null);
        headerD.setBounds(0, 0, 1340, 45);
        headerD.setBackground(new Color(220, 100, 20));
        cardHistory.add(headerD);

        JLabel lblD = new JLabel("Riwayat Transaksi");
        lblD.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblD.setForeground(Color.WHITE);
        lblD.setBounds(20, 5, 300, 35);
        headerD.add(lblD);

        modelRiwayat = new DefaultTableModel(
                new Object[]{"Kode Transaksi", "Nama Anggota", "Total Belanja", "Tanggal"}, 0
        );
        tableRiwayat = new JTable(modelRiwayat);
        tableRiwayat.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableRiwayat.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tableRiwayat.setRowHeight(25);

        JScrollPane scrollHistory = new JScrollPane(tableRiwayat);
        scrollHistory.setBounds(20, 65, 1300, 345);
        scrollHistory.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollHistory.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardHistory.add(scrollHistory);

        // ====== LOAD DATA BARANG DARI DATABASE ======
        loadBarangFromDatabase();
        comboNamaBarang.setSelectedIndex(-1);
        clearBarangFields();
        btnKeranjang.setEnabled(true);

        setVisible(true);
    }

    // ==================== HELPER ====================

    private String generateKodeTransaksi() {
        Random random = new Random();
        int number = random.nextInt(90000) + 10000;
        return "TRS-" + number;
    }

    private void clearBarangFields() {
        txtHargaSatuan.setText("");
        txtKategoriBarang.setText("");
        txtStok.setText("");
        txtJumlah.setText("1");
        txtTotalHarga.setText("");
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

            String namaBarang = item.getNamaBarang();
            int existingRowIndex = -1;

            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                String namaDiTabel = (String) modelKeranjang.getValueAt(i, 0);
                if (namaDiTabel.equals(namaBarang)) {
                    existingRowIndex = i;
                    break;
                }
            }

            if (existingRowIndex != -1) {
                int existingQty = (int) modelKeranjang.getValueAt(existingRowIndex, 1);
                int newQty = existingQty + jumlah;
                int newTotal = harga * newQty;

                modelKeranjang.setValueAt(newQty, existingRowIndex, 1);
                modelKeranjang.setValueAt(newTotal, existingRowIndex, 3);
            } else {
                int total = harga * jumlah;
                modelKeranjang.addRow(new Object[]{namaBarang, jumlah, harga, total});
            }

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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tanggal = sdf.format(new Date());

        // Simpan ke database
        String sqlTransaksi = "INSERT INTO transaksi (kode_transaksi, tanggal_transaksi, kode_anggota, total_harga, bayar, kembalian, status_transaksi, keterangan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlUpdateStok = "UPDATE barang SET stok = stok - ? WHERE nama_barang = ?";

        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false); // mulai transaksi

            try {
                // 1. Insert data transaksi utama
                try (PreparedStatement psTransaksi = conn.prepareStatement(sqlTransaksi)) {
                    psTransaksi.setString(1, txtKodeTransaksi.getText());
                    psTransaksi.setString(2, tanggal);
                    psTransaksi.setString(3, txtKodeAnggota.getText());
                    psTransaksi.setDouble(4, totalBelanja);
                    psTransaksi.setDouble(5, totalBelanja); // bayar = total
                    psTransaksi.setDouble(6, 0); // kembalian = 0
                    psTransaksi.setString(7, "Lunas");

                    // Buat keterangan detail barang
                    StringBuilder keterangan = new StringBuilder("Pembelian: ");
                    for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                        String namaBarang = (String) modelKeranjang.getValueAt(i, 0);
                        int jumlah = (int) modelKeranjang.getValueAt(i, 1);
                        keterangan.append(namaBarang).append(" (").append(jumlah).append("x)");
                        if (i < modelKeranjang.getRowCount() - 1) {
                            keterangan.append(", ");
                        }
                    }
                    psTransaksi.setString(8, keterangan.toString());
                    psTransaksi.executeUpdate();
                }

                // 2. Update stok barang
                try (PreparedStatement psStok = conn.prepareStatement(sqlUpdateStok)) {
                    for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                        String namaBarang = (String) modelKeranjang.getValueAt(i, 0);
                        int jumlah = (int) modelKeranjang.getValueAt(i, 1);

                        // Update stok barang
                        psStok.setInt(1, jumlah);
                        psStok.setString(2, namaBarang);
                        psStok.executeUpdate();
                    }
                }

                conn.commit(); // commit semua perubahan

                // Tampilkan di riwayat
                SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                modelRiwayat.addRow(new Object[]{
                        txtKodeTransaksi.getText(),
                        txtNamaLengkap.getText(),
                        "Rp " + String.format("%,d", totalBelanja),
                        sdfDisplay.format(new Date())
                });

                JOptionPane.showMessageDialog(this,
                        "Transaksi berhasil disimpan!\nTotal: Rp " + String.format("%,d", totalBelanja),
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                resetForm();

            } catch (SQLException e) {
                conn.rollback(); // batalkan semua perubahan jika ada error
                throw e;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan transaksi ke database:\n" + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void resetForm() {
        txtKodeTransaksi.setText(generateKodeTransaksi());

        txtKodeAnggota.setText("");
        txtNamaLengkap.setText("");
        txtKelas.setText("");
        comboStatusAnggota.setSelectedIndex(0);
        txtNoTelp.setText("");

        comboNamaBarang.setSelectedIndex(-1);
        clearBarangFields();

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

            comboNamaBarang.setSelectedIndex(-1);

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