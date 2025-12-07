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
import model.Card;
import model.ECardType;
import service.BankService;

public class CardManagerForm extends JFrame {

    private BankService service;
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtCardNo;
    private JComboBox<String> cmbType;
    private JCheckBox chkActive;
    private String selectedCardId = null;

    public CardManagerForm() {
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
        setTitle("Manage Cards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {"Card No", "Type", "Expiry", "Status", "Account"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedCardId = (String) model.getValueAt(row, 0);
                    txtCardNo.setText(selectedCardId);
                    cmbType.setSelectedItem(model.getValueAt(row, 1).toString());
                    chkActive.setSelected((Boolean) model.getValueAt(row, 3));
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtCardNo = new JTextField(); txtCardNo.setEditable(false);
        cmbType = new JComboBox<>();
        for (ECardType t : ECardType.values()) cmbType.addItem(t.toString());
        chkActive = new JCheckBox("Active Card");

        formPanel.add(new JLabel("Card No:")); formPanel.add(txtCardNo);
        formPanel.add(new JLabel("Type:")); formPanel.add(cmbType);
        formPanel.add(new JLabel("Status:")); formPanel.add(chkActive);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnUpdate.setPreferredSize(new Dimension(130, 40));
        btnDelete.setPreferredSize(new Dimension(130, 40));
        btnDelete.setForeground(Color.RED);
        btnRefresh.setPreferredSize(new Dimension(130, 40));

        btnUpdate.addActionListener(e -> updateCard());
        btnDelete.addActionListener(e -> deleteCard());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnUpdate); btnPanel.add(btnDelete); btnPanel.add(btnRefresh);
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Card> list = service.findAllCards();
            for (Card c : list) {
                String acc = (c.getAccount() != null) ? c.getAccount().getAccountNumber() : "Unknown";
                String expiry = c.getEndMonth() + "/" + c.getEndYear();
                model.addRow(new Object[]{ c.getCardNumber(), c.getType(), expiry, c.isActive(), acc });
            }
        } catch (Exception e) {}
    }

    private void updateCard() {
        if (selectedCardId == null) return;
        try {
            Card c = service.findCardById(selectedCardId);
            c.setType(ECardType.valueOf(cmbType.getSelectedItem().toString()));
            c.setActive(chkActive.isSelected());
            service.updateCard(c);
            JOptionPane.showMessageDialog(this, "Updated!");
            loadData();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteCard() {
        if (selectedCardId == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                Card c = service.findCardById(selectedCardId);
                service.deleteCard(c);
                loadData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}