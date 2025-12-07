package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.Customer;
import service.BankService;

public class CustomerForm extends JFrame {

    private BankService service;

    // Components
    private JTextField txtFirstName, txtLastName, txtPhone, txtEmail, txtAddress;
    private JButton btnSave, btnViewAll;

    public CustomerForm() {
        setupConnection();
        initComponents();
    }

    private void setupConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Server connection failed: " + e.getMessage());
        }
    }

    private void initComponents() {
        setTitle("Customer Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Use BorderLayout: Form in Center, Buttons at Bottom
        setLayout(new BorderLayout());

        // --- CENTER: FORM FIELDS ---
        JPanel formPanel = new JPanel();
        // GridLayout: Any number of rows, 2 columns, 10px horz gap, 10px vert gap
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        // Add padding (Top, Left, Bottom, Right) so it doesn't touch edges
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtFirstName = new JTextField(15); // 15 columns width hint
        txtLastName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(txtFirstName);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(txtLastName);

        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(txtPhone);

        formPanel.add(new JLabel("Email Address:"));
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Physical Address:"));
        formPanel.add(txtAddress);

        add(formPanel, BorderLayout.CENTER);

        // --- BOTTOM: BUTTONS ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center buttons with gap

        btnSave = new JButton("Save Customer");
        btnSave.setPreferredSize(new Dimension(130, 35)); // Make button slightly bigger

        btnViewAll = new JButton("View List");
        btnViewAll.setPreferredSize(new Dimension(130, 35));

        // Actions
        btnSave.addActionListener(e -> saveCustomer());
        btnViewAll.addActionListener(e -> openManager());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnViewAll);

        add(buttonPanel, BorderLayout.SOUTH);

        // "Pack" shrinks the window to fit the content perfectly
        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    private void saveCustomer() {
        try {
            Customer c = new Customer();
            c.setFirstName(txtFirstName.getText());
            c.setLastName(txtLastName.getText());
            c.setPhoneNumber(txtPhone.getText());
            c.setEmail(txtEmail.getText());
            c.setAddress(txtAddress.getText());

            service.createCustomer(c);

            JOptionPane.showMessageDialog(this, "✅ Customer Saved Successfully!");
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void openManager() {
        // Opens the table view we created earlier
        new CustomerManagerForm().setVisible(true);
    }

    private void clearFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
    }

    public static void main(String[] args) {
        // Use "Nimbus" look and feel for a modern, non-Java look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> new CustomerForm().setVisible(true));
    }
}