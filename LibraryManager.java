/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cw.librarymanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LibraryManager extends JFrame {
    private JTextField txtBookID, txtTitle, txtAuthor, txtYear;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public LibraryManager() {
        setTitle("Library Manager");
        setLayout(new BorderLayout());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("Book ID:"));
        txtBookID = new JTextField();
        formPanel.add(txtBookID);

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Year:"));
        txtYear = new JTextField();
        formPanel.add(txtYear);

        JButton btnAdd = new JButton("Add Book");
        formPanel.add(btnAdd);
        JButton btnDelete = new JButton("Delete Book");
        formPanel.add(btnDelete);

        add(formPanel, BorderLayout.NORTH);

        // Table panel
        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Refresh button
        JButton btnRefresh = new JButton("Refresh");
        add(btnRefresh, BorderLayout.SOUTH);

        // Event listeners
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBooks();
            }
        });

        viewBooks(); // Initial load
    }

    private Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:ucanaccess://C:/Users/ADMIN/Desktop/Library.accdb";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void addBook() {
        String bookID = txtBookID.getText();
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String year = txtYear.getText();

        String sql = "INSERT INTO Books (BookID, Title, Author, Year) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookID);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, year);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        viewBooks();
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        String bookID = tableModel.getValueAt(selectedRow, 0).toString();

        String sql = "DELETE FROM Books WHERE BookID = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookID);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        viewBooks();
    }

    private void viewBooks() {
        tableModel.setRowCount(0);

        String sql = "SELECT * FROM Books";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String bookID = rs.getString("BookID");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String year = rs.getString("Year");
                tableModel.addRow(new Object[]{bookID, title, author, year});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibraryManager().setVisible(true);
            }
        });
    }
}
