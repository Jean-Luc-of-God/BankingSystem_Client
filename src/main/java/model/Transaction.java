package model;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.*;

/**
 *
 * @author JeanLuc
 */

@Entity
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Enumerated(EnumType.STRING)
    private ETransactionType type;

    private Double amount;
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private ETransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public ETransactionType getType() { return type; }
    public void setType(ETransactionType type) { this.type = type; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public ETransactionStatus getStatus() { return status; }
    public void setStatus(ETransactionStatus status) { this.status = status; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}