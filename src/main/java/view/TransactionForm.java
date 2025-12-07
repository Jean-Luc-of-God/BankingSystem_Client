package view;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import model.Account;
import model.ETransactionStatus;
import model.ETransactionType;
import model.Transaction;
import service.BankService;

public class TransactionForm extends javax.swing.JFrame {

    private BankService service;
    private Account selectedAccount = null;

    public TransactionForm() {
        initComponents();
        connectToServer();
        populateTypes();
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection Error: " + e.getMessage());
        }
    }

    private void populateTypes() {
        cmbType.removeAllItems();
        for (ETransactionType type : ETransactionType.values()) {
            cmbType.addItem(type.toString());
        }
    }

    private void searchAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (txtSearchAcc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Account Number first!");
                return;
            }

            String accId = txtSearchAcc.getText();
            selectedAccount = service.findAccountById(accId);

            if (selectedAccount != null) {
                // ✅ FIX: Get the first customer from the Set (since getCustomer() was removed)
                String owner = "Unknown";
                if (selectedAccount.getCustomers() != null && !selectedAccount.getCustomers().isEmpty()) {
                    owner = selectedAccount.getCustomers().iterator().next().getFirstName();
                }

                lblAccountInfo.setText("Owner: " + owner + " | Balance: $" + selectedAccount.getBalance());
                JOptionPane.showMessageDialog(this, "Account Found!");
            } else {
                lblAccountInfo.setText("Account Not Found");
                JOptionPane.showMessageDialog(this, "Account ID not found.");
                selectedAccount = null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (selectedAccount == null) {
                JOptionPane.showMessageDialog(this, "Please search for an Account first!");
                return;
            }

            Transaction t = new Transaction();
            t.setAmount(Double.parseDouble(txtAmount.getText()));
            t.setTransactionDate(LocalDate.now());

            t.setType(ETransactionType.valueOf(cmbType.getSelectedItem().toString()));
            t.setStatus(ETransactionStatus.SUCCESSFUL);

            t.setAccount(selectedAccount);

            service.createTransaction(t);

            JOptionPane.showMessageDialog(this, "Transaction Processed Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- GUI Setup ---
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtSearchAcc = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        lblAccountInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox<>();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Transaction Entry");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setText("New Transaction");

        jLabel2.setText("Account No:");
        btnSearch.setText("Search");
        btnSearch.addActionListener(evt -> searchAccountButtonActionPerformed(evt));

        lblAccountInfo.setForeground(new java.awt.Color(0, 102, 0));
        lblAccountInfo.setText("Current Balance: ---");

        jLabel3.setText("Amount:");
        jLabel4.setText("Type:");

        btnSave.setText("Process Transaction");
        btnSave.addActionListener(evt -> saveButtonActionPerformed(evt));

        // Layout Code
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(lblAccountInfo)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4))
                                        .addGap(18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(layout.createSequentialGroup().addComponent(txtSearchAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnSearch))
                                                .addComponent(txtAmount).addComponent(cmbType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addGap(20)
                        .addComponent(jLabel1).addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(txtSearchAcc).addComponent(btnSearch))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAccountInfo).addGap(18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3).addComponent(txtAmount))
                        .addGap(10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4).addComponent(cmbType))
                        .addGap(20)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(30, Short.MAX_VALUE))
        );
        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new TransactionForm().setVisible(true));
    }

    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cmbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblAccountInfo;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtSearchAcc;
}