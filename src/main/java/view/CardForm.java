package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.Account;
import model.Card;
import model.ECardType;
import service.BankService;

public class CardForm extends JFrame {

    private BankService service;
    private Account selectedAccount = null;

    private JTextField txtSearchAcc, txtCardNo, txtCcv, txtMonth, txtYear;
    private JPasswordField txtPin;
    private JLabel lblAccountInfo;
    private JComboBox<String> cmbType;
    private JButton btnSearch, btnSave, btnViewAll;

    public CardForm() {
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
        setTitle("Issue New Card");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainWrapper = new JPanel(new GridBagLayout());
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        contentPanel.setBorder(new TitledBorder("Card Details"));
        contentPanel.setPreferredSize(new Dimension(500, 350));

        txtSearchAcc = new JTextField();
        btnSearch = new JButton("Find");
        btnSearch.addActionListener(e -> searchAccount());

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(txtSearchAcc, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        lblAccountInfo = new JLabel("Owner: (None)");
        lblAccountInfo.setForeground(new Color(0, 100, 0));
        lblAccountInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtCardNo = new JTextField();
        cmbType = new JComboBox<>();
        txtPin = new JPasswordField();
        txtCcv = new JTextField();

        JPanel datePanel = new JPanel(new GridLayout(1, 2, 5, 0));
        txtMonth = new JTextField(); txtMonth.setToolTipText("MM");
        txtYear = new JTextField(); txtYear.setToolTipText("YY");
        datePanel.add(txtMonth); datePanel.add(txtYear);

        contentPanel.add(new JLabel("Account No:"));
        contentPanel.add(searchPanel);

        contentPanel.add(new JLabel(""));
        contentPanel.add(lblAccountInfo);

        contentPanel.add(new JLabel("Card Number:"));
        contentPanel.add(txtCardNo);

        contentPanel.add(new JLabel("Card Type:"));
        contentPanel.add(cmbType);

        contentPanel.add(new JLabel("PIN:"));
        contentPanel.add(txtPin);

        contentPanel.add(new JLabel("Expiry (MM / YY):"));
        contentPanel.add(datePanel);

        contentPanel.add(new JLabel("CCV:"));
        contentPanel.add(txtCcv);

        mainWrapper.add(contentPanel);
        add(mainWrapper, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnSave = new JButton("Issue Card");
        btnSave.setPreferredSize(new Dimension(160, 45));

        btnViewAll = new JButton("Manage Cards");
        btnViewAll.setPreferredSize(new Dimension(160, 45));

        btnSave.addActionListener(e -> saveCard());
        btnViewAll.addActionListener(e -> new CardManagerForm().setVisible(true));

        btnPanel.add(btnSave);
        btnPanel.add(btnViewAll);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void populateTypes() {
        cmbType.removeAllItems();
        for (ECardType t : ECardType.values()) cmbType.addItem(t.toString());
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

    private void saveCard() {
        try {
            if (selectedAccount == null) { JOptionPane.showMessageDialog(this, "Find account first"); return; }

            Card c = new Card();
            c.setCardNumber(txtCardNo.getText());
            c.setType(ECardType.valueOf(cmbType.getSelectedItem().toString()));
            c.setPin(new String(txtPin.getPassword()));
            c.setCcv(txtCcv.getText());
            c.setEndMonth(txtMonth.getText());
            c.setEndYear(txtYear.getText());
            c.setActive(true);
            c.setAccount(selectedAccount);

            service.createCard(c);
            JOptionPane.showMessageDialog(this, "Card Issued!");
            txtCardNo.setText("");
            txtPin.setText("");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    public static void main(String[] args) {
        try { for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) if ("Nimbus".equals(info.getName())) UIManager.setLookAndFeel(info.getClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new CardForm().setVisible(true));
    }
}