package model;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.*;

/**
 *
 * @author JeanLuc
 */

@Entity
@Table(name = "loan_repayment")
public class LoanRepayment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    private Double paymentAmount;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    public LoanRepayment() {}


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public Double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(Double paymentAmount) { this.paymentAmount = paymentAmount; }

    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }
}