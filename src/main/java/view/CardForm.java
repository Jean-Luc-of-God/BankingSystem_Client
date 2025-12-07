package view;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JOptionPane;
import model.Account;
import model.Card;
import model.ECardType;
import service.BankService;

public class CardForm extends javax.swing.JFrame {

    private BankService service;
    private Account selectedAccount = null;

    public CardForm() {
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
        for (ECardType type : ECardType.values()) {
            cmbType.addItem(type.toString());
        }
    }

    private void searchAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (txtSearchAcc.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Account Number first!");
                return;
            }
            selectedAccount = service.findAccountById(txtSearchAcc.getText());
            if (selectedAccount != null) {
                // ✅ FIX: Handle Set<Customer> correctly
                String ownerName = "Unknown";
                if (selectedAccount.getCustomers() != null && !selectedAccount.getCustomers().isEmpty()) {
                    ownerName = selectedAccount.getCustomers().iterator().next().getFirstName();
                }
                lblAccountInfo.setText("Owner: " + ownerName);
                JOptionPane.showMessageDialog(this, "Account Found!");
            } else {
                lblAccountInfo.setText("Account Not Found");
                JOptionPane.showMessageDialog(this, "Account ID not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (selectedAccount == null) {
                JOptionPane.showMessageDialog(this, "Search for an Account first!");
                return;
            }

            Card c = new Card();
            c.setCardNumber(txtCardNo.getText());
            c.setType(ECardType.valueOf(cmbType.getSelectedItem().toString()));
            c.setPin(txtPin.getText());
            c.setCcv(txtCcv.getText());
            c.setEndMonth(txtMonth.getText());
            c.setEndYear(txtYear.getText());
            c.setActive(true);
            c.setAccount(selectedAccount);

            service.createCard(c);
            JOptionPane.showMessageDialog(this, "Card Issued Successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtSearchAcc = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        lblAccountInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCardNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbType = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtPin = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        txtCcv = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtMonth = new javax.swing.JTextField();
        txtYear = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Card Management");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setText("Issue New Card");

        jLabel2.setText("Account No:");
        btnSearch.setText("Search");
        btnSearch.addActionListener(evt -> searchAccountButtonActionPerformed(evt));

        lblAccountInfo.setForeground(new java.awt.Color(0, 102, 0));
        lblAccountInfo.setText("Owner: ---");

        jLabel3.setText("Card Number:");
        jLabel4.setText("Type:");
        jLabel5.setText("PIN:");
        jLabel6.setText("CCV:");
        jLabel7.setText("Expiry (MM/YY):");

        btnSave.setText("Issue Card");
        btnSave.addActionListener(evt -> saveButtonActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addGap(30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(lblAccountInfo)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4).addComponent(jLabel5).addComponent(jLabel7))
                                        .addGap(18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(layout.createSequentialGroup().addComponent(txtSearchAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnSearch))
                                                .addComponent(txtCardNo).addComponent(cmbType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(txtPin)
                                                .addGroup(layout.createSequentialGroup().addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(jLabel6).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtCcv, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addGap(20)
                        .addComponent(jLabel1).addGap(20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(txtSearchAcc).addComponent(btnSearch))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAccountInfo).addGap(18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3).addComponent(txtCardNo))
                        .addGap(10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4).addComponent(cmbType))
                        .addGap(10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel5).addComponent(txtPin))
                        .addGap(10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel7).addComponent(txtMonth).addComponent(txtYear).addComponent(jLabel6).addComponent(txtCcv))
                        .addGap(20)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(30, Short.MAX_VALUE))
        );
        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new CardForm().setVisible(true));
    }

    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cmbType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblAccountInfo;
    private javax.swing.JTextField txtCcv;
    private javax.swing.JTextField txtCardNo;
    private javax.swing.JTextField txtMonth;
    private javax.swing.JPasswordField txtPin;
    private javax.swing.JTextField txtSearchAcc;
    private javax.swing.JTextField txtYear;
}