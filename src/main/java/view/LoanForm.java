package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.Account;
import model.ELoanStatus;
import model.Loan;
import service.BankService;

public class LoanForm extends JFrame {

    private BankService service;
    private Account selectedAccount = null;

    private JTextField txtSearchAcc, txtAmount, txtDuration;
    private JLabel lblAccountInfo;
    private JButton btnSearch, btnSave, btnViewAll;

    public LoanForm() {
        setupConnection();
        initComponents();
    }

    private void setupConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) { }
    }

    private void initComponents() {
        setTitle("Apply for Loan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Center Form
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtSearchAcc = new JTextField();
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchAccount());

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(txtSearchAcc, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        lblAccountInfo = new JLabel("Owner: (None)");
        lblAccountInfo.setForeground(new Color(0, 100, 0));
        lblAccountInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtAmount = new JTextField();
        txtDuration = new JTextField();

        formPanel.add(new JLabel("Account No:"));
        formPanel.add(searchPanel);

        formPanel.add(new JLabel(""));
        formPanel.add(lblAccountInfo);

        formPanel.add(new JLabel("Loan Amount:"));
        formPanel.add(txtAmount);

        formPanel.add(new JLabel("Duration (Months):"));
        formPanel.add(txtDuration);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSave = new JButton("Submit Application");
        btnSave.setPreferredSize(new Dimension(150, 35));

        btnViewAll = new JButton("Manage Loans");
        btnViewAll.setPreferredSize(new Dimension(150, 35));

        btnSave.addActionListener(e -> saveLoan());
        btnViewAll.addActionListener(e -> new LoanManagerForm().setVisible(true));

        btnPanel.add(btnSave);
        btnPanel.add(btnViewAll);

        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void searchAccount() {
        try {
            if (txtSearchAcc.getText().isEmpty()) return;
            selectedAccount = service.findAccountById(txtSearchAcc.getText());
            if (selectedAccount != null) {
                String owner = "Unknown";
                if (selectedAccount.getCustomers() != null && !selectedAccount.getCustomers().isEmpty()) {
                    owner = selectedAccount.getCustomers().iterator().next().getFirstName();
                }
                lblAccountInfo.setText("Owner: " + owner);
            } else {
                lblAccountInfo.setText("Not Found");
                selectedAccount = null;
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void saveLoan() {
        try {
            if (selectedAccount == null) { JOptionPane.showMessageDialog(this, "Find account first"); return; }

            Loan l = new Loan();
            double amt = Double.parseDouble(txtAmount.getText());
            l.setAmountToReceive(amt);
            l.setAmountToPay(amt * 1.15);
            l.setInterestRate(0.15);
            l.setMonthlyDeduction(amt / 12);

            int months = Integer.parseInt(txtDuration.getText());
            LocalDate today = LocalDate.now();
            l.setCreatedDate(today);
            l.setStartDate(today);
            l.setEndDate(today.plusMonths(months));
            l.setStatus(ELoanStatus.INITIATED);

            Set<Account> accs = new HashSet<>();
            accs.add(selectedAccount);
            l.setAccounts(accs);

            service.createLoan(l);
            JOptionPane.showMessageDialog(this, "Loan Submitted!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    public static void main(String[] args) {
        try { for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) if ("Nimbus".equals(info.getName())) UIManager.setLookAndFeel(info.getClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoanForm().setVisible(true));
    }
}