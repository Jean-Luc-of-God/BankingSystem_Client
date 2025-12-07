package model;

/**
 *
 * @author JeanLuc
 */


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate createdDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double amountToPay;
    private ELoanStatus status;
    private Double interestRate;
    private Double amountToReceive;
    private Double monthlyDeduction;


    private Set<Account> accounts;

    public Loan() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Double getAmountToPay() { return amountToPay; }
    public void setAmountToPay(Double amountToPay) { this.amountToPay = amountToPay; }

    public ELoanStatus getStatus() { return status; }
    public void setStatus(ELoanStatus status) { this.status = status; }

    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }

    public Double getAmountToReceive() { return amountToReceive; }
    public void setAmountToReceive(Double amountToReceive) { this.amountToReceive = amountToReceive; }

    public Double getMonthlyDeduction() { return monthlyDeduction; }
    public void setMonthlyDeduction(Double monthlyDeduction) { this.monthlyDeduction = monthlyDeduction; }

    public Set<Account> getAccounts() { return accounts; }
    public void setAccounts(Set<Account> accounts) { this.accounts = accounts; }
}