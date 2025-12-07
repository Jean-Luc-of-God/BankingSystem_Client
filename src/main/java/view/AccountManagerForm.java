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
        setSize(1000, 700); // Fixed Startup Size
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP: Table ---
        String[] columns = {"Acc No", "Owner", "Balance", "Type", "Active"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25); // Make rows easier to read
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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
        // Add a title above table
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Account Registry", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);

        // --- BOTTOM: Editors ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 4, 15, 15)); // Good spacing
        fieldsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtAccNum = new JTextField(); txtAccNum.setEditable(false);
        txtBalance = new JTextField();
        cmbType = new JComboBox<>();
        for (EAccountType t : EAccountType.values()) cmbType.addItem(t.toString());
        chkActive = new JCheckBox("Active Account");

        fieldsPanel.add(new JLabel("Acc No (Locked):")); fieldsPanel.add(txtAccNum);
        fieldsPanel.add(new JLabel("Balance:")); fieldsPanel.add(txtBalance);
        fieldsPanel.add(new JLabel("Type:")); fieldsPanel.add(cmbType);
        fieldsPanel.add(new JLabel("Status:")); fieldsPanel.add(chkActive);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton btnUpdate = new JButton("Update Account");
        JButton btnDelete = new JButton("Delete Account");
        JButton btnRefresh = new JButton("Refresh List");

        btnUpdate.setPreferredSize(new Dimension(140, 40));
        btnDelete.setPreferredSize(new Dimension(140, 40));
        btnDelete.setForeground(Color.RED);
        btnRefresh.setPreferredSize(new Dimension(140, 40));

        btnUpdate.addActionListener(e -> updateAccount());
        btnDelete.addActionListener(e -> deleteAccount());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        bottomPanel.add(fieldsPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
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