import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class LibraryManagementSystem extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/LibraryMSdb";
    private static final String user = "root";
    private static final String pass = "R@jan12#";
    private static String currentuser = null;
    
    private JTextField usernameField, passwordField, nameField, addressField, phoneField;
    private JTable booksTable, issuedBooksTable, studentsTable;
    private DefaultTableModel tableModel, issuedBooksTableModel, studentsTableModel;
    private JPanel mainPanel, loginPanel, menuPanel, addBookPanel, viewBooksPanel, addStudentPanel, issueBookPanel, issuedBooksPanel, memberDetailsPanel, returnBookPanel, viewStudentsPanel;
    private CardLayout cardLayout;

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        createLoginPanel();
        createMenuPanel();
        createAddBookPanel();
        createViewBooksPanel();
        createAddStudentPanel();
        createViewStudentsPanel();
        createIssueBookPanel();
        createIssuedBooksPanel();
        createMemberDetailsPanel();
        createReturnBookPanel();
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Login"));

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(signupButton);

        loginPanel.add(formPanel, gbc);

        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> showSignupDialog());

        mainPanel.add(loginPanel, "LOGIN");
    }

    private void createMenuPanel() {
        menuPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttonLabels = {
            "Add Books", "View Books",
            "Add Students", "View Students",
            "Issue Book", "View Issued Books",
            "Return Book", "Member Details",
            "Logout", ""
        };

        for (String label : buttonLabels) {
            if (!label.isEmpty()) {
                JButton button = new JButton(label);
                button.setFont(new Font("Arial", Font.PLAIN, 14));
                button.addActionListener(e -> handleMenuAction(label));
                buttonPanel.add(button);
            } else {
                buttonPanel.add(new JPanel()); // Empty panel for spacing
            }
        }

        menuPanel.add(new JLabel("Library Management System", SwingConstants.CENTER), BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(menuPanel, "MENU");
    }

    private void createAddBookPanel() {
        addBookPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField bookNameField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField totalQuantityField = new JTextField();
        JTextField availableQuantityField = new JTextField();

        formPanel.add(new JLabel("Book Name:"));
        formPanel.add(bookNameField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Total Quantity:"));
        formPanel.add(totalQuantityField);
        formPanel.add(new JLabel("Available Quantity:"));
        formPanel.add(availableQuantityField);

        JButton saveButton = new JButton("Save Book");
        JButton backButton = new JButton("Back to Menu");

        saveButton.addActionListener(e -> {
            try {
                addBook(
                    bookNameField.getText(),
                    authorField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(totalQuantityField.getText()),
                    Integer.parseInt(availableQuantityField.getText())
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and quantities");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel);
        addBookPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(addBookPanel, "ADD_BOOK");
    }

    private void createViewBooksPanel() {
        viewBooksPanel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Name", "Author", "Price", "Total Qty", "Available Qty", "Added By"};
        tableModel = new DefaultTableModel(columns, 0);
        booksTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Menu");
        
        refreshButton.addActionListener(e -> refreshBooksList());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        viewBooksPanel.add(scrollPane, BorderLayout.CENTER);
        viewBooksPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(viewBooksPanel, "VIEW_BOOKS");
    }

    private void createAddStudentPanel() {
        addStudentPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField studentNameField = new JTextField();
        JTextField phoneField = new JTextField();

        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(studentNameField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneField);

        JButton saveButton = new JButton("Save Student");
        JButton backButton = new JButton("Back to Menu");

        saveButton.addActionListener(e -> {
            String name = studentNameField.getText();
            String phone = phoneField.getText();
            if (!name.isEmpty() && !phone.isEmpty()) {
                addStudent(name, phone);
                studentNameField.setText("");
                phoneField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel);
        addStudentPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(addStudentPanel, "ADD_STUDENT");
    }

    private void createViewStudentsPanel() {
        viewStudentsPanel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Name", "Phone", "Books Issued"};
        studentsTableModel = new DefaultTableModel(columns, 0);
        studentsTable = new JTable(studentsTableModel);
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Menu");
        
        refreshButton.addActionListener(e -> refreshStudentsList());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        viewStudentsPanel.add(scrollPane, BorderLayout.CENTER);
        viewStudentsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(viewStudentsPanel, "VIEW_STUDENTS");
    }

    private void createIssueBookPanel() {
        issueBookPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField bookIdField = new JTextField();
        JTextField studentIdField = new JTextField();

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);
        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);

        JButton issueButton = new JButton("Issue Book");
        JButton backButton = new JButton("Back to Menu");

        issueButton.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText());
                int studentId = Integer.parseInt(studentIdField.getText());
                issueBook(bookId, studentId);
                bookIdField.setText("");
                studentIdField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid IDs!");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(issueButton);
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel);
        issueBookPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(issueBookPanel, "ISSUE_BOOK");
    }

    private void createIssuedBooksPanel() {
        issuedBooksPanel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Book", "Student", "Issued By", "Issue Date", "Return Date", "Penalty"};
        issuedBooksTableModel = new DefaultTableModel(columns, 0);
        issuedBooksTable = new JTable(issuedBooksTableModel);
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Menu");
        
        refreshButton.addActionListener(e -> refreshIssuedBooksList());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        issuedBooksPanel.add(scrollPane, BorderLayout.CENTER);
        issuedBooksPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(issuedBooksPanel, "VIEW_ISSUED_BOOKS");
    }

    private void createMemberDetailsPanel() {
        memberDetailsPanel = new JPanel(new BorderLayout());
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username: ");
        JLabel nameLabel = new JLabel("Name: ");
        JLabel phoneLabel = new JLabel("Phone: ");
        JLabel addressLabel = new JLabel("Address: ");
        JLabel joiningDateLabel = new JLabel("Joining Date: ");
        JLabel booksIssuedLabel = new JLabel("Books Issued: ");

        detailsPanel.add(new JLabel("Username:"));
        detailsPanel.add(usernameLabel);
        detailsPanel.add(new JLabel("Name:"));
        detailsPanel.add(nameLabel);
        detailsPanel.add(new JLabel("Phone:"));
        detailsPanel.add(phoneLabel);
        detailsPanel.add(new JLabel("Address:"));
        detailsPanel.add(addressLabel);
        detailsPanel.add(new JLabel("Joining Date:"));
        detailsPanel.add(joiningDateLabel);
        detailsPanel.add(new JLabel("Books Issued:"));
        detailsPanel.add(booksIssuedLabel);

        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Menu");

        refreshButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                String sql = "SELECT m.*, COUNT(i.Issue_Id) as books_issued FROM Members m " +
                        "LEFT JOIN issue i ON m.Username = i.Issued_By " +
                        "WHERE m.Username = ? GROUP BY m.Username";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, currentuser);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    usernameLabel.setText(rs.getString("Username"));
                    nameLabel.setText(rs.getString("MName"));
                    phoneLabel.setText(rs.getString("MPhone"));
                    addressLabel.setText(rs.getString("Address"));
                    joiningDateLabel.setText(rs.getDate("Joining_Date").toString());
                    booksIssuedLabel.setText(String.valueOf(rs.getInt("books_issued")));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error fetching member details: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        memberDetailsPanel.add(detailsPanel, BorderLayout.CENTER);
        memberDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(memberDetailsPanel, "MEMBER_DETAILS");
    }

    private void createReturnBookPanel() {
        returnBookPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField issueIdField = new JTextField();

        formPanel.add(new JLabel("Issue ID:"));
        formPanel.add(issueIdField);

        JButton returnButton = new JButton("Return Book");
        JButton backButton = new JButton("Back to Menu");

        returnButton.addActionListener(e -> {
            try {
                int issueId = Integer.parseInt(issueIdField.getText());
                returnBook(issueId);
                issueIdField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Issue ID!");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);
        buttonPanel.add(backButton);

        formPanel.add(buttonPanel);
        returnBookPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(returnBookPanel, "RETURN_BOOK");
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT * FROM members WHERE Username=? AND Password=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentuser = username;
                cardLayout.show(mainPanel, "MENU");
                JOptionPane.showMessageDialog(this, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void showSignupDialog() {
        JDialog signupDialog = new JDialog(this, "Sign Up", true);
        signupDialog.setLayout(new GridLayout(6, 2, 5, 5));
        
        nameField = new JTextField();
        addressField = new JTextField();
        phoneField = new JTextField();
        JTextField newUsernameField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        
        signupDialog.add(new JLabel("Name:"));
        signupDialog.add(nameField);
        signupDialog.add(new JLabel("Address:"));
        signupDialog.add(addressField);
        signupDialog.add(new JLabel("Phone:"));
        signupDialog.add(phoneField);
        signupDialog.add(new JLabel("Username:"));
        signupDialog.add(newUsernameField);
        signupDialog.add(new JLabel("Password:"));
        signupDialog.add(newPasswordField);
        
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            signup(
                newUsernameField.getText(),
                nameField.getText(),
                new String(newPasswordField.getPassword()),
                phoneField.getText(),
                addressField.getText()
            );
            signupDialog.dispose();
        });
        
        signupDialog.add(submitButton);
        signupDialog.pack();
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setVisible(true);
    }

    private void signup(String username, String name, String password, String phone, String address) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "INSERT INTO members(Username, MName, Password, MPhone, Address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sign up successful! Please login.");
        } catch (SQLException e) {
            if (e.getMessage().contains("phone_number")) {
                JOptionPane.showMessageDialog(this, "Invalid phone number. Please enter 10 digits only.");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void addBook(String name, String author, double price, int totalQty, int availableQty) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "INSERT INTO Books (Book_Name, Author, Price, Total_Quantity, Available_Quantity, Added_By) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, author);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, totalQty);
            pstmt.setInt(5, availableQty);
            pstmt.setString(6, currentuser);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage());
        }
    }

    private void refreshBooksList() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT * FROM Books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("Book_Id"),
                    rs.getString("Book_Name"),
                    rs.getString("Author"),
                    rs.getDouble("Price"),
                    rs.getInt("Total_Quantity"),
                    rs.getInt("Available_Quantity"),
                    rs.getString("Added_By")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing books list: " + e.getMessage());
        }
    }

    private void addStudent(String name, String phone) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "INSERT INTO Students (Stu_Name, Stu_Phone) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
        } catch (SQLException e) {
            if (e.getMessage().contains("phone_number")) {
                JOptionPane.showMessageDialog(this, "Invalid phone number. Please enter 10 digits only.");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage());
            }
        }
    }

    private void refreshStudentsList() {
        studentsTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT s.*, COUNT(i.Issue_Id) as books_issued " +
                        "FROM Students s " +
                        "LEFT JOIN issue i ON s.Stu_Id = i.Stu_Id AND i.Returned_Date IS NULL " +
                        "GROUP BY s.Stu_Id, s.Stu_Name, s.Stu_Phone";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("Stu_Id"),
                    rs.getString("Stu_Name"),
                    rs.getString("Stu_Phone"),
                    rs.getInt("books_issued")
                };
                studentsTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing students list: " + e.getMessage());
        }
    }

    private void issueBook(int bookId, int studentId) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String checksql = "SELECT available_quantity FROM books WHERE Book_Id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checksql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("Available_Quantity") > 0) {
                String issueSql = "INSERT INTO issue(Book_Id, Stu_Id, Issued_By) VALUES(?, ?, ?)";
                PreparedStatement issueStmt = conn.prepareStatement(issueSql);
                issueStmt.setInt(1, bookId);
                issueStmt.setInt(2, studentId);
                issueStmt.setString(3, currentuser);
                issueStmt.executeUpdate();

                String updateSql = "UPDATE books SET Available_Quantity = Available_Quantity-1 WHERE Book_Id=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book issued successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Sorry, book not available.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error issuing book: " + e.getMessage());
        }
    }

    private void refreshIssuedBooksList() {
        issuedBooksTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT i.*, b.Book_Name, s.Stu_Name FROM issue i " +
                    "JOIN books b ON i.Book_Id = b.Book_Id " +
                    "JOIN students s ON i.Stu_Id = s.Stu_Id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("Issue_Id"),
                    rs.getString("Book_Name"),
                    rs.getString("Stu_Name"),
                    rs.getString("Issued_By"),
                    rs.getDate("Issued_Date"),
                    rs.getDate("Returned_Date"),
                    rs.getInt("Penalty")
                };
                issuedBooksTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing issued books list: " + e.getMessage());
        }
    }

    private void returnBook(int issueId) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String checksql = "SELECT Book_Id, Issued_Date FROM issue WHERE Issue_Id = ? AND Returned_Date IS NULL";
            PreparedStatement checkStmt = conn.prepareStatement(checksql);
            checkStmt.setInt(1, issueId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("Book_Id");
                Date issueDate = rs.getDate("Issued_Date");
                LocalDate returnDate = LocalDate.now();

                long daysKept = returnDate.toEpochDay() - issueDate.toLocalDate().toEpochDay();
                double fine = daysKept > 29 ? (daysKept - 29) * 10.0 : 0.0;

                String updateIssueSql = "UPDATE issue SET Returned_Date = ?, Penalty = ? WHERE Issue_Id = ?";
                PreparedStatement updateIssueStmt = conn.prepareStatement(updateIssueSql);
                updateIssueStmt.setDate(1, Date.valueOf(returnDate));
                updateIssueStmt.setDouble(2, fine);
                updateIssueStmt.setInt(3, issueId);
                updateIssueStmt.executeUpdate();

                String updateBookSql = "UPDATE Books SET Available_Quantity = Available_Quantity + 1 WHERE Book_Id = ?";
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                String message = "Book returned successfully!";
                if (fine > 0) {
                    message += String.format("\nFine charged: Rs. %.2f", fine);
                }
                JOptionPane.showMessageDialog(this, message);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid issue ID or book already returned.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage());
        }
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "Add Books":
                cardLayout.show(mainPanel, "ADD_BOOK");
                break;
            case "View Books":
                refreshBooksList();
                cardLayout.show(mainPanel, "VIEW_BOOKS");
                break;
            case "Add Students":
                cardLayout.show(mainPanel, "ADD_STUDENT");
                break;
            case "View Students":
                refreshStudentsList();
                cardLayout.show(mainPanel, "VIEW_STUDENTS");
                break;
            case "Issue Book":
                cardLayout.show(mainPanel, "ISSUE_BOOK");
                break;
            case "View Issued Books":
                refreshIssuedBooksList();
                cardLayout.show(mainPanel, "VIEW_ISSUED_BOOKS");
                break;
            case "Member Details":
                cardLayout.show(mainPanel, "MEMBER_DETAILS");
                break;
            case "Return Book":
                cardLayout.show(mainPanel, "RETURN_BOOK");
                break;
            case "Logout":
                currentuser = null;
                cardLayout.show(mainPanel, "LOGIN");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid option");
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            SwingUtilities.invokeLater(() -> {
                new LibraryManagementSystem().setVisible(true);
            });
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
}
