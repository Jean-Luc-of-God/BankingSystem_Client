package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import model.Account;
import model.EAccountType;
import service.BankService;

public class AccountManagerForm extends JFrame {

    private BankService service;
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtAccNum, txtBalance;
    private JComboBox<String> cmbType;
    private JCheckBox chkActive;
    private String selectedAccId = null;

    public AccountManagerForm() {
        setupConnection();
        initComponents();
        loadData();
    }

    private void setupConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) { }
    }

    private void initComponents() {
        setTitle("Manage Accounts");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP: Table ---
        String[] columns = {"Acc No", "Owner", "Balance", "Type", "Active"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedAccId = (String) model.getValueAt(row, 0);
                    txtAccNum.setText(selectedAccId);
                    txtBalance.setText(model.getValueAt(row, 2).toString().replace("$", ""));
                    cmbType.setSelectedItem(model.getValueAt(row, 3).toString());
                    chkActive.setSelected((Boolean) model.getValueAt(row, 4));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 250));
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM: Editors ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        fieldsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtAccNum = new JTextField(); txtAccNum.setEditable(false);
        txtBalance = new JTextField();
        cmbType = new JComboBox<>();
        for (EAccountType t : EAccountType.values()) cmbType.addItem(t.toString());
        chkActive = new JCheckBox("Active");

        fieldsPanel.add(new JLabel("Acc No:")); fieldsPanel.add(txtAccNum);
        fieldsPanel.add(new JLabel("Balance:")); fieldsPanel.add(txtBalance);
        fieldsPanel.add(new JLabel("Type:")); fieldsPanel.add(cmbType);
        fieldsPanel.add(new JLabel("Status:")); fieldsPanel.add(chkActive);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnUpdate.addActionListener(e -> updateAccount());
        btnDelete.addActionListener(e -> deleteAccount());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        bottomPanel.add(fieldsPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Account> list = service.findAllAccounts();
            for (Account a : list) {
                String owner = "Unknown";
                if (a.getCustomers() != null && !a.getCustomers().isEmpty()) {
                    owner = a.getCustomers().iterator().next().getFirstName();
                }
                model.addRow(new Object[]{ a.getAccountNumber(), owner, "$" + a.getBalance(), a.getType(), a.isActive() });
            }
        } catch (Exception e) {}
    }

    private void updateAccount() {
        if (selectedAccId == null) return;
        try {
            Account a = service.findAccountById(selectedAccId);
            a.setBalance(Double.parseDouble(txtBalance.getText()));
            a.setType(EAccountType.valueOf(cmbType.getSelectedItem().toString()));
            a.setActive(chkActive.isSelected());
            service.updateAccount(a);
            JOptionPane.showMessageDialog(this, "Updated!");
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteAccount() {
        if (selectedAccId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Account a = service.findAccountById(selectedAccId);
                service.deleteAccount(a);
                loadData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}