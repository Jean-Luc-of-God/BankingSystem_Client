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
import model.ELoanStatus;
import model.Loan;
import service.BankService;

public class LoanManagerForm extends JFrame {

    private BankService service;
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtId, txtAmount;
    private JComboBox<String> cmbStatus;
    private int selectedLoanId = -1;

    public LoanManagerForm() {
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
        setTitle("Manage Loans");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP: Table ---
        String[] columns = {"ID", "Amount", "Status", "Account"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedLoanId = (int) model.getValueAt(row, 0);
                    txtId.setText(String.valueOf(selectedLoanId));
                    txtAmount.setText(model.getValueAt(row, 1).toString().replace("$", ""));
                    cmbStatus.setSelectedItem(model.getValueAt(row, 2).toString());
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BOTTOM: Editors ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtId = new JTextField(); txtId.setEditable(false);
        txtAmount = new JTextField();
        cmbStatus = new JComboBox<>();
        for (ELoanStatus s : ELoanStatus.values()) cmbStatus.addItem(s.toString());

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Amount:")); formPanel.add(txtAmount);
        formPanel.add(new JLabel("Status:")); formPanel.add(cmbStatus);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnUpdate.addActionListener(e -> updateLoan());
        btnDelete.addActionListener(e -> deleteLoan());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);

        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Loan> list = service.findAllLoans();
            for (Loan l : list) {
                String accNum = (l.getAccounts() != null && !l.getAccounts().isEmpty())
                        ? l.getAccounts().iterator().next().getAccountNumber()
                        : "Unknown";
                model.addRow(new Object[]{ l.getId(), "$" + l.getAmountToReceive(), l.getStatus(), accNum });
            }
        } catch (Exception e) {}
    }

    private void updateLoan() {
        if (selectedLoanId == -1) return;
        try {
            Loan l = service.findLoanById(selectedLoanId);
            l.setAmountToReceive(Double.parseDouble(txtAmount.getText()));
            l.setStatus(ELoanStatus.valueOf(cmbStatus.getSelectedItem().toString()));
            service.updateLoan(l);
            JOptionPane.showMessageDialog(this, "Updated!");
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteLoan() {
        if (selectedLoanId == -1) return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Loan l = service.findLoanById(selectedLoanId);
                service.deleteLoan(l);
                loadData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}