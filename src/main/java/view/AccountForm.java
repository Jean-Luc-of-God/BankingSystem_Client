package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.Account;
import model.EAccountType;
import model.Customer;
import service.BankService;

public class AccountForm extends JFrame {

    private BankService service;
    private Customer selectedCustomer = null;

    // Components
    private JTextField txtCustId, txtAccountId, txtBalance;
    private JLabel lblCustomerName;
    private JComboBox<String> cmbType;
    private JButton btnSearchCust, btnSave, btnViewAll;

    public AccountForm() {
        setupConnection();
        initComponents();
        populateAccountTypes();
    }

    private void setupConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + e.getMessage());
        }
    }

    private void initComponents() {
        setTitle("Create New Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600); // 1. FIXED STARTUP SIZE (No longer tiny)
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());

        // --- MAIN WRAPPER (Prevents stretching) ---
        // This panel holds everything in the center but won't let inputs grow huge
        JPanel mainWrapper = new JPanel(new GridBagLayout());

        // --- FORM CONTENT ---
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // 15px gaps
        contentPanel.setBorder(new TitledBorder("Account Details"));
        contentPanel.setPreferredSize(new Dimension(500, 400)); // 2. FIXED CONTENT SIZE

        // Fields (Set specific columns so they have width)
        txtCustId = new JTextField(15);
        btnSearchCust = new JButton("Find Owner");
        btnSearchCust.addActionListener(e -> searchCustomer());

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(txtCustId, BorderLayout.CENTER);
        searchPanel.add(btnSearchCust, BorderLayout.EAST);

        lblCustomerName = new JLabel("Owner: (None)");
        lblCustomerName.setForeground(new Color(0, 100, 0));
        lblCustomerName.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtAccountId = new JTextField();
        txtBalance = new JTextField();
        cmbType = new JComboBox<>();

        // Add to Content Panel
        contentPanel.add(new JLabel("Customer ID:"));
        contentPanel.add(searchPanel);

        contentPanel.add(new JLabel("")); // Spacer
        contentPanel.add(lblCustomerName);

        contentPanel.add(new JLabel("New Account No:"));
        contentPanel.add(txtAccountId);

        contentPanel.add(new JLabel("Account Type:"));
        contentPanel.add(cmbType);

        contentPanel.add(new JLabel("Initial Balance:"));
        contentPanel.add(txtBalance);

        // Add Content Panel to the Wrapper (Centered)
        mainWrapper.add(contentPanel);
        add(mainWrapper, BorderLayout.CENTER);

        // --- BOTTOM: BUTTONS ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        btnSave = new JButton("Create Account");
        btnSave.setPreferredSize(new Dimension(160, 45)); // Nice big buttons
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnViewAll = new JButton("View All Accounts");
        btnViewAll.setPreferredSize(new Dimension(160, 45));
        btnViewAll.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnSave.addActionListener(e -> saveAccount());
        btnViewAll.addActionListener(e -> openManager());

        btnPanel.add(btnSave);
        btnPanel.add(btnViewAll);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void populateAccountTypes() {
        cmbType.removeAllItems();
        for (EAccountType type : EAccountType.values()) {
            cmbType.addItem(type.toString());
        }
    }

    private void searchCustomer() {
        try {
            if (txtCustId.getText().isEmpty()) return;
            int id = Integer.parseInt(txtCustId.getText());
            selectedCustomer = service.findCustomerById(id);

            if (selectedCustomer != null) {
                lblCustomerName.setText("Owner: " + selectedCustomer.getFirstName() + " " + selectedCustomer.getLastName());
            } else {
                lblCustomerName.setText("Owner: Not Found");
                selectedCustomer = null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void saveAccount() {
        try {
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer first.");
                return;
            }
            Account a = new Account();
            a.setAccountNumber(txtAccountId.getText());
            a.setBalance(Double.parseDouble(txtBalance.getText()));
            a.setType(EAccountType.valueOf(cmbType.getSelectedItem().toString()));
            a.setRegisteredDate(LocalDate.now());
            a.setActive(true);

            Set<Customer> customers = new HashSet<>();
            customers.add(selectedCustomer);
            a.setCustomers(customers);

            service.createAccount(a);
            JOptionPane.showMessageDialog(this, "Account Created!");

            txtAccountId.setText("");
            txtBalance.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void openManager() {
        new AccountManagerForm().setVisible(true);
    }

    public static void main(String[] args) {
        try { for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) if ("Nimbus".equals(info.getName())) UIManager.setLookAndFeel(info.getClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new AccountForm().setVisible(true));
    }
}