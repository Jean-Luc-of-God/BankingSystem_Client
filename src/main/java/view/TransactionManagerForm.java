package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import model.Transaction;
import service.BankService;

public class TransactionManagerForm extends JFrame {

    private BankService service;
    private JTable table;
    private DefaultTableModel model;

    public TransactionManagerForm() {
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
        setTitle("Transaction History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Date", "Amount", "Type", "Status", "Account"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Transaction Logs", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);

        // Refresh Button only (Transactions usually aren't deleted/edited for audit reasons)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnRefresh = new JButton("Refresh Logs");
        btnRefresh.setPreferredSize(new Dimension(150, 40));
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Transaction> list = service.findAllTransactions();
            for (Transaction t : list) {
                String acc = (t.getAccount() != null) ? t.getAccount().getAccountNumber() : "Unknown";
                model.addRow(new Object[]{
                        t.getId(), t.getTransactionDate(), "$" + t.getAmount(),
                        t.getType(), t.getStatus(), acc
                });
            }
        } catch (Exception e) {}
    }
}