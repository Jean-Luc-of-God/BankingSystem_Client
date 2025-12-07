package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.Account;
import model.ETransactionStatus;
import model.ETransactionType;
import model.Transaction;
import service.BankService;

public class TransactionForm extends JFrame {

    private BankService service;
    private Account selectedAccount = null;

    private JTextField txtSearchAcc, txtAmount;
    private JLabel lblAccountInfo;
    private JComboBox<String> cmbType;
    private JButton btnSearch, btnSave, btnViewAll;

    public TransactionForm() {
        setupConnection();
        initComponents();
        populateTypes();
    }

    private void setupConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) { }
    }

    private void initComponents() {
        setTitle("New Transaction");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainWrapper = new JPanel(new GridBagLayout());
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(new TitledBorder("Transaction Details"));
        contentPanel.setPreferredSize(new Dimension(500, 250));

        txtSearchAcc = new JTextField();
        btnSearch = new JButton("Find");
        btnSearch.addActionListener(e -> searchAccount());

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(txtSearchAcc, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        lblAccountInfo = new JLabel("Balance: ---");
        lblAccountInfo.setForeground(new Color(0, 100, 0));
        lblAccountInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtAmount = new JTextField();
        cmbType = new JComboBox<>();

        contentPanel.add(new JLabel("Account No:"));
        contentPanel.add(searchPanel);

        contentPanel.add(new JLabel(""));
        contentPanel.add(lblAccountInfo);

        contentPanel.add(new JLabel("Amount:"));
        contentPanel.add(txtAmount);

        contentPanel.add(new JLabel("Type:"));
        contentPanel.add(cmbType);

        mainWrapper.add(contentPanel);
        add(mainWrapper, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnSave = new JButton("Process Transaction");
        btnSave.setPreferredSize(new Dimension(170, 45));

        btnViewAll = new JButton("History Log");
        btnViewAll.setPreferredSize(new Dimension(170, 45));

        btnSave.addActionListener(e -> saveTransaction());
        btnViewAll.addActionListener(e -> new TransactionManagerForm().setVisible(true));

        btnPanel.add(btnSave);
        btnPanel.add(btnViewAll);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void populateTypes() {
        cmbType.removeAllItems();
        for (ETransactionType t : ETransactionType.values()) cmbType.addItem(t.toString());
    }

    private void searchAccount() {
        try {
            if (txtSearchAcc.getText().isEmpty()) return;
            selectedAccount = service.findAccountById(txtSearchAcc.getText());
            if (selectedAccount != null) {
                lblAccountInfo.setText("Current Balance: $" + selectedAccount.getBalance());
            } else {
                lblAccountInfo.setText("Not Found");
                selectedAccount = null;
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void saveTransaction() {
        try {
            if (selectedAccount == null) { JOptionPane.showMessageDialog(this, "Find account first"); return; }

            Transaction t = new Transaction();
            t.setAmount(Double.parseDouble(txtAmount.getText()));
            t.setTransactionDate(LocalDate.now());
            t.setType(ETransactionType.valueOf(cmbType.getSelectedItem().toString()));
            t.setStatus(ETransactionStatus.SUCCESSFUL);
            t.setAccount(selectedAccount);

            service.createTransaction(t);
            JOptionPane.showMessageDialog(this, "Success!");
            txtAmount.setText("");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    public static void main(String[] args) {
        try { for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) if ("Nimbus".equals(info.getName())) UIManager.setLookAndFeel(info.getClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new TransactionForm().setVisible(true));
    }
}