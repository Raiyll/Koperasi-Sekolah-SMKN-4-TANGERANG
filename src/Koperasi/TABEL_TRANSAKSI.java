package Koperasi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;

public class TABEL_TRANSAKSI extends javax.swing.JFrame {
    private Connection conn;
    private DefaultTableModel model;

    public TABEL_TRANSAKSI() {
        initComponents();
        getContentPane().setBackground(java.awt.Color.decode("#0078D7"));

        // Initialize table model
        model = (DefaultTableModel) jTable1.getModel();

        // Load data dari database saat form dibuka
        loadDataFromDatabase();

        // Set table menjadi non-editable
        jTable1.setDefaultEditor(Object.class, null);

        // Tambahkan listener untuk klik row di table
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = jTable1.getSelectedRow();
                if (row != -1) {
                    populateFieldsFromTable(row);
                }
            }
        });

        // DISABLE tombol Save karena input transaksi dari PEMBELIANPAGE
        jButton3.setEnabled(false);
        jButton3.setToolTipText("Gunakan menu 'Beli Barang' untuk transaksi baru");
        jButton3.setBackground(new java.awt.Color(180, 180, 180));

        // Set warna tombol
        jButton1.setBackground(new java.awt.Color(220, 53, 69)); // Delete - Merah
        jButton1.setForeground(java.awt.Color.WHITE);
        jButton2.setBackground(new java.awt.Color(255, 193, 7)); // Edit - Kuning
        jButton2.setForeground(java.awt.Color.BLACK);
        jButton4.setBackground(new java.awt.Color(108, 117, 125)); // Clear - Abu
        jButton4.setForeground(java.awt.Color.WHITE);

        // Tambah label info
        jLabel3.setText("LIHAT & EDIT TRANSAKSI");
    }

    // Method untuk load data dari database
    private void loadDataFromDatabase() {
        model.setRowCount(0); // Clear table

        String sql = "SELECT kode_transaksi, tanggal_transaksi, kode_anggota, total_harga, " +
                "bayar, kembalian, status_transaksi, keterangan FROM transaksi ORDER BY tanggal_transaksi DESC";

        try (Connection conn = ConnectionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("kode_transaksi");

                // Format tanggal
                try {
                    java.sql.Timestamp timestamp = rs.getTimestamp("tanggal_transaksi");
                    row[1] = sdf.format(timestamp);
                } catch (Exception e) {
                    row[1] = rs.getString("tanggal_transaksi");
                }

                row[2] = rs.getString("kode_anggota");
                row[3] = "Rp " + String.format("%,d", rs.getInt("total_harga"));
                row[4] = "Rp " + String.format("%,d", rs.getInt("bayar"));
                row[5] = "Rp " + String.format("%,d", rs.getInt("kembalian"));
                row[6] = rs.getString("status_transaksi");
                row[7] = rs.getString("keterangan");

                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data transaksi:\n" + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Method untuk mengisi field dari row yang dipilih
    private void populateFieldsFromTable(int row) {
        try {
            txtkodetransaksi.setText(model.getValueAt(row, 0).toString());
            txttanggaltransaksi.setText(model.getValueAt(row, 1).toString());
            txtkodeanggota.setText(model.getValueAt(row, 2).toString());

            // Remove "Rp " dan koma untuk parsing
            String totalHarga = model.getValueAt(row, 3).toString().replace("Rp ", "").replace(",", "").trim();
            String bayar = model.getValueAt(row, 4).toString().replace("Rp ", "").replace(",", "").trim();
            String kembalian = model.getValueAt(row, 5).toString().replace("Rp ", "").replace(",", "").trim();

            jTextField4.setText(totalHarga);
            jTextField5.setText(bayar);
            jTextField6.setText(kembalian);

            jComboBox1.setSelectedItem(model.getValueAt(row, 6).toString());
            jTextField8.setText(model.getValueAt(row, 7).toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat mengisi field:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk clear/reset form
    private void clearForm() {
        txtkodetransaksi.setText("");
        txttanggaltransaksi.setText("");
        txtkodeanggota.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jComboBox1.setSelectedIndex(0);
        jTextField8.setText("");
        jTable1.clearSelection();
    }

    // Method untuk save/insert data
    private void saveData() {
        // Validasi input
        if (txtkodetransaksi.getText().trim().isEmpty() ||
                txttanggaltransaksi.getText().trim().isEmpty() ||
                txtkodeanggota.getText().trim().isEmpty() ||
                jTextField4.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Mohon lengkapi data transaksi!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO transaksi (kode_transaksi, tanggal_transaksi, kode_anggota, " +
                "total_harga, bayar, kembalian, status_transaksi, keterangan) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txtkodetransaksi.getText().trim());
            ps.setString(2, txttanggaltransaksi.getText().trim());
            ps.setString(3, txtkodeanggota.getText().trim());
            ps.setInt(4, Integer.parseInt(jTextField4.getText().trim()));
            ps.setInt(5, Integer.parseInt(jTextField5.getText().trim()));
            ps.setInt(6, Integer.parseInt(jTextField6.getText().trim()));
            ps.setString(7, jComboBox1.getSelectedItem().toString());
            ps.setString(8, jTextField8.getText().trim());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Data transaksi berhasil disimpan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

            loadDataFromDatabase();
            clearForm();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan data:\n" + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Total Harga, Bayar, dan Kembalian harus berupa angka!",
                    "Error Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk update data
    private void updateData() {
        if (txtkodetransaksi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data yang akan diupdate!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE transaksi SET tanggal_transaksi=?, kode_anggota=?, total_harga=?, " +
                "bayar=?, kembalian=?, status_transaksi=?, keterangan=? WHERE kode_transaksi=?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txttanggaltransaksi.getText().trim());
            ps.setString(2, txtkodeanggota.getText().trim());
            ps.setInt(3, Integer.parseInt(jTextField4.getText().trim()));
            ps.setInt(4, Integer.parseInt(jTextField5.getText().trim()));
            ps.setInt(5, Integer.parseInt(jTextField6.getText().trim()));
            ps.setString(6, jComboBox1.getSelectedItem().toString());
            ps.setString(7, jTextField8.getText().trim());
            ps.setString(8, txtkodetransaksi.getText().trim());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                        "Data transaksi berhasil diupdate!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                loadDataFromDatabase();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Data tidak ditemukan!",
                        "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengupdate data:\n" + e.getMessage(),
                    "Error Database",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Total Harga, Bayar, dan Kembalian harus berupa angka!",
                    "Error Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method untuk delete data
    private void deleteData() {
        if (txtkodetransaksi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data yang akan dihapus!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus transaksi ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM transaksi WHERE kode_transaksi=?";

            try (Connection conn = ConnectionDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, txtkodetransaksi.getText().trim());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Data transaksi berhasil dihapus!",
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadDataFromDatabase();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Data tidak ditemukan!",
                            "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data:\n" + e.getMessage(),
                        "Error Database",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtkodeanggota = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField8 = new javax.swing.JTextField();
        txtkodetransaksi = new javax.swing.JTextField();
        txttanggaltransaksi = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("DATA TRANSAKSI");

        jPanel2.setBackground(new java.awt.Color(221, 106, 53));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("LIHAT & EDIT TRANSAKSI");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Kode Transaksi");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Tanggal Transaksi");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Kode Anggota");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Total Harga");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Bayar");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Kembalian");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Status Transaksi");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Keterangan");

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lunas", "Belum Lunas" }));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Koperasi/assets/860829 (1).png")));
        jButton1.setText("Delete");
        jButton1.setBackground(new java.awt.Color(220, 53, 69));
        jButton1.setForeground(java.awt.Color.WHITE);
        jButton1.setToolTipText("Hapus transaksi yang dipilih");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Koperasi/assets/edit-246 (1).png")));
        jButton2.setText("Edit");
        jButton2.setBackground(new java.awt.Color(255, 193, 7));
        jButton2.setForeground(java.awt.Color.BLACK);
        jButton2.setToolTipText("Update data transaksi yang dipilih");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Koperasi/assets/save-44 (1).png")));
        jButton3.setText("Refresh");
        jButton3.setBackground(new java.awt.Color(40, 167, 69));
        jButton3.setForeground(java.awt.Color.WHITE);
        jButton3.setToolTipText("Muat ulang data dari database");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Koperasi/assets/clear-icon (1).png")));
        jButton4.setText("Clear");
        jButton4.setBackground(new java.awt.Color(108, 117, 125));
        jButton4.setForeground(java.awt.Color.WHITE);
        jButton4.setToolTipText("Bersihkan form input");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(204, 204, 204));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Koperasi/assets/arrow-go-back-icon-lg (1).png")));
        jButton5.setText("Kembali");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton4))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(txtkodeanggota)
                                                                .addComponent(jTextField4)
                                                                .addComponent(jTextField5)
                                                                .addComponent(jTextField6)
                                                                .addComponent(jTextField8)
                                                                .addComponent(jComboBox1, 0, 151, Short.MAX_VALUE)))
                                                .addComponent(jLabel3)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(jLabel4)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(txtkodetransaksi))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(jLabel5)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(txttanggaltransaksi)))
                                        .addComponent(jButton5))
                                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(txtkodetransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(txttanggaltransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(txtkodeanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel9)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel10)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel11)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel12))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2)
                                        .addComponent(jButton3)
                                        .addComponent(jButton4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SMKN 4 TANGERANG");

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Koperasi/assets/logosmk4 (1).png")));
        jLabel8.setText("jLabel8");

        jPanel1.setBackground(new java.awt.Color(221, 106, 53));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {
                        "Kode Transaksi", "Tanggal Transaksi", "Kode Anggota", "Total Harga", "Bayar", "Kembalian", "Status Transaksi", "Keterangan"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setShowGrid(true);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 831, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(jScrollPane1)
                                .addGap(31, 31, 31))
        );

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 28));
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("INPUT DATA TRANSAKSI KOPERASI");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(105, 105, 105))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addComponent(jLabel2)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .addComponent(jLabel1)))
                                .addGap(64, 64, 64)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(61, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addContainerGap(537, Short.MAX_VALUE)
                                        .addComponent(jLabel13)
                                        .addGap(425, 425, 425)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(41, 41, 41)
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                                .addComponent(jLabel2)
                                                .addGap(18, 18, 18))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel1)
                                                .addGap(47, 47, 47)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(71, 71, 71))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(75, 75, 75)
                                        .addComponent(jLabel13)
                                        .addContainerGap(658, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        new MENUPAGE().setVisible(true);
        dispose();
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        // Refresh data dari database
        loadDataFromDatabase();
        clearForm();
        JOptionPane.showMessageDialog(this,
                "Data berhasil dimuat ulang!",
                "Refresh",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        updateData();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        deleteData();
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        clearForm();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TABEL_TRANSAKSI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TABEL_TRANSAKSI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TABEL_TRANSAKSI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TABEL_TRANSAKSI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TABEL_TRANSAKSI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField txtkodeanggota;
    private javax.swing.JTextField txtkodetransaksi;
    private javax.swing.JTextField txttanggaltransaksi;
    // End of variables declaration
}