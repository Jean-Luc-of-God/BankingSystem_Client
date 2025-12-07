package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private double balance;
    private LocalDate registeredDate;
    private boolean active;
    private EAccountType type;


    private Set<Customer> customers;

    public Account() {}


    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public EAccountType getType() { return type; }
    public void setType(EAccountType type) { this.type = type; }

    public Set<Customer> getCustomers() { return customers; }
    public void setCustomers(Set<Customer> customers) { this.customers = customers; }
}