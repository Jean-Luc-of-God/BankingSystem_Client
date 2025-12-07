package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import model.Customer;
import service.BankService;

public class CustomerManagerForm extends JFrame {

    private BankService service;
    private JTable table;
    private DefaultTableModel model;

    // Edit Fields
    private JTextField txtFirst, txtLast, txtPhone, txtEmail, txtAddress;
    private int selectedId = -1;

    public CustomerManagerForm() {
        setupService();
        initUI();
        loadData();
    }

    private void setupService() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100);
            service = (BankService) registry.lookup("BankingService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Customer List");
        setSize(800, 500);
        setLocationRelativeTo(null);
        // Dispose allows closing this window without killing the whole app
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- TOP: Table ---
        String[] columns = {"ID", "First Name", "Last Name", "Phone", "Email", "Address"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Click Event to fill boxes
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedId = (int) model.getValueAt(row, 0);
                    txtFirst.setText(model.getValueAt(row, 1).toString());
                    txtLast.setText(model.getValueAt(row, 2).toString());
                    txtPhone.setText(model.getValueAt(row, 3).toString());
                    txtEmail.setText(model.getValueAt(row, 4).toString());
                    txtAddress.setText(model.getValueAt(row, 5).toString());
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BOTTOM: Edit Panel ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        JPanel fieldsPanel = new JPanel(new GridLayout(2, 5));
        txtFirst = new JTextField();
        txtLast = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();

        fieldsPanel.add(new JLabel("First Name"));
        fieldsPanel.add(new JLabel("Last Name"));
        fieldsPanel.add(new JLabel("Phone"));
        fieldsPanel.add(new JLabel("Email"));
        fieldsPanel.add(new JLabel("Address"));

        fieldsPanel.add(txtFirst);
        fieldsPanel.add(txtLast);
        fieldsPanel.add(txtPhone);
        fieldsPanel.add(txtEmail);
        fieldsPanel.add(txtAddress);

        JPanel btnPanel = new JPanel();
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnRefresh.addActionListener(e -> loadData());

        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        bottomPanel.add(fieldsPanel);
        bottomPanel.add(btnPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Customer> list = service.findAllCustomers();
            for (Customer c : list) {
                model.addRow(new Object[]{
                        c.getId(), c.getFirstName(), c.getLastName(),
                        c.getPhoneNumber(), c.getEmail(), c.getAddress()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data");
        }
    }

    private void updateCustomer() {
        if (selectedId == -1) return;
        try {
            Customer c = service.findCustomerById(selectedId);
            c.setFirstName(txtFirst.getText());
            c.setLastName(txtLast.getText());
            c.setPhoneNumber(txtPhone.getText());
            c.setEmail(txtEmail.getText());
            c.setAddress(txtAddress.getText());

            service.updateCustomer(c);
            JOptionPane.showMessageDialog(this, "Updated!");
            loadData();
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }

    private void deleteCustomer() {
        if (selectedId == -1) return;
        int opt = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                Customer c = service.findCustomerById(selectedId);
                service.deleteCustomer(c);
                loadData();
                clearFields();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        txtFirst.setText(""); txtLast.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddress.setText("");
        selectedId = -1;
    }

    // Main method removed/commented because this form is now opened by CustomerForm
    /*
    public static void main(String[] args) {
        new CustomerManagerForm().setVisible(true);
    }
    */
}